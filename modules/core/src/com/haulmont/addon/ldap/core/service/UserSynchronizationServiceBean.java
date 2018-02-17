package com.haulmont.addon.ldap.core.service;

import com.haulmont.addon.ldap.core.dao.CubaUserDao;
import com.haulmont.addon.ldap.core.dao.LdapUserDao;
import com.haulmont.addon.ldap.core.dao.MatchingRuleDao;
import com.haulmont.addon.ldap.core.dto.LdapUserWrapper;
import com.haulmont.addon.ldap.core.rule.ApplyMatchingRuleContext;
import com.haulmont.addon.ldap.core.rule.MatchingRuleApplierInitializer;
import com.haulmont.addon.ldap.core.rule.programmatic.CustomLdapMatchingRule;
import com.haulmont.addon.ldap.core.spring.events.*;
import com.haulmont.addon.ldap.dto.TestUserSynchronizationDto;
import com.haulmont.addon.ldap.entity.AbstractMatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRule;
import com.haulmont.addon.ldap.service.UserSynchronizationService;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserRole;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service(UserSynchronizationService.NAME)
public class UserSynchronizationServiceBean implements UserSynchronizationService {

    @Inject
    @Qualifier(LdapUserDao.NAME)
    private LdapUserDao ldapUserDao;

    @Inject
    @Qualifier(CubaUserDao.NAME)
    private CubaUserDao cubaUserDao;

    @Inject
    @Qualifier(MatchingRuleDao.NAME)
    private MatchingRuleDao matchingRuleDao;

    @Inject
    @Qualifier(MatchingRuleApplierInitializer.NAME)
    private MatchingRuleApplierInitializer matchingRuleApplierInitializer;

    @Inject
    private Metadata metadata;

    @Inject
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void synchronizeUser(String login) {
        LdapUserWrapper ldapUserWrapper = ldapUserDao.getLdapUserWrapper(login);
        if (ldapUserWrapper != null) {
            User cubaUser = cubaUserDao.getCubaUserByLogin(login);
            ApplyMatchingRuleContext applyMatchingRuleContext = new ApplyMatchingRuleContext(ldapUserWrapper.getLdapUser(), ldapUserWrapper.getLdapUserAttributes(), cubaUser);
            setCommonAttributesFromLdapUser(applyMatchingRuleContext, cubaUser, login);
            List<MatchingRule> matchingRules = matchingRuleDao.getMatchingRules();
            List<UserRole> originalUserRoles = new ArrayList<>(cubaUser.getUserRoles());
            applicationEventPublisher.publishEvent(new BeforeUserUpdatedFromLdapEvent(this, applyMatchingRuleContext, cubaUser));
            matchingRuleApplierInitializer.getMatchingRuleChain().applyMatchingRules(matchingRules, applyMatchingRuleContext, cubaUser);
            applicationEventPublisher.publishEvent(new AfterUserUpdatedFromLdapEvent(this, applyMatchingRuleContext, cubaUser));
            cubaUserDao.saveCubaUser(cubaUser, originalUserRoles);
        }
    }

    private void setCommonAttributesFromLdapUser(ApplyMatchingRuleContext applyMatchingRuleContext, User cubaUser, String login) {
        if (PersistenceHelper.isNew(cubaUser)) {//only for new users
            applicationEventPublisher.publishEvent(new BeforeNewUserCreatedFromLdapEvent(this, applyMatchingRuleContext, cubaUser));
            cubaUser.setLogin(login);
            cubaUser.setUserRoles(new ArrayList<>());
        }
        cubaUser.setEmail(applyMatchingRuleContext.getLdapUser().getEmail());
        cubaUser.setName(applyMatchingRuleContext.getLdapUser().getCn());
        cubaUser.setLastName(applyMatchingRuleContext.getLdapUser().getSn());
        cubaUser.setPosition(applyMatchingRuleContext.getLdapUser().getPosition());
        cubaUser.setLanguage(applyMatchingRuleContext.getLdapUser().getLanguage());

        Boolean userDisabled = applyMatchingRuleContext.getLdapUser().getDisabled();
        if (userDisabled) {
            applicationEventPublisher.publishEvent(new BeforeUserDeactivatedFromLdapEvent(this, applyMatchingRuleContext, cubaUser));
            cubaUser.setActive(false);
            applicationEventPublisher.publishEvent(new AfterUserDeactivatedFromLdapEvent(this, applyMatchingRuleContext, cubaUser));
        } else {
            cubaUser.setActive(true);
        }

        if (PersistenceHelper.isNew(cubaUser)) {//only for new users
            applicationEventPublisher.publishEvent(new AfterNewUserCreatedFromLdapEvent(this, applyMatchingRuleContext, cubaUser));
        }
    }

    @Override
    public TestUserSynchronizationDto testUserSynchronization(String login) {
        TestUserSynchronizationDto testUserSynchronizationDto = new TestUserSynchronizationDto();
        LdapUserWrapper ldapUserWrapper = ldapUserDao.getLdapUserWrapper(login);
        if (ldapUserWrapper != null) {
            User cubaUser = cubaUserDao.getCubaUserByLogin(login);
            ApplyMatchingRuleContext applyMatchingRuleContext = new ApplyMatchingRuleContext(ldapUserWrapper.getLdapUser(), ldapUserWrapper.getLdapUserAttributes(), cubaUser);
            if (cubaUser.getUserRoles() != null) {
                cubaUser.getUserRoles().clear();
            } else {
                cubaUser.setUserRoles(new ArrayList<>());
            }
            List<MatchingRule> matchingRules = matchingRuleDao.getMatchingRules();
            matchingRuleApplierInitializer.getMatchingRuleChain().applyMatchingRules(matchingRules, applyMatchingRuleContext, cubaUser);
            applyMatchingRuleContext.getAppliedRules().forEach(matchingRule -> {
                if (matchingRule instanceof CustomLdapMatchingRule) {
                    CustomLdapMatchingRule pmr = (CustomLdapMatchingRule) matchingRule;
                    testUserSynchronizationDto.getAppliedMatchingRules().add(matchingRuleDao.mapProgrammaticRule(pmr));
                } else {
                    testUserSynchronizationDto.getAppliedMatchingRules().add((AbstractMatchingRule) matchingRule);
                }
            });

            testUserSynchronizationDto.getAppliedCubaRoles().addAll(cubaUser.getUserRoles().stream().map(UserRole::getRole).collect(Collectors.toList()));
        }

        return testUserSynchronizationDto;
    }
}
