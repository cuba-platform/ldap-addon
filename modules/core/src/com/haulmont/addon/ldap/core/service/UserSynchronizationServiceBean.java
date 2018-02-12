package com.haulmont.addon.ldap.core.service;

import com.haulmont.addon.ldap.core.dao.CubaUserDao;
import com.haulmont.addon.ldap.core.dao.LdapUserDao;
import com.haulmont.addon.ldap.core.dao.MatchingRuleDao;
import com.haulmont.addon.ldap.core.rule.ApplyMatchingRuleContext;
import com.haulmont.addon.ldap.core.rule.MatchingRuleApplierInitializer;
import com.haulmont.addon.ldap.core.rule.programmatic.LdapProgrammaticMatchingRule;
import com.haulmont.addon.ldap.core.spring.events.*;
import com.haulmont.addon.ldap.dto.TestUserSynchronizationDto;
import com.haulmont.addon.ldap.entity.AbstractMatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRule;
import com.haulmont.addon.ldap.service.UserSynchronizationService;
import com.haulmont.cuba.core.global.Metadata;
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
        ApplyMatchingRuleContext ldapUser = ldapUserDao.getLdapUserWrapper(login);
        if (ldapUser != null) {
            User tmp = cubaUserDao.getCubaUserByLogin(login);
            User cubaUser = setCommonAttributesFromLdapUser(ldapUser, tmp, login);
            List<MatchingRule> matchingRules = matchingRuleDao.getMatchingRules();
            List<UserRole> originalUserRoles = new ArrayList<>(cubaUser.getUserRoles());
            applicationEventPublisher.publishEvent(new BeforeUserUpdatedFromLdapEvent(this, ldapUser, cubaUser));
            matchingRuleApplierInitializer.getMatchingRuleChain().applyMatchingRules(matchingRules, ldapUser, cubaUser);
            applicationEventPublisher.publishEvent(new AfterUserUpdatedFromLdapEvent(this, ldapUser, cubaUser));
            cubaUserDao.saveCubaUser(cubaUser, originalUserRoles);
        }
    }

    private User setCommonAttributesFromLdapUser(ApplyMatchingRuleContext applyMatchingRuleContext, User cubaUser, String login) {
        //TODO: position, language
        User cu = cubaUser == null ? metadata.create(User.class) : cubaUser;
        if (cubaUser == null) {//only for new users
            applicationEventPublisher.publishEvent(new BeforeNewUserCreatedFromLdapEvent(this, applyMatchingRuleContext, cu));
            cu.setLogin(login);
            cu.setUserRoles(new ArrayList<>());
        }
        cu.setEmail(applyMatchingRuleContext.getLdapUser().getEmail());
        cu.setName(applyMatchingRuleContext.getLdapUser().getCn());
        cu.setLastName(applyMatchingRuleContext.getLdapUser().getSn());
        cu.setPosition(applyMatchingRuleContext.getLdapUser().getPosition());
        cu.setLanguage(applyMatchingRuleContext.getLdapUser().getLanguage());

        Boolean userDisabled = applyMatchingRuleContext.getLdapUser().getDisabled();
        if (userDisabled) {
            applicationEventPublisher.publishEvent(new BeforeUserDeactivatedFromLdapEvent(this, applyMatchingRuleContext, cu));
            cu.setActive(false);
            applicationEventPublisher.publishEvent(new AfterUserDeactivatedFromLdapEvent(this, applyMatchingRuleContext, cu));
        } else {
            cu.setActive(true);
        }

        if (cubaUser == null) {//only for new users
            applicationEventPublisher.publishEvent(new AfterNewUserCreatedFromLdapEvent(this, applyMatchingRuleContext, cu));
        }
        return cu;
    }

    @Override
    public TestUserSynchronizationDto testUserSynchronization(String login) {
        TestUserSynchronizationDto testUserSynchronizationDto = new TestUserSynchronizationDto();
        ApplyMatchingRuleContext ldapUser = ldapUserDao.getLdapUserWrapper(login);
        if (ldapUser != null) {
            User cubaUser = cubaUserDao.getCubaUserByLogin(login);
            cubaUser = cubaUser == null ? metadata.create(User.class) : cubaUser;
            if (cubaUser.getUserRoles() != null) {
                cubaUser.getUserRoles().clear();
            } else {
                cubaUser.setUserRoles(new ArrayList<>());
            }
            List<MatchingRule> matchingRules = matchingRuleDao.getMatchingRules();
            matchingRuleApplierInitializer.getMatchingRuleChain().applyMatchingRules(matchingRules, ldapUser, cubaUser);
            ldapUser.getAppliedRules().forEach(matchingRule -> {
                if (matchingRule instanceof LdapProgrammaticMatchingRule) {
                    LdapProgrammaticMatchingRule pmr = (LdapProgrammaticMatchingRule) matchingRule;
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
