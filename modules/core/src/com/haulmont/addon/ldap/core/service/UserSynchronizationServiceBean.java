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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.haulmont.addon.ldap.entity.MatchingRuleType.CUSTOM;

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
            cubaUser.getUserRoles().clear();//user get roles only from LDAP
            ApplyMatchingRuleContext applyMatchingRuleContext = new ApplyMatchingRuleContext(ldapUserWrapper.getLdapUser(), ldapUserWrapper.getLdapUserAttributes(), cubaUser);
            setCommonAttributesFromLdapUser(applyMatchingRuleContext, cubaUser, login);
            List<MatchingRule> matchingRules = matchingRuleDao.getMatchingRules();
            applicationEventPublisher.publishEvent(new BeforeUserUpdatedFromLdapEvent(this, applyMatchingRuleContext, cubaUser));
            for (MatchingRule mr : matchingRules) {
                matchingRuleApplierInitializer.getMatchingRuleChain().applyMatchingRule(mr, applyMatchingRuleContext, cubaUser);
                if (applyMatchingRuleContext.isStopExecution()) {
                    break;
                }
            }
            applicationEventPublisher.publishEvent(new AfterUserUpdatedFromLdapEvent(this, applyMatchingRuleContext, cubaUser));
            cubaUserDao.saveCubaUser(cubaUser);
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
    //TODO: объединить synchronizeUser и testUserSynchronization
    public TestUserSynchronizationDto testUserSynchronization(String login, List<AbstractMatchingRule> rulesToApply) {
        TestUserSynchronizationDto testUserSynchronizationDto = new TestUserSynchronizationDto();
        LdapUserWrapper ldapUserWrapper = ldapUserDao.getLdapUserWrapper(login);
        if (ldapUserWrapper == null) return testUserSynchronizationDto;

        testUserSynchronizationDto.setUserExistsInLdap(true);
        User cubaUser = cubaUserDao.getCubaUserByLogin(login);

        List<MatchingRule> result = rulesToApply.stream().filter(r -> !CUSTOM.equals(r.getRuleType())).collect(Collectors.toList());
        List<CustomLdapMatchingRule> customRules = matchingRuleDao.getCustomMatchingRules();
        rulesToApply.stream().filter(r -> CUSTOM.equals(r.getRuleType())).forEach(customRuleDto -> {
            CustomLdapMatchingRule clmr = customRules.stream().filter(cr -> cr.getId().equals(customRuleDto.getId())).findFirst().get();
            CustomLdapMatchingRule tempClmr = clmr.clone(clmr);
            tempClmr.setOrder(customRuleDto.getOrder());
            result.add(tempClmr);
        });

        ApplyMatchingRuleContext applyMatchingRuleContext = new ApplyMatchingRuleContext(ldapUserWrapper.getLdapUser(), ldapUserWrapper.getLdapUserAttributes(), cubaUser);
        if (cubaUser.getUserRoles() != null) {
            cubaUser.getUserRoles().clear();
        } else {
            cubaUser.setUserRoles(new ArrayList<>());
        }

        result.sort((r1, r2) -> {
            int o1 = r1.getOrder() == null || r1.getOrder().getOrder() == null ? 0 : r1.getOrder().getOrder();
            int o2 = r2.getOrder() == null || r2.getOrder().getOrder() == null ? 0 : r2.getOrder().getOrder();
            return o1 - o2;
        });

        for (MatchingRule mr : result) {
            matchingRuleApplierInitializer.getMatchingRuleChain().applyMatchingRule(mr, applyMatchingRuleContext, cubaUser);
            if (applyMatchingRuleContext.isStopExecution()) {
                break;
            }
        }
        applyMatchingRuleContext.getAppliedRules().forEach(matchingRule -> {
            if (CUSTOM.equals(matchingRule.getRuleType())) {
                CustomLdapMatchingRule pmr = (CustomLdapMatchingRule) matchingRule;
                testUserSynchronizationDto.getAppliedMatchingRules().add(matchingRuleDao.mapProgrammaticRule(pmr));
            } else {
                testUserSynchronizationDto.getAppliedMatchingRules().add((AbstractMatchingRule) matchingRule);
            }
        });

        testUserSynchronizationDto.getAppliedCubaRoles().addAll(cubaUser.getUserRoles().stream().map(UserRole::getRole).collect(Collectors.toList()));
        testUserSynchronizationDto.setGroup(cubaUser.getGroup());

        return testUserSynchronizationDto;
    }
}
