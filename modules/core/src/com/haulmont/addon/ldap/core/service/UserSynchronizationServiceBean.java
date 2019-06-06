/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.addon.ldap.core.service;

import com.haulmont.addon.ldap.config.LdapPropertiesConfig;
import com.haulmont.addon.ldap.core.dao.*;
import com.haulmont.addon.ldap.core.rule.LdapMatchingRuleContext;
import com.haulmont.addon.ldap.core.rule.appliers.MatchingRuleApplier;
import com.haulmont.addon.ldap.core.rule.custom.CustomLdapMatchingRuleWrapper;
import com.haulmont.addon.ldap.core.spring.events.*;
import com.haulmont.addon.ldap.dto.ExpiredSession;
import com.haulmont.addon.ldap.dto.LdapUser;
import com.haulmont.addon.ldap.dto.TestUserSynchronizationDto;
import com.haulmont.addon.ldap.dto.UserSynchronizationResultDto;
import com.haulmont.addon.ldap.entity.AbstractCommonMatchingRule;
import com.haulmont.addon.ldap.entity.AbstractDbStoredMatchingRule;
import com.haulmont.addon.ldap.entity.CommonMatchingRule;
import com.haulmont.addon.ldap.service.UserSynchronizationService;
import com.haulmont.addon.ldap.utils.MatchingRuleUtils;
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
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Supplier;
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

    @Inject
    private GroupDao groupDao;

    @Inject
    private LdapPropertiesConfig ldapPropertiesConfig;

    @Override
    public UserSynchronizationResultDto synchronizeUser(String login,
                                                        boolean saveSynchronizationResult,
                                                        LdapUser cachedLdapUser,
                                                        User cachedCubaUser,
                                                        List<CommonMatchingRule> cachedMatchingRules) {
        try {
            String modeMessage = saveSynchronizationResult ? messages.formatMessage(UserSynchronizationServiceBean.class, "saveMode") :
                    messages.formatMessage(UserSynchronizationServiceBean.class, "notSaveMode");
            SynchronizationMode modeType = saveSynchronizationResult ? SynchronizationMode.SAVE_DATA : SynchronizationMode.NOT_SAVE_DATA;

            // get LDAP user entity
            LdapUser ldapUser = orElseGet(cachedLdapUser, () -> ldapUserDao.getLdapUser(login));
            if (ldapUser == null) {
                return new UserSynchronizationResultDto();
            }
            logger.info(messages.formatMessage(UserSynchronizationServiceBean.class, "userSyncStart", login, modeMessage));

            // get related CUBA user entity that will be synchronized then
            User cubaUser = orElseGet(cachedCubaUser, () -> cubaUserDao.getCubaUserByLogin(login));

            // copy CUBA user state before sync
            User originalCubaUser = metadataTools.copy(cubaUser);
            originalCubaUser.setUserRoles(new ArrayList<>(cubaUser.getUserRoles()));


            cubaUser.getUserRoles().clear();//user get roles only from LDAP

            // Create matching rule context
            LdapMatchingRuleContext ldapMatchingRuleContext = new LdapMatchingRuleContext(ldapUser, cubaUser,
                    MatchingRuleUtils.getRoles(originalCubaUser), originalCubaUser.getGroup());

            // Get user enabled status
            boolean ldapUserEnabled = !ldapUser.getDisabled();
            boolean cubaUserEnabled = originalCubaUser.getActive();

            if (ldapUserEnabled) {
                events.publish(new UserActivatedFromLdapEvent(this, ldapMatchingRuleContext, cubaUser, modeType));
            }

            if (cubaUserEnabled || ldapUserEnabled) {
                copyLdapAttributesToCubaUser(ldapMatchingRuleContext, cubaUser, login, modeMessage, modeType);
            }

            if (ldapUserEnabled) {
                List<CommonMatchingRule> matchingRules = orElseGet(cachedMatchingRules, matchingRuleDao::getMatchingRules);
                events.publish(new BeforeUserRolesAndAccessGroupUpdatedFromLdapEvent(this, ldapMatchingRuleContext, cubaUser, modeType));
                matchingRuleApplier.applyMatchingRules(matchingRules, ldapMatchingRuleContext, originalCubaUser);
                events.publish(new AfterUserRolesAndAccessGroupUpdatedFromLdapEvent(this, ldapMatchingRuleContext, cubaUser, modeType));
            }

            if (saveSynchronizationResult) {
                cubaUserDao.saveCubaUser(cubaUser, originalCubaUser, ldapMatchingRuleContext);
            }

            logger.info(messages.formatMessage(UserSynchronizationServiceBean.class, "userSyncEnd", login, modeMessage));
            return userPrivilegesChanged(originalCubaUser, cubaUser);
        } catch (Exception e) {
            userSynchronizationLogDao.logSynchronizationError(login, e);
            throw new RuntimeException(messages.formatMessage(UserSynchronizationServiceBean.class, "errorDuringLdapSync", login), e);
        }
    }

    private static <T> T orElseGet(T value, Supplier<T> orElse) {
        return Optional.ofNullable(value).orElseGet(orElse);
    }

    @Override
    public TestUserSynchronizationDto testUserSynchronization(String login, List<AbstractCommonMatchingRule> rulesToApply) {
        TestUserSynchronizationDto testUserSynchronizationDto = new TestUserSynchronizationDto();
        LdapUser ldapUser = ldapUserDao.getLdapUser(login);
        if (ldapUser == null) return testUserSynchronizationDto;

        testUserSynchronizationDto.setUserExistsInLdap(true);
        User cubaUser = cubaUserDao.getCubaUserByLogin(login);

        List<CommonMatchingRule> result = rulesToApply.stream()
                .filter(r -> !(CUSTOM == r.getRuleType()))
                .collect(Collectors.toList());
        List<CustomLdapMatchingRuleWrapper> customRules = matchingRuleDao.getCustomMatchingRules();
        rulesToApply.stream()
                .filter(r -> CUSTOM == r.getRuleType())
                .forEach(customRuleDto -> {
                    CustomLdapMatchingRuleWrapper wrapper = customRules.stream()
                            .filter(cr -> cr.getMatchingRuleId().equals(customRuleDto.getMatchingRuleId()))
                            .findFirst()
                            .orElseThrow(() ->
                                    new RuntimeException("Custom matching rule with id " + customRuleDto.getMatchingRuleId() + " must exist"));
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

        testUserSynchronizationDto.getAppliedCubaRoles().addAll(cubaUser.getUserRoles().stream()
                .map(UserRole::getRole)
                .collect(Collectors.toList()));
        testUserSynchronizationDto.setGroup(cubaUser.getGroup());

        return testUserSynchronizationDto;
    }

    @Override
    public Set<ExpiredSession> getExpiringSession() {
        return userSynchronizationSchedulerService.getExpiredSessions();
    }

    @Override
    @Transactional
    public void synchronizeUsersFromLdap(List<User> cubaUsers, List<LdapUser> ldapUsers, List<CommonMatchingRule> matchingRules) {
        for (LdapUser ldapUser : ldapUsers) {
            User cubaUser = cubaUsers.stream().filter(cu -> cu.getLogin().equals(ldapUser.getLogin())).findAny().
                    orElseThrow(() -> (new RuntimeException("Synchronization: No CUBA user with login " + ldapUser.getLogin())));
            if (ldapPropertiesConfig.getUserSynchronizationOnlyActiveProperty()) {
                if (ldapUser.getDisabled() && cubaUser.getActive()) {
                    cubaUser.setActive(false);
                    userSynchronizationLogDao.logDisabledDuringSync(ldapUser);
                    cubaUserDao.save(cubaUser);
                } else if (!ldapUser.getDisabled() && !cubaUser.getActive()) {
                    cubaUser.setActive(true);
                    userSynchronizationLogDao.logEnabledDuringSync(ldapUser);
                    cubaUserDao.save(cubaUser);
                }
            } else {
                synchronizeUser(ldapUser.getLogin(), true, ldapUser, cubaUser, matchingRules);
            }
        }

    }

    private void copyLdapAttributesToCubaUser(LdapMatchingRuleContext ldapMatchingRuleContext,
                                              User syncUser,
                                              String login,
                                              String modeMessage,
                                              SynchronizationMode modeType) {
        if (PersistenceHelper.isNew(syncUser) || ldapPropertiesConfig.getSynchronizeCommonInfoFromLdap()) {
            syncUser.setEmail(ldapMatchingRuleContext.getLdapUser().getEmail());
            syncUser.setName(ldapMatchingRuleContext.getLdapUser().getCn());
            syncUser.setFirstName(ldapMatchingRuleContext.getLdapUser().getGivenName());
            syncUser.setLastName(ldapMatchingRuleContext.getLdapUser().getSn());
            syncUser.setMiddleName(ldapMatchingRuleContext.getLdapUser().getMiddleName());
            syncUser.setPosition(ldapMatchingRuleContext.getLdapUser().getPosition());
            syncUser.setLanguage(ldapMatchingRuleContext.getLdapUser().getLanguage());
        }

        if (ldapMatchingRuleContext.getLdapUser().getDisabled()) {
            syncUser.setActive(false);
            events.publish(new UserDeactivatedFromLdapEvent(this, ldapMatchingRuleContext, syncUser, modeType));
            logger.info(messages.formatMessage(UserSynchronizationServiceBean.class, "userDeactivatedFromLdap", login, modeMessage));
        } else {
            syncUser.setActive(true);
        }

        if (PersistenceHelper.isNew(syncUser)) {//only for new users
            if (!syncUser.getActive()) {//set default group to new disabled user
                syncUser.setGroup(groupDao.getDefaultGroup());
            }
            logger.info(messages.formatMessage(UserSynchronizationServiceBean.class, "userCreatedFromLdap", login, modeMessage));
            events.publish(new UserCreatedFromLdapEvent(this, ldapMatchingRuleContext, syncUser, modeType));
        }
        logger.info(messages.formatMessage(UserSynchronizationServiceBean.class, "userGetCommonInfoFromLdap", login, modeMessage));
    }

    private UserSynchronizationResultDto userPrivilegesChanged(User beforeSyncUser, User afterSyncUser) {
        UserSynchronizationResultDto result = new UserSynchronizationResultDto();
        if (!afterSyncUser.getActive()) {
            result.setInactiveUser(true);
        }
        if ((beforeSyncUser.getActive() == null && afterSyncUser.getActive() != null) ||
                (beforeSyncUser.getActive() != null && !beforeSyncUser.getActive().equals(afterSyncUser.getActive()))) {
            result.setUserPrivilegesChanged(true);
        }

        if ((beforeSyncUser.getGroup() == null && afterSyncUser.getGroup() != null) ||
                (beforeSyncUser.getGroup() != null && !beforeSyncUser.getGroup().equals(afterSyncUser.getGroup()))) {
            result.setUserPrivilegesChanged(true);
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
            result.setUserPrivilegesChanged(true);
        }

        return result;
    }
}
