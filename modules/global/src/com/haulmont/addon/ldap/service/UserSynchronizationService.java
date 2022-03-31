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

package com.haulmont.addon.ldap.service;

import com.haulmont.addon.ldap.dto.ExpiredSession;
import com.haulmont.addon.ldap.dto.LdapUser;
import com.haulmont.addon.ldap.dto.TestUserSynchronizationDto;
import com.haulmont.addon.ldap.dto.UserSynchronizationResultDto;
import com.haulmont.addon.ldap.entity.AbstractCommonMatchingRule;
import com.haulmont.addon.ldap.entity.CommonMatchingRule;
import com.haulmont.cuba.security.entity.User;

import java.util.List;
import java.util.Set;

public interface UserSynchronizationService {
    String NAME = "ldap_UserSynchronizationService";

    /**
     * Synchronizes the state of a CUBA user in accordance with LDAP using persisted matching rules.
     *
     * @param login                     user login
     * @param tenantId                  todo
     * @param saveSynchronizationResult persists the user state after synchronization
     * @param cachedLdapUser            cached LdapUser, if null it will be loaded from LDAP
     * @param cachedCubaUser            cached User, if null it will be loaded from database
     * @param cachedMatchingRules       cached matching rules list, if null it will be loaded from database
     */
    UserSynchronizationResultDto synchronizeUser(String login, String tenantId, boolean saveSynchronizationResult, LdapUser cachedLdapUser,
                                                 User cachedCubaUser, List<CommonMatchingRule> cachedMatchingRules);

    /**
     * Synchronizes the state of a CUBA user in accordance with LDAP using provided matching rules.
     * After synchronization, the user state is not persisted.
     *
     * @param login        user login
     * @param tenantId todo
     * @param rulesToApply matching rules provided on LDAP Matching Rule Screen.
     */
    TestUserSynchronizationDto testUserSynchronization(String login, String tenantId, List<AbstractCommonMatchingRule> rulesToApply);

    /**
     * Returns expired user sessions<br>
     * A session becomes expired if an access group or roles assigned to the user are changed since
     * the last LDAP synchronization or a user is deactivated via LDAP.
     */
    Set<ExpiredSession> getExpiringSession();

    /**
     * Synchronizes the state of a CUBA user in accordance with LDAP using persisted matching rules using pre loaded users and matching rules.
     *
     * @param cubaUsers     pre loaded CUBA users
     * @param ldapUsers     pre loaded LDAP users
     * @param matchingRules pre loaded matching rules
     */
    void synchronizeUsersFromLdap(List<User> cubaUsers, List<LdapUser> ldapUsers, List<CommonMatchingRule> matchingRules);

}
