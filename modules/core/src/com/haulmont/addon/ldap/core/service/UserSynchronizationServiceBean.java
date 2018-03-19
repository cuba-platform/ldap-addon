package com.haulmont.addon.ldap.core.service;

import com.haulmont.addon.ldap.core.dao.CubaUserDao;
import com.haulmont.addon.ldap.core.dao.LdapUserDao;
import com.haulmont.addon.ldap.core.dao.MatchingRuleDao;
import com.haulmont.addon.ldap.core.dao.UserSynchronizationLogDao;
import com.haulmont.addon.ldap.core.dto.LdapUserWrapper;
import com.haulmont.addon.ldap.core.rule.ApplyMatchingRuleContext;
import com.haulmont.addon.ldap.core.rule.appliers.MatchingRuleApplier;
import com.haulmont.addon.ldap.core.rule.programmatic.CustomLdapMatchingRuleWrapper;
import com.haulmont.addon.ldap.core.spring.events.*;
import com.haulmont.addon.ldap.dto.TestUserSynchronizationDto;
import com.haulmont.addon.ldap.entity.AbstractCommonMatchingRule;
import com.haulmont.addon.ldap.entity.AbstractDbStoredMatchingRule;
import com.haulmont.addon.ldap.entity.CommonMatchingRule;
import com.haulmont.addon.ldap.service.UserSynchronizationService;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.MetadataTools;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(UserSynchronizationServiceBean.class);

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
    @Qualifier(MatchingRuleApplier.NAME)
    private MatchingRuleApplier matchingRuleApplier;

    @Inject
    private ApplicationEventPublisher applicationEventPublisher;

    @Inject
    private MetadataTools metadataTools;

    @Inject
    private Messages messages;

    @Inject
    private UserSynchronizationLogDao userSynchronizationLogDao;

    @Override
    public void synchronizeUserAfterLdapLogin(String login) {
        try {
            logger.info(messages.formatMessage(UserSynchronizationServiceBean.class, "userSyncStart", login));
            LdapUserWrapper ldapUserWrapper = ldapUserDao.getLdapUserWrapper(login);
            User cubaUser = cubaUserDao.getCubaUserByLogin(login);
            User originalUser = metadataTools.copy(cubaUser);
            originalUser.setUserRoles(new ArrayList<>(cubaUser.getUserRoles()));
            cubaUser.getUserRoles().clear();//user get roles only from LDAP
            ApplyMatchingRuleContext applyMatchingRuleContext = new ApplyMatchingRuleContext(ldapUserWrapper.getLdapUser(), ldapUserWrapper.getLdapUserAttributes(), cubaUser);
            setCommonAttributesFromLdapUser(applyMatchingRuleContext, cubaUser, login);
            List<CommonMatchingRule> matchingRules = matchingRuleDao.getMatchingRules();
            applicationEventPublisher.publishEvent(new BeforeUserUpdatedFromLdapEvent(this, applyMatchingRuleContext, cubaUser));
            matchingRuleApplier.applyMatchingRules(matchingRules, applyMatchingRuleContext);
            applicationEventPublisher.publishEvent(new AfterUserUpdatedFromLdapEvent(this, applyMatchingRuleContext, cubaUser));
            cubaUserDao.saveCubaUser(cubaUser, originalUser, applyMatchingRuleContext);
            logger.info(messages.formatMessage(UserSynchronizationServiceBean.class, "userSyncEnd", login));
        } catch (Exception e) {
            userSynchronizationLogDao.logSynchronizationError(login, e);
            throw new RuntimeException(messages.formatMessage(UserSynchronizationServiceBean.class, "errorDuringLdapSync", login), e);
        }
    }

    @Override
    //TODO: объединить synchronizeUserAfterLdapLogin и testUserSynchronization
    public TestUserSynchronizationDto testUserSynchronization(String login, List<AbstractCommonMatchingRule> rulesToApply) {
        TestUserSynchronizationDto testUserSynchronizationDto = new TestUserSynchronizationDto();
        LdapUserWrapper ldapUserWrapper = ldapUserDao.getLdapUserWrapper(login);
        if (ldapUserWrapper == null) return testUserSynchronizationDto;

        testUserSynchronizationDto.setUserExistsInLdap(true);
        User cubaUser = cubaUserDao.getCubaUserByLogin(login);

        List<CommonMatchingRule> result = rulesToApply.stream().filter(r -> !CUSTOM.equals(r.getRuleType())).collect(Collectors.toList());
        List<CustomLdapMatchingRuleWrapper> customRules = matchingRuleDao.getCustomMatchingRules();
        rulesToApply.stream().filter(r -> CUSTOM.equals(r.getRuleType())).forEach(customRuleDto -> {
            CustomLdapMatchingRuleWrapper wrapper = customRules.stream().filter(cr -> cr.getMatchingRuleId().equals(customRuleDto.getMatchingRuleId())).findFirst().get();
            wrapper.getOrder().setOrder(customRuleDto.getOrder().getOrder());
            wrapper.getStatus().setIsActive(customRuleDto.getStatus().getIsActive());
            result.add(wrapper);
        });

        ApplyMatchingRuleContext applyMatchingRuleContext = new ApplyMatchingRuleContext(ldapUserWrapper.getLdapUser(), ldapUserWrapper.getLdapUserAttributes(), cubaUser);
        cubaUser.getUserRoles().clear();

        matchingRuleApplier.applyMatchingRules(result, applyMatchingRuleContext);

        applyMatchingRuleContext.getAppliedRules().forEach(matchingRule -> {
            if (CUSTOM.equals(matchingRule.getRuleType())) {
                CustomLdapMatchingRuleWrapper cmr = (CustomLdapMatchingRuleWrapper) matchingRule;
                testUserSynchronizationDto.getAppliedMatchingRules().add(matchingRuleDao.mapCustomRuleToDto(cmr));
            } else {
                testUserSynchronizationDto.getAppliedMatchingRules().add((AbstractDbStoredMatchingRule) matchingRule);
            }
        });

        testUserSynchronizationDto.getAppliedCubaRoles().addAll(cubaUser.getUserRoles().stream().map(UserRole::getRole).collect(Collectors.toList()));
        testUserSynchronizationDto.setGroup(cubaUser.getGroup());

        return testUserSynchronizationDto;
    }

    private void setCommonAttributesFromLdapUser(ApplyMatchingRuleContext applyMatchingRuleContext, User cubaUser, String login) {
        if (PersistenceHelper.isNew(cubaUser)) {//only for new users
            applicationEventPublisher.publishEvent(new BeforeNewUserCreatedFromLdapEvent(this, applyMatchingRuleContext, cubaUser));
            cubaUser.setLogin(login);
            cubaUser.setUserRoles(new ArrayList<>());
            logger.info(messages.formatMessage(UserSynchronizationServiceBean.class, "userCreatedFromLdap", login));
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
            logger.info(messages.formatMessage(UserSynchronizationServiceBean.class, "userDeactivatedFromLdap", login));
        } else {
            cubaUser.setActive(true);
        }

        if (PersistenceHelper.isNew(cubaUser)) {//only for new users
            applicationEventPublisher.publishEvent(new AfterNewUserCreatedFromLdapEvent(this, applyMatchingRuleContext, cubaUser));
        }
        logger.info(messages.formatMessage(UserSynchronizationServiceBean.class, "userGetCommonInfoFromLdap", login));
    }
}
