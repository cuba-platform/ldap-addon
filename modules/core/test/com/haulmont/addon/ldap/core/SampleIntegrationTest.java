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

package com.haulmont.addon.ldap.core;

import com.haulmont.addon.ldap.core.dao.*;
import com.haulmont.addon.ldap.dto.UserSynchronizationResultDto;
import com.haulmont.addon.ldap.entity.*;
import com.haulmont.addon.ldap.service.UserSynchronizationService;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.Role;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserRole;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class SampleIntegrationTest {

    @ClassRule
    public static LdapTestContainer cont = LdapTestContainer.Common.INSTANCE;

    private Metadata metadata;
    private Persistence persistence;
    private DataManager dataManager;
    private LdapUserDao ldapUserDao;
    private LdapConfigDao ldapConfigDao;
    private DaoHelper daoHelper;
    private CubaUserDao cubaUserDao;
    private UserSynchronizationService userSynchronizationService;
    private UserSynchronizationLogDao userSynchronizationLogDao;

    @Before
    public void setUp() throws Exception {
        metadata = cont.metadata();
        persistence = cont.persistence();
        dataManager = AppBeans.get(DataManager.class);
        ldapUserDao = AppBeans.get(LdapUserDao.class);
        ldapConfigDao = AppBeans.get(LdapConfigDao.class);
        daoHelper = AppBeans.get(DaoHelper.class);
        cubaUserDao = AppBeans.get(CubaUserDao.class);
        userSynchronizationService = AppBeans.get(UserSynchronizationService.class);
        userSynchronizationLogDao = AppBeans.get(UserSynchronizationLogDao.class);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void intActiveUserTest() {
        try (Transaction tx = persistence.createTransaction()) {
            LdapConfig ldapConfig = ldapConfigDao.getLdapConfig();
            ldapConfig.setLoginAttribute("uid");
            ldapConfig.setInactiveUserAttribute("roomNumber");
            ldapConfig.setMiddleNameAttribute("initials");
            daoHelper.persistOrMerge(ldapConfig);

            //Custom
            MatchingRuleOrder customOrder = metadata.create(MatchingRuleOrder.class);
            customOrder.setOrder(1);
            customOrder.setCustomMatchingRuleId("com.haulmont.addon.ldap.core.custom.TestCustomLdapRule");

            MatchingRuleStatus customStatus = metadata.create(MatchingRuleStatus.class);
            customStatus.setCustomMatchingRuleId("com.haulmont.addon.ldap.core.custom.TestCustomLdapRule");
            customStatus.setIsActive(true);

            daoHelper.persistOrMerge(customOrder);
            daoHelper.persistOrMerge(customStatus);

            Group testGroup1 = metadata.create(Group.class);
            testGroup1.setName("Test group 1");

            Group testGroup2 = metadata.create(Group.class);
            testGroup2.setName("Test group 2");

            daoHelper.persistOrMerge(testGroup1);
            daoHelper.persistOrMerge(testGroup2);

            Role role = metadata.create(Role.class);
            role.setName("Initial role");

            User barts = cubaUserDao.getCubaUserByLogin("barts");
            barts.setGroup(testGroup1);

            UserRole userRole = metadata.create(UserRole.class);
            userRole.setUser(barts);
            userRole.setRole(role);
            barts.getUserRoles().add(userRole);

            daoHelper.persistOrMerge(role);
            daoHelper.persistOrMerge(userRole);
            daoHelper.persistOrMerge(barts);

            //Simple
            Role simpleRole = metadata.create(Role.class);
            simpleRole.setName("Simple role");

            MatchingRuleOrder simpleOrder = metadata.create(MatchingRuleOrder.class);
            simpleOrder.setOrder(2);

            MatchingRuleStatus simpleStatus = metadata.create(MatchingRuleStatus.class);
            simpleStatus.setIsActive(true);

            SimpleRuleCondition simpleRuleCondition1 = metadata.create(SimpleRuleCondition.class);
            simpleRuleCondition1.setAttribute("givenName");
            simpleRuleCondition1.setAttributeValue("Bart");

            SimpleRuleCondition simpleRuleCondition2 = metadata.create(SimpleRuleCondition.class);
            simpleRuleCondition2.setAttribute("mail");
            simpleRuleCondition2.setAttributeValue("barts@example.com");

            SimpleMatchingRule simpleMatchingRule = metadata.create(SimpleMatchingRule.class);
            simpleMatchingRule.setUuid(UUID.fromString("a8ba61d2-c0a3-2a78-3322-c4f754807599"));
            simpleMatchingRule.setStatus(simpleStatus);
            simpleMatchingRule.setOrder(simpleOrder);
            simpleRuleCondition1.setSimpleMatchingRule(simpleMatchingRule);
            simpleRuleCondition2.setSimpleMatchingRule(simpleMatchingRule);
            simpleMatchingRule.getConditions().add(simpleRuleCondition1);
            simpleMatchingRule.getConditions().add(simpleRuleCondition2);
            simpleMatchingRule.setAccessGroup(testGroup2);
            simpleMatchingRule.getRoles().add(simpleRole);

            daoHelper.persistOrMerge(simpleRole);
            daoHelper.persistOrMerge(simpleMatchingRule);

            //Scripting
            Role scriptingRole = metadata.create(Role.class);
            scriptingRole.setName("Scripting role");

            MatchingRuleOrder scriptingOrder = metadata.create(MatchingRuleOrder.class);
            scriptingOrder.setOrder(4);

            MatchingRuleStatus scriptingStatus = metadata.create(MatchingRuleStatus.class);
            scriptingStatus.setIsActive(true);

            ScriptingMatchingRule scriptingMatchingRule = metadata.create(ScriptingMatchingRule.class);
            scriptingMatchingRule.setUuid(UUID.fromString("d5ce5200-2274-1c09-8b03-4f16a2f8e73e"));
            scriptingMatchingRule.setAccessGroup(testGroup1);
            scriptingMatchingRule.setStatus(scriptingStatus);
            scriptingMatchingRule.setOrder(scriptingOrder);
            scriptingMatchingRule.setScriptingCondition("{ldapContext}.ldapUser.login=='barts'");
            scriptingMatchingRule.getRoles().add(scriptingRole);
            daoHelper.persistOrMerge(scriptingRole);
            daoHelper.persistOrMerge(scriptingMatchingRule);

            //Scripting
            Role scriptingRole2 = metadata.create(Role.class);
            scriptingRole2.setName("Scripting role 2");

            MatchingRuleOrder scriptingOrder2 = metadata.create(MatchingRuleOrder.class);
            scriptingOrder2.setOrder(3);

            MatchingRuleStatus scriptingStatus2 = metadata.create(MatchingRuleStatus.class);
            scriptingStatus.setIsActive(true);

            ScriptingMatchingRule scriptingMatchingRule2 = metadata.create(ScriptingMatchingRule.class);
            scriptingMatchingRule2.setAccessGroup(testGroup2);
            scriptingMatchingRule2.setStatus(scriptingStatus2);
            scriptingMatchingRule2.setOrder(scriptingOrder2);
            scriptingMatchingRule2.setScriptingCondition("{ldapContext}.ldapUser.login=='joes'");
            scriptingMatchingRule2.getRoles().add(scriptingRole2);
            daoHelper.persistOrMerge(scriptingRole2);
            daoHelper.persistOrMerge(scriptingMatchingRule2);

            persistence.getEntityManager().flush();

            UserSynchronizationResultDto userSynchronizationResultDto = userSynchronizationService.synchronizeUser("barts", true, null, null, null);

            assertEquals(true, userSynchronizationResultDto.isUserPrivilegesChanged());

            User updated = cubaUserDao.getCubaUserByLogin("barts");

            assertEquals("barts@example.com", updated.getEmail());
            assertEquals("Bart Smeth", updated.getName());
            assertEquals("Bart", updated.getFirstName());
            assertEquals("Smeth", updated.getLastName());
            assertEquals("Petrovich", updated.getMiddleName());
            assertEquals("ru", updated.getLanguage());
            assertEquals("Manager", updated.getPosition());
            assertEquals(true, updated.getActive());

            assertEquals("Company", updated.getGroup().getName());
            assertEquals(3, updated.getUserRoles().size());
            assertEquals(true, updated.getUserRoles().stream().anyMatch(ur -> ur.getRole().getName().equals("Administrators")));
            assertEquals(true, updated.getUserRoles().stream().anyMatch(ur -> ur.getRole().getName().equals("Simple role")));
            assertEquals(true, updated.getUserRoles().stream().anyMatch(ur -> ur.getRole().getName().equals("Scripting role")));

            List<UserSynchronizationLog> logs = userSynchronizationLogDao.getByLogin("barts");
            assertEquals(1, logs.size());
            assertEquals(UserSynchronizationResultEnum.SUCCESS_SYNC, logs.get(0).getResult());
            assertEquals("Initial role\n", logs.get(0).getRolesBefore());
            assertEquals("Administrators\n" +
                    "Simple role\n" +
                    "Scripting role\n", logs.get(0).getRolesAfter());
            assertEquals("Test group 1", logs.get(0).getAccessGroupBefore());
            assertEquals("Company", logs.get(0).getAccessGroupAfter());
            assertEquals("1)Custom. Test custom Rule(com.haulmont.addon.ldap.core.custom.TestCustomLdapRule)\n" +
                    "\n" +
                    "2)Simple. null(a8ba61d2-c0a3-2a78-3322-c4f754807599)\n" +
                    "   Override existing group: false. Override existing roles: false. Terminal rule: false.\n" +
                    "   Group: Test group 2.\n" +
                    "   Roles: Simple role.\n" +
                    "   Additional rule info: givenName:Bart,mail:barts@example.com\n" +
                    "3)Scripting. null(d5ce5200-2274-1c09-8b03-4f16a2f8e73e)\n" +
                    "   Override existing group: false. Override existing roles: false. Terminal rule: false.\n" +
                    "   Group: Test group 1.\n" +
                    "   Roles: Scripting role.\n" +
                    "   Additional rule info: {ldapContext}.ldapUser.login=='barts'\n", logs.get(0).getAppliedRules());
            assertEquals(UserSynchronizationResultEnum.SUCCESS_SYNC, logs.get(0).getResult());
        }
    }

    @Test
    public void intInactiveUserTest() {
        try (Transaction tx = persistence.createTransaction()) {
            LdapConfig ldapConfig = ldapConfigDao.getLdapConfig();
            ldapConfig.setLoginAttribute("uid");
            ldapConfig.setInactiveUserAttribute("roomNumber");
            daoHelper.persistOrMerge(ldapConfig);

            //Custom
            MatchingRuleOrder customOrder = metadata.create(MatchingRuleOrder.class);
            customOrder.setOrder(1);
            customOrder.setCustomMatchingRuleId("com.haulmont.addon.ldap.core.custom.TestCustomLdapRule");

            MatchingRuleStatus customStatus = metadata.create(MatchingRuleStatus.class);
            customStatus.setCustomMatchingRuleId("com.haulmont.addon.ldap.core.custom.TestCustomLdapRule");
            customStatus.setIsActive(true);

            daoHelper.persistOrMerge(customOrder);
            daoHelper.persistOrMerge(customStatus);

            Group testGroup1 = metadata.create(Group.class);
            testGroup1.setName("Test group 1");

            Group testGroup2 = metadata.create(Group.class);
            testGroup2.setName("Test group 2");

            daoHelper.persistOrMerge(testGroup1);
            daoHelper.persistOrMerge(testGroup2);

            Role role = metadata.create(Role.class);
            role.setName("Initial role");

            User joes = cubaUserDao.getCubaUserByLogin("joes");
            joes.setGroup(testGroup1);

            UserRole userRole = metadata.create(UserRole.class);
            userRole.setUser(joes);
            userRole.setRole(role);
            joes.getUserRoles().add(userRole);

            daoHelper.persistOrMerge(role);
            daoHelper.persistOrMerge(userRole);
            daoHelper.persistOrMerge(joes);

            //Simple
            Role simpleRole = metadata.create(Role.class);
            simpleRole.setName("Simple role");

            MatchingRuleOrder simpleOrder = metadata.create(MatchingRuleOrder.class);
            simpleOrder.setOrder(2);

            MatchingRuleStatus simpleStatus = metadata.create(MatchingRuleStatus.class);
            simpleStatus.setIsActive(true);

            SimpleRuleCondition simpleRuleCondition2 = metadata.create(SimpleRuleCondition.class);
            simpleRuleCondition2.setAttribute("mail");
            simpleRuleCondition2.setAttributeValue("joes@example.com");

            SimpleMatchingRule simpleMatchingRule = metadata.create(SimpleMatchingRule.class);
            simpleMatchingRule.setStatus(simpleStatus);
            simpleMatchingRule.setOrder(simpleOrder);
            simpleRuleCondition2.setSimpleMatchingRule(simpleMatchingRule);
            simpleMatchingRule.getConditions().add(simpleRuleCondition2);
            simpleMatchingRule.setAccessGroup(testGroup2);
            simpleMatchingRule.getRoles().add(simpleRole);

            daoHelper.persistOrMerge(simpleRole);
            daoHelper.persistOrMerge(simpleMatchingRule);

            //Scripting
            MatchingRuleStatus scriptingStatus = metadata.create(MatchingRuleStatus.class);
            scriptingStatus.setIsActive(true);
            Role scriptingRole2 = metadata.create(Role.class);
            scriptingRole2.setName("Scripting role 2");

            MatchingRuleOrder scriptingOrder2 = metadata.create(MatchingRuleOrder.class);
            scriptingOrder2.setOrder(3);

            MatchingRuleStatus scriptingStatus2 = metadata.create(MatchingRuleStatus.class);
            scriptingStatus.setIsActive(true);

            ScriptingMatchingRule scriptingMatchingRule2 = metadata.create(ScriptingMatchingRule.class);
            scriptingMatchingRule2.setAccessGroup(testGroup2);
            scriptingMatchingRule2.setStatus(scriptingStatus2);
            scriptingMatchingRule2.setOrder(scriptingOrder2);
            scriptingMatchingRule2.setScriptingCondition("{ldapContext}.ldapUser.login=='joes'");
            scriptingMatchingRule2.getRoles().add(scriptingRole2);
            daoHelper.persistOrMerge(scriptingRole2);
            daoHelper.persistOrMerge(scriptingMatchingRule2);

            persistence.getEntityManager().flush();
            persistence.getEntityManager().getDelegate().detach(joes);
            persistence.getEntityManager().getDelegate().detach(role);
            persistence.getEntityManager().getDelegate().detach(userRole);

            UserSynchronizationResultDto userSynchronizationResultDto = userSynchronizationService.synchronizeUser("joes", true, null, null, null);

            assertEquals(true, userSynchronizationResultDto.isUserPrivilegesChanged());

            User updated = cubaUserDao.getCubaUserByLogin("joes");

            assertEquals(false, updated.getActive());

            assertEquals("Test group 1", updated.getGroup().getName());
            assertEquals(0, updated.getUserRoles().size());//if user disabled all roles clear
            //assertEquals(true, updated.getUserRoles().stream().anyMatch(ur -> ur.getRole().getName().equals("Initial role")));

            List<UserSynchronizationLog> logs = userSynchronizationLogDao.getByLogin("joes");

            assertEquals(1, logs.size());
            assertEquals(UserSynchronizationResultEnum.SUCCESS_SYNC, logs.get(0).getResult());
        }
    }

    @Test
    public void disabledLoginTest() {
        try (Transaction tx = persistence.createTransaction()) {
            LdapConfig ldapConfig = ldapConfigDao.getLdapConfig();
            ldapConfig.setLoginAttribute("uid");
            ldapConfig.setInactiveUserAttribute("roomNumber");
            daoHelper.persistOrMerge(ldapConfig);

            //Custom
            MatchingRuleOrder customOrder = metadata.create(MatchingRuleOrder.class);
            customOrder.setOrder(1);
            customOrder.setCustomMatchingRuleId("com.haulmont.addon.ldap.core.custom.TestCustomLdapRule");

            MatchingRuleStatus customStatus = metadata.create(MatchingRuleStatus.class);
            customStatus.setCustomMatchingRuleId("com.haulmont.addon.ldap.core.custom.TestCustomLdapRule");
            customStatus.setIsActive(true);

            daoHelper.persistOrMerge(customOrder);
            daoHelper.persistOrMerge(customStatus);

            Group testGroup1 = metadata.create(Group.class);
            testGroup1.setName("Test group 1");

            daoHelper.persistOrMerge(testGroup1);

            Role role = metadata.create(Role.class);
            role.setName("Initial role");

            User joes = cubaUserDao.getCubaUserByLogin("joes");
            joes.setGroup(testGroup1);

            UserRole userRole = metadata.create(UserRole.class);
            userRole.setUser(joes);
            userRole.setRole(role);
            joes.getUserRoles().add(userRole);

            joes.setActive(false);

            daoHelper.persistOrMerge(role);
            daoHelper.persistOrMerge(userRole);
            daoHelper.persistOrMerge(joes);

            //Simple
            Role simpleRole = metadata.create(Role.class);
            simpleRole.setName("Simple role");

            MatchingRuleOrder simpleOrder = metadata.create(MatchingRuleOrder.class);
            simpleOrder.setOrder(2);

            MatchingRuleStatus simpleStatus = metadata.create(MatchingRuleStatus.class);
            simpleStatus.setIsActive(true);

            SimpleRuleCondition simpleRuleCondition2 = metadata.create(SimpleRuleCondition.class);
            simpleRuleCondition2.setAttribute("mail");
            simpleRuleCondition2.setAttributeValue("joes@example.com");

            SimpleMatchingRule simpleMatchingRule = metadata.create(SimpleMatchingRule.class);
            simpleMatchingRule.setStatus(simpleStatus);
            simpleMatchingRule.setOrder(simpleOrder);
            simpleRuleCondition2.setSimpleMatchingRule(simpleMatchingRule);
            simpleMatchingRule.getConditions().add(simpleRuleCondition2);
            simpleMatchingRule.setAccessGroup(testGroup1);
            simpleMatchingRule.getRoles().add(simpleRole);

            daoHelper.persistOrMerge(simpleRole);
            daoHelper.persistOrMerge(simpleMatchingRule);

            persistence.getEntityManager().flush();

            UserSynchronizationResultDto userSynchronizationResultDto = userSynchronizationService.synchronizeUser("joes", true, null, null, null);

            assertEquals(true, userSynchronizationResultDto.isUserPrivilegesChanged());

            User updated = cubaUserDao.getCubaUserByLogin("joes");

            assertEquals(false, updated.getActive());

            assertEquals("Test group 1", updated.getGroup().getName());
            assertEquals(1, updated.getUserRoles().size());
            assertEquals(true, updated.getUserRoles().stream().anyMatch(ur -> ur.getRole().getName().equals("Initial role")));

            List<UserSynchronizationLog> logs = userSynchronizationLogDao.getByLogin("joes");

            assertEquals(1, logs.size());
            assertEquals(UserSynchronizationResultEnum.DISABLED_USER_TRY_LOGIN, logs.get(0).getResult());
        }
    }

    @Test
    public void noPrivilegesChangedTest() {
        try (Transaction tx = persistence.createTransaction()) {
            LdapConfig ldapConfig = ldapConfigDao.getLdapConfig();
            ldapConfig.setLoginAttribute("uid");
            ldapConfig.setInactiveUserAttribute("roomNumber");
            daoHelper.persistOrMerge(ldapConfig);

            //Custom
            MatchingRuleOrder customOrder = metadata.create(MatchingRuleOrder.class);
            customOrder.setOrder(1);
            customOrder.setCustomMatchingRuleId("com.haulmont.addon.ldap.core.custom.TestCustomLdapRule");

            MatchingRuleStatus customStatus = metadata.create(MatchingRuleStatus.class);
            customStatus.setCustomMatchingRuleId("com.haulmont.addon.ldap.core.custom.TestCustomLdapRule");
            customStatus.setIsActive(true);

            daoHelper.persistOrMerge(customOrder);
            daoHelper.persistOrMerge(customStatus);

            Group testGroup1 = metadata.create(Group.class);
            testGroup1.setName("Test group 1");

            daoHelper.persistOrMerge(testGroup1);

            Role role = metadata.create(Role.class);
            role.setName("Initial role");

            User bobh = cubaUserDao.getCubaUserByLogin("bobh");
            bobh.setGroup(testGroup1);

            UserRole userRole = metadata.create(UserRole.class);
            userRole.setUser(bobh);
            userRole.setRole(role);
            bobh.getUserRoles().add(userRole);

            daoHelper.persistOrMerge(role);
            daoHelper.persistOrMerge(userRole);
            daoHelper.persistOrMerge(bobh);

            //Simple
            MatchingRuleOrder simpleOrder = metadata.create(MatchingRuleOrder.class);
            simpleOrder.setOrder(2);

            MatchingRuleStatus simpleStatus = metadata.create(MatchingRuleStatus.class);
            simpleStatus.setIsActive(true);

            SimpleRuleCondition simpleRuleCondition2 = metadata.create(SimpleRuleCondition.class);
            simpleRuleCondition2.setAttribute("mail");
            simpleRuleCondition2.setAttributeValue("bobh@example.com");

            SimpleMatchingRule simpleMatchingRule = metadata.create(SimpleMatchingRule.class);
            simpleMatchingRule.setStatus(simpleStatus);
            simpleMatchingRule.setOrder(simpleOrder);
            simpleRuleCondition2.setSimpleMatchingRule(simpleMatchingRule);
            simpleMatchingRule.getConditions().add(simpleRuleCondition2);
            simpleMatchingRule.setAccessGroup(testGroup1);
            simpleMatchingRule.getRoles().add(role);

            daoHelper.persistOrMerge(simpleMatchingRule);

            persistence.getEntityManager().flush();

            UserSynchronizationResultDto userSynchronizationResultDto = userSynchronizationService.synchronizeUser("bobh", true, null, null, null);

            assertEquals(false, userSynchronizationResultDto.isUserPrivilegesChanged());

            User updated = cubaUserDao.getCubaUserByLogin("bobh");

            assertEquals(true, updated.getActive());

            assertEquals("Test group 1", updated.getGroup().getName());
            assertEquals(1, updated.getUserRoles().size());
            assertEquals(true, updated.getUserRoles().stream().anyMatch(ur -> ur.getRole().getName().equals("Initial role")));

            List<UserSynchronizationLog> logs = userSynchronizationLogDao.getByLogin("bobh");

            assertEquals(1, logs.size());
            assertEquals(UserSynchronizationResultEnum.SUCCESS_SYNC, logs.get(0).getResult());
        }
    }
}