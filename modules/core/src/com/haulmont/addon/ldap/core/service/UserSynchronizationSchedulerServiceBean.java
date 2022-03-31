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
import com.haulmont.addon.ldap.core.dao.CubaUserDao;
import com.haulmont.addon.ldap.core.dao.LdapUserDao;
import com.haulmont.addon.ldap.core.dao.MatchingRuleDao;
import com.haulmont.addon.ldap.dto.ExpiredSession;
import com.haulmont.addon.ldap.dto.LdapUser;
import com.haulmont.addon.ldap.dto.UserSynchronizationResultDto;
import com.haulmont.addon.ldap.entity.CommonMatchingRule;
import com.haulmont.addon.ldap.service.TenantProviderService;
import com.haulmont.addon.ldap.service.UserSynchronizationService;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.security.app.UserSessionService;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserSessionEntity;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

@Service(UserSynchronizationSchedulerService.NAME)
public class UserSynchronizationSchedulerServiceBean implements UserSynchronizationSchedulerService {

    private final Set<ExpiredSession> expiredSessions = new CopyOnWriteArraySet<>();

    @Inject
    private UserSynchronizationService userSynchronizationService;

    @Inject
    private UserSessionService userSessionService;

    @Inject
    private TimeSource timeSource;

    @Inject
    private LdapPropertiesConfig ldapPropertiesConfig;

    @Inject
    private CubaUserDao cubaUserDao;

    @Inject
    private LdapUserDao ldapUserDao;

    @Inject
    private MatchingRuleDao matchingRuleDao;

    @Inject
    private TenantProviderService tenantProviderService;

    public void checkExpiredSessions() {
        List<String> standardAuthenticationUsers = ldapPropertiesConfig.getStandardAuthenticationUsers() == null
                ? new ArrayList<>()
                : ldapPropertiesConfig.getStandardAuthenticationUsers();
        List<UserSessionEntity> activeSessions = userSessionService.loadUserSessionEntities(UserSessionService.Filter.ALL).stream()
                .filter(userSession -> !userSession.getSystem())
                .filter(userSession -> !standardAuthenticationUsers.contains(userSession.getLogin()))
                .collect(Collectors.toList());
        for (UserSessionEntity use : activeSessions) {
            UserSynchronizationResultDto userSynchronizationResult = userSynchronizationService
                    .synchronizeUser(use.getLogin(), use.getSysTenantId(), false, null, null, null);
            if (userSynchronizationResult.isUserPrivilegesChanged()) {
                expiredSessions.add(new ExpiredSession(use.getUuid(), use.getLogin(), timeSource.currentTimeMillis()));
            }
        }
    }

    public void killExpiredSessions() {
        for (ExpiredSession es : expiredSessions) {
            if ((timeSource.currentTimeMillis() - es.getCreateTsMillis()) >= ldapPropertiesConfig.getSessionExpiringPeriodSec() * 1000) {
                expiredSessions.remove(es);
                userSessionService.killSession(es.getUuid());
            }
        }
    }

    public Set<ExpiredSession> getExpiredSessions() {
        return new HashSet<>(expiredSessions);
    }

    @Override
    public void synchronizeUsersFromLdap() {
        List<String> activeUsers = cubaUserDao.getCubaUsersLoginsAndGroup();
        List<String> standardAuthenticationUsers = ldapPropertiesConfig.getStandardAuthenticationUsers();
        activeUsers.removeAll(standardAuthenticationUsers);
        List<List<String>> subLists = ListUtils.partition(activeUsers, ldapPropertiesConfig.getUserSynchronizationBatchSize());

        for (String tenantId : tenantProviderService.getTenantIds()) {
            List<CommonMatchingRule> matchingRules = matchingRuleDao.getMatchingRules(tenantId);
            for (List<String> list : subLists) {
                List<User> users = cubaUserDao.getCubaUsersByLogin(list);
                List<LdapUser> ldapUsers = ldapUserDao.getLdapUsers(list, tenantId);
                userSynchronizationService.synchronizeUsersFromLdap(users, ldapUsers, matchingRules);
            }
        }
    }
}
