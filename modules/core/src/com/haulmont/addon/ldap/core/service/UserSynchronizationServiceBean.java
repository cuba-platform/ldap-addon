package com.haulmont.addon.ldap.core.service;

import com.haulmont.addon.ldap.core.dao.CubaUserDao;
import com.haulmont.addon.ldap.core.dao.LdapUserDao;
import com.haulmont.addon.ldap.core.dao.MatchingRuleDao;
import com.haulmont.addon.ldap.core.dao.UserSynchronizationLogDao;
import com.haulmont.addon.ldap.core.dto.LdapUser;
import com.haulmont.addon.ldap.core.rule.LdapMatchingRuleContext;
import com.haulmont.addon.ldap.core.rule.appliers.MatchingRuleApplier;
import com.haulmont.addon.ldap.core.rule.custom.CustomLdapMatchingRuleWrapper;
import com.haulmont.addon.ldap.core.spring.events.*;
import com.haulmont.addon.ldap.dto.ExpiredSession;
import com.haulmont.addon.ldap.dto.TestUserSynchronizationDto;
import com.haulmont.addon.ldap.entity.AbstractCommonMatchingRule;
import com.haulmont.addon.ldap.entity.AbstractDbStoredMatchingRule;
import com.haulmont.addon.ldap.entity.CommonMatchingRule;
import com.haulmont.addon.ldap.service.UserSynchronizationService;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.MetadataTools;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.security.entity.Role;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.haulmont.addon.ldap.entity.MatchingRuleType.CUSTOM;

@Service(UserSynchronizationService.NAME)
public class UserSynchronizationServiceBean implements UserSynchronizationService {

    private final Logger logger = LoggerFactory.getLogger(UserSynchronizationServiceBean.class);

    @Inject
    private LdapUserDao ldapUserDao;

    @Inject
    private CubaUserDao cubaUserDao;

    @Inject
    private MatchingRuleDao matchingRuleDao;

    @Inject
    private MatchingRuleApplier matchingRuleApplier;

    @Inject
    private Events events;

    @Inject
    private MetadataTools metadataTools;

    @Inject
    private Messages messages;

    @Inject
    private UserSynchronizationLogDao userSynchronizationLogDao;

    @Inject
    private UserSynchronizationSchedulerService userSynchronizationSchedulerService;

    @Override
    public boolean synchronizeUser(String login, boolean saveSynchronizationResult) {
        try {
            String modeMessage = saveSynchronizationResult ? messages.formatMessage(UserSynchronizationServiceBean.class, "saveMode") :
                    messages.formatMessage(UserSynchronizationServiceBean.class, "notSaveMode");
            SynchronizationMode modeType = saveSynchronizationResult ? SynchronizationMode.SAVE_DATA : SynchronizationMode.NOT_SAVE_DATA;

            LdapUser ldapUser = ldapUserDao.getLdapUser(login);
            if (ldapUser == null) {
                return false;
            }
            logger.info(messages.formatMessage(UserSynchronizationServiceBean.class, "userSyncStart", login, modeMessage));
            User cubaUser = cubaUserDao.getCubaUserByLogin(login);
            User beforeRulesApplyUserState = metadataTools.copy(cubaUser);
            beforeRulesApplyUserState.setUserRoles(new ArrayList<>(cubaUser.getUserRoles()));
            cubaUser.getUserRoles().clear();//user get roles only from LDAP
            LdapMatchingRuleContext ldapMatchingRuleContext = new LdapMatchingRuleContext(ldapUser, cubaUser);
            setCommonAttributesFromLdapUser(ldapMatchingRuleContext, cubaUser, beforeRulesApplyUserState, login, modeMessage, modeType);
            if (!ldapMatchingRuleContext.getLdapUser().getDisabled()) {
                List<CommonMatchingRule> matchingRules = matchingRuleDao.getMatchingRules();
                events.publish(new BeforeUserUpdatedFromLdapEvent(this, ldapMatchingRuleContext, cubaUser, modeType));
                matchingRuleApplier.applyMatchingRules(matchingRules, ldapMatchingRuleContext, beforeRulesApplyUserState);
                events.publish(new AfterUserUpdatedFromLdapEvent(this, ldapMatchingRuleContext, cubaUser, modeType));
            }
            if (saveSynchronizationResult) {
                cubaUserDao.saveCubaUser(cubaUser, beforeRulesApplyUserState, ldapMatchingRuleContext);
            }

            logger.info(messages.formatMessage(UserSynchronizationServiceBean.class, "userSyncEnd", login, modeMessage));
            return userPrivilegesWereChanged(beforeRulesApplyUserState, cubaUser);
        } catch (Exception e) {
            userSynchronizationLogDao.logSynchronizationError(login, e);
            throw new RuntimeException(messages.formatMessage(UserSynchronizationServiceBean.class, "errorDuringLdapSync", login), e);
        }
    }

    @Override
    public TestUserSynchronizationDto testUserSynchronization(String login, List<AbstractCommonMatchingRule> rulesToApply) {
        TestUserSynchronizationDto testUserSynchronizationDto = new TestUserSynchronizationDto();
        LdapUser ldapUser = ldapUserDao.getLdapUser(login);
        if (ldapUser == null) return testUserSynchronizationDto;

        testUserSynchronizationDto.setUserExistsInLdap(true);
        User cubaUser = cubaUserDao.getCubaUserByLogin(login);

        List<CommonMatchingRule> result = rulesToApply.stream().filter(r -> !(CUSTOM == r.getRuleType())).collect(Collectors.toList());
        List<CustomLdapMatchingRuleWrapper> customRules = matchingRuleDao.getCustomMatchingRules();
        rulesToApply.stream()
                .filter(r -> CUSTOM == r.getRuleType())
                .forEach(customRuleDto -> {
                    CustomLdapMatchingRuleWrapper wrapper = customRules.stream()
                            .filter(cr -> cr.getMatchingRuleId().equals(customRuleDto.getMatchingRuleId()))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Custom matching rule with id " + customRuleDto.getMatchingRuleId() + " must exist"));
                    wrapper.getOrder().setOrder(customRuleDto.getOrder().getOrder());
                    wrapper.getStatus().setIsActive(customRuleDto.getStatus().getIsActive());
                    result.add(wrapper);
                });

        LdapMatchingRuleContext ldapMatchingRuleContext = new LdapMatchingRuleContext(ldapUser, cubaUser);
        User beforeRulesApplyUserState = metadataTools.copy(cubaUser);
        beforeRulesApplyUserState.setUserRoles(new ArrayList<>(cubaUser.getUserRoles()));
        cubaUser.getUserRoles().clear();

        matchingRuleApplier.applyMatchingRules(result, ldapMatchingRuleContext, beforeRulesApplyUserState);

        ldapMatchingRuleContext.getAppliedRules().forEach(matchingRule -> {
            if (CUSTOM == matchingRule.getRuleType()) {
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

    @Override
    public Set<ExpiredSession> getExpiringSession() {
        return userSynchronizationSchedulerService.getExpiredSessions();
    }

    private void setCommonAttributesFromLdapUser(LdapMatchingRuleContext ldapMatchingRuleContext, User cubaUser, User beforeRulesApplyUserState, String login, String modeMessage, SynchronizationMode modeType) {
        Boolean userDisabled = ldapMatchingRuleContext.getLdapUser().getDisabled();
        if (!beforeRulesApplyUserState.getActive() && userDisabled) {
            return;
        }
        cubaUser.setEmail(ldapMatchingRuleContext.getLdapUser().getEmail());
        cubaUser.setName(ldapMatchingRuleContext.getLdapUser().getCn());
        cubaUser.setPosition(ldapMatchingRuleContext.getLdapUser().getPosition());
        cubaUser.setLanguage(ldapMatchingRuleContext.getLdapUser().getLanguage());

        if (userDisabled) {
            cubaUser.setActive(false);
            events.publish(new UserDeactivatedFromLdapEvent(this, ldapMatchingRuleContext, cubaUser, modeType));
            logger.info(messages.formatMessage(UserSynchronizationServiceBean.class, "userDeactivatedFromLdap", login, modeMessage));
        } else {
            cubaUser.setActive(true);
        }

        if (PersistenceHelper.isNew(cubaUser)) {//only for new users
            logger.info(messages.formatMessage(UserSynchronizationServiceBean.class, "userCreatedFromLdap", login, modeMessage));
            events.publish(new UserCreatedFromLdapEvent(this, ldapMatchingRuleContext, cubaUser, modeType));
        }
        logger.info(messages.formatMessage(UserSynchronizationServiceBean.class, "userGetCommonInfoFromLdap", login, modeMessage));
    }

    private boolean userPrivilegesWereChanged(User beforeSyncUser, User afterSyncUser) {
        if (!afterSyncUser.getActive()) {
            return true;
        }

        if ((beforeSyncUser.getGroup() == null && afterSyncUser.getGroup() != null) ||
                (beforeSyncUser.getGroup() != null && !beforeSyncUser.getGroup().equals(afterSyncUser.getGroup()))) {
            return true;
        }

        List<Role> rolesBeforeSync = beforeSyncUser.getUserRoles().stream()
                .map(UserRole::getRole)
                .sorted(Comparator.comparing(Role::getName))
                .collect(Collectors.toList());
        List<Role> rolesAfterSync = afterSyncUser.getUserRoles().stream()
                .map(UserRole::getRole)
                .sorted(Comparator.comparing(Role::getName))
                .collect(Collectors.toList());

        if (!rolesAfterSync.equals(rolesBeforeSync)) {
            return true;
        }

        return false;
    }
}
