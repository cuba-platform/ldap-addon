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
import com.haulmont.addon.ldap.core.rule.LdapMatchingRuleContext;
import com.haulmont.addon.ldap.core.rule.appliers.MatchingRuleApplier;
import com.haulmont.addon.ldap.dto.LdapUser;
import com.haulmont.addon.ldap.dto.UserSynchronizationResultDto;
import com.haulmont.addon.ldap.entity.*;
import com.haulmont.addon.ldap.service.UserSynchronizationService;
import com.haulmont.addon.ldap.utils.MatchingRuleUtils;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.Role;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserRole;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class MatchingRuleTest {

//    private static final String CUSTOM_RULE_ID = "com.haulmont.addon.ldap.core.custom.TestCustomLdapRule";
//
//    @ClassRule
//    public static LdapTestContainer cont = LdapTestContainer.Common.INSTANCE;
//
//    private Metadata metadata;
//    private Persistence persistence;
//    private MatchingRuleDao matchingRuleDao;
//    private DaoHelper daoHelper;
//    private CubaUserDao cubaUserDao;
//    private UserSynchronizationService userSynchronizationService;
//    private MatchingRuleApplier matchingRuleApplier;
//    private UserSynchronizationLogDao userSynchronizationLogDao;
//    private LdapConfigDao ldapConfigDao;
//    private MatchingRuleUtils matchingRuleUtils;
//
//    private static LdapUser createLdapUser(String login) {
//        LdapUser ldapUser = new LdapUser(new HashMap<>());
//        ldapUser.setLogin(login);
//        return ldapUser;
//    }
//
//    private static boolean hasRole(@NotNull User user, @NotNull String roleName) {
//        return user.getUserRoles().stream().anyMatch(userRole -> checkRoleName(userRole, roleName));
//    }
//
//    private static boolean checkRoleName(@NotNull UserRole userRole, @NotNull String name) {
//        return name.equals(userRole.getRole().getName());
//    }
//
//    @Before
//    public void setUp() {
//        matchingRuleDao = AppBeans.get(MatchingRuleDao.class);
//        metadata = AppBeans.get(Metadata.class);
//        persistence = AppBeans.get(Persistence.class);
//        daoHelper = AppBeans.get(DaoHelper.class);
//        cubaUserDao = AppBeans.get(CubaUserDao.class);
//        userSynchronizationService = AppBeans.get(UserSynchronizationService.class);
//        matchingRuleApplier = AppBeans.get(MatchingRuleApplier.class);
//        userSynchronizationLogDao = AppBeans.get(UserSynchronizationLogDao.class);
//        ldapConfigDao = AppBeans.get(LdapConfigDao.class);
//        matchingRuleUtils = AppBeans.get(MatchingRuleUtils.class);
//    }
//
//    @After
//    public void setupDefaultDbState() {
//        cont.setupDefaultDbState();
//    }
//
//    @Test
//    public void simpleRuleTest() {
//        try (Transaction ignored = persistence.createTransaction()) {
//            prepareSimpleTest();
//            List<CommonMatchingRule> rules = matchingRuleDao.getMatchingRules();
//            assertEquals(4, rules.size());
//            assertEquals(MatchingRuleType.SIMPLE, rules.get(0).getRuleType());
//            assertEquals(MatchingRuleType.CUSTOM, rules.get(1).getRuleType());
//            assertEquals(MatchingRuleType.SCRIPTING, rules.get(2).getRuleType());
//            assertEquals(MatchingRuleType.DEFAULT, rules.get(3).getRuleType());
//            SimpleMatchingRule simpleMatchingRule = (SimpleMatchingRule) rules.get(0);
//            assertEquals("login", simpleMatchingRule.getConditions().get(0).getAttribute());
//
//
//        }
//    }
//
//    /**
//     * Created 2 rules that should not be applied due to conditions.
//     * Thus default rule should be applied
//     */
//    @Test
//    public void testDefaultRule() {
//        try (Transaction ignored = persistence.createTransaction()) {
//            LdapConfig ldapConfig = ldapConfigDao.getDefaultLdapConfig();
//            ldapConfig.setLoginAttribute("uid");
//            daoHelper.persistOrMerge(ldapConfig);
//
//            //Custom rule
//            createAndPersistCustomRule(CUSTOM_RULE_ID, 1);
//
//            User joes = cubaUserDao.getOrCreateCubaUser("joes");
//            Group initialGroup = createGroup("Initial group", daoHelper::persistOrMerge);
//            joes.setGroup(initialGroup);
//            joes.setLoginLowerCase("joes");
//            daoHelper.persistOrMerge(joes);
//
//            //Simple rule
//            Role simpleRole = createRole("Simple role");
//            Group simpleRuleGroup = createGroup("Simple group", daoHelper::persistOrMerge);
//            SimpleRuleCondition simpleRuleCondition = createSimpleRuleCondition("uid", "im_not_joes");
//            SimpleMatchingRule simpleMatchingRule = createSimpleRule(true, 2, simpleRuleGroup, simpleRole, simpleRuleCondition);
//            daoHelper.persistOrMerge(simpleRole);
//            daoHelper.persistOrMerge(simpleMatchingRule);
//            DefaultMatchingRule defaultRule = matchingRuleDao.getMatchingRules().stream()
//                    .filter(rule -> rule instanceof DefaultMatchingRule)
//                    .map(rule -> (DefaultMatchingRule) rule)
//                    .findFirst()
//                    .orElseThrow(() -> new RuntimeException("Default rule is not defined"));
//            defaultRule.setIsOverrideExistingAccessGroup(true);
//            defaultRule.setIsOverrideExistingRoles(true);
//
//            persistence.getEntityManager().getDelegate().flush();
//
//            List<CommonMatchingRule> rules = matchingRuleDao.getMatchingRules();
//            assertEquals(3, rules.size());
//
//            persistence.getEntityManager().getDelegate().clear();
//
//            LdapUser ldapUser = createLdapUser("joes");
//            LdapMatchingRuleContext ldapMatchingRuleContext = new LdapMatchingRuleContext(ldapUser, joes, matchingRuleUtils.getRoles(joes), joes.getGroup());
//            matchingRuleApplier.applyMatchingRules(matchingRuleDao.getMatchingRules(), ldapMatchingRuleContext, joes);
//
//            assertEquals(1, ldapMatchingRuleContext.getAppliedRules().size());
//            assertTrue(ldapMatchingRuleContext.getAppliedRules().stream()
//                    .allMatch(mr -> MatchingRuleType.DEFAULT == mr.getRuleType()));
//        }
//    }
//
//    @Test
//    public void testTerminalAttribute() {
//        try (Transaction ignored = persistence.createTransaction()) {
//            prepareTerminalAttributeTest(true, "joes", true);
//
//            User joes = cubaUserDao.getOrCreateCubaUser("joes");
//
//            LdapUser ldapUser = createLdapUser("joes");
//
//            LdapMatchingRuleContext ldapMatchingRuleContext = new LdapMatchingRuleContext(ldapUser, joes);
//            matchingRuleApplier.applyMatchingRules(matchingRuleDao.getMatchingRules(), ldapMatchingRuleContext, joes);
//
//            assertEquals(1, ldapMatchingRuleContext.getAppliedRules().size());
//            assertTrue(ldapMatchingRuleContext.getAppliedRules().stream().allMatch(mr -> MatchingRuleType.SCRIPTING == mr.getRuleType()));
//            assertEquals("Test group joes", joes.getGroup().getName());
//            assertEquals(1, joes.getUserRoles().size());
//            assertEquals("Scripting role 1 joes", joes.getUserRoles().get(0).getRole().getName());
//            assertTrue(ldapMatchingRuleContext.isTerminalRuleApply());
//
//            cubaUserDao.saveCubaUser(joes, joes, ldapMatchingRuleContext);
//            persistence.getEntityManager().flush();
//            prepareTerminalAttributeTest(false, "bena", false);
//
//            User bena = cubaUserDao.getOrCreateCubaUser("bena");
//
//            ldapUser = new LdapUser(new HashMap<>());
//            ldapUser.setLogin("bena");
//
//            ldapMatchingRuleContext = new LdapMatchingRuleContext(ldapUser, bena);
//            matchingRuleApplier.applyMatchingRules(matchingRuleDao.getMatchingRules(), ldapMatchingRuleContext, bena);
//
//            assertEquals(2, ldapMatchingRuleContext.getAppliedRules().size());
//            assertTrue(ldapMatchingRuleContext.getAppliedRules().stream().allMatch(mr -> MatchingRuleType.SCRIPTING == mr.getRuleType()));
//            assertEquals("Test group bena", bena.getGroup().getName());
//            assertEquals(2, bena.getUserRoles().size());
//            assertTrue(bena.getUserRoles().stream().anyMatch(ur -> ur.getRole().getName().equals("Scripting role 1 bena")));
//            assertTrue(bena.getUserRoles().stream().anyMatch(ur -> ur.getRole().getName().equals("Scripting role 2 bena")));
//            assertFalse(ldapMatchingRuleContext.isTerminalRuleApply());
//
//            cubaUserDao.saveCubaUser(bena, bena, ldapMatchingRuleContext);
//        }
//    }
//
//    @Test
//    public void testOverrideAttribute() {
//        try (Transaction ignored = persistence.createTransaction()) {
//            createRulesForOverrideAttributeTest(true, "joes", true);
//
//            final User joes = cubaUserDao.getOrCreateCubaUser("joes");
//            final Group joesInitialGroup = createGroup("Joes initial group", daoHelper::persistOrMerge);
//            final Role joesInitialRole = createRole("Joes initial role");
//            assignGroupAndRolesThenPersist(joes, joesInitialGroup, joesInitialRole);
//
//            final LdapMatchingRuleContext ldapMatchingRuleContext = createLdapMatchingRuleContext(createLdapUser("joes"), joes);
//            matchingRuleApplier.applyMatchingRules(matchingRuleDao.getMatchingRules(), ldapMatchingRuleContext, joes);
//
//            assertEquals(2, ldapMatchingRuleContext.getAppliedRules().size());
//            assertTrue(ldapMatchingRuleContext.getAppliedRules().stream().allMatch(mr -> MatchingRuleType.SCRIPTING == mr.getRuleType()));
//            assertEquals("Test group 2", joes.getGroup().getName());
//            assertEquals(1, joes.getUserRoles().size());
//            assertTrue(hasRole(joes, "Scripting role 2"));
//        }
//    }
//
//    @Test
//    public void testNotOverrideAttribute() {
//        try (Transaction ignored = persistence.createTransaction()) {
//            createRulesForOverrideAttributeTest(false, "bena", true);
//
//            User bena = cubaUserDao.getOrCreateCubaUser("bena");
//            Group benaInitialGroup = createGroup("Bena initial group", daoHelper::persistOrMerge);
//            Role benaInitialRole = createRole("Bena initial role");
//            assignGroupAndRolesThenPersist(bena, benaInitialGroup, benaInitialRole);
//
//            final LdapMatchingRuleContext benaMatchingRuleContext = createLdapMatchingRuleContext(createLdapUser("bena"), bena);
//            matchingRuleApplier.applyMatchingRules(matchingRuleDao.getMatchingRules(), benaMatchingRuleContext, bena);
//
//            assertEquals(2, benaMatchingRuleContext.getAppliedRules().size());
//            assertTrue(benaMatchingRuleContext.getAppliedRules().stream().allMatch(mr -> MatchingRuleType.SCRIPTING == mr.getRuleType()));
//            assertEquals(benaInitialGroup.getName(), bena.getGroup().getName());
//            assertEquals(3, bena.getUserRoles().size());
//            assertTrue(hasRole(bena, benaInitialRole.getName()));
//            assertTrue(hasRole(bena, "Scripting role 1"));
//            assertTrue(hasRole(bena, "Scripting role 2"));
//        }
//    }
//
//    private void prepareSimpleTest() {
//        Group testGroup = metadata.create(Group.class);
//        testGroup.setName("Test group simple");
//        testGroup = daoHelper.persistOrMerge(testGroup);
//
//        //Custom
//        MatchingRuleOrder customOrder = metadata.create(MatchingRuleOrder.class);
//        customOrder.setOrder(2);
//        customOrder.setCustomMatchingRuleId("com.haulmont.addon.ldap.core.custom.TestCustomLdapRule");
//
//        MatchingRuleStatus customStatus = metadata.create(MatchingRuleStatus.class);
//        customStatus.setCustomMatchingRuleId("com.haulmont.addon.ldap.core.custom.TestCustomLdapRule");
//        customStatus.setIsActive(true);
//
//        daoHelper.persistOrMerge(customOrder);
//        daoHelper.persistOrMerge(customStatus);
//
//        //Simple
//        Role simpleRole = metadata.create(Role.class);
//        simpleRole.setName("Simple role");
//
//        MatchingRuleOrder simpleOrder = metadata.create(MatchingRuleOrder.class);
//        simpleOrder.setOrder(1);
//
//        MatchingRuleStatus simpleStatus = metadata.create(MatchingRuleStatus.class);
//        simpleStatus.setIsActive(true);
//
//        SimpleRuleCondition simpleRuleCondition = metadata.create(SimpleRuleCondition.class);
//        simpleRuleCondition.setAttribute("login");
//        simpleRuleCondition.setAttributeValue("barts");
//
//        SimpleMatchingRule simpleMatchingRule = metadata.create(SimpleMatchingRule.class);
//        simpleMatchingRule.setStatus(simpleStatus);
//        simpleMatchingRule.setOrder(simpleOrder);
//        simpleRuleCondition.setSimpleMatchingRule(simpleMatchingRule);
//        simpleMatchingRule.getConditions().add(simpleRuleCondition);
//        simpleMatchingRule.setAccessGroup(testGroup);
//        simpleMatchingRule.getRoles().add(simpleRole);
//        simpleMatchingRule.updateRolesList();
//
//        daoHelper.persistOrMerge(simpleRole);
//        daoHelper.persistOrMerge(simpleMatchingRule);
//
//        //Scripting
//        Role scriptingRole = metadata.create(Role.class);
//        scriptingRole.setName("Scripting role");
//
//        MatchingRuleOrder scriptingOrder = metadata.create(MatchingRuleOrder.class);
//        scriptingOrder.setOrder(3);
//
//        MatchingRuleStatus scriptingStatus = metadata.create(MatchingRuleStatus.class);
//        scriptingStatus.setIsActive(false);
//
//        ScriptingMatchingRule scriptingMatchingRule = metadata.create(ScriptingMatchingRule.class);
//        scriptingMatchingRule.setAccessGroup(testGroup);
//        scriptingMatchingRule.setStatus(scriptingStatus);
//        scriptingMatchingRule.setOrder(scriptingOrder);
//        scriptingMatchingRule.setScriptingCondition("{ldapContext}.ldapUser.login=='admin'");
//        scriptingMatchingRule.getRoles().add(scriptingRole);
//        scriptingMatchingRule.updateRolesList();
//        daoHelper.persistOrMerge(scriptingRole);
//        daoHelper.persistOrMerge(scriptingMatchingRule);
//
//        persistence.getEntityManager().flush();
//
//        persistence.getEntityManager().getDelegate().clear();
//
//    }
//
//    private void prepareTerminalAttributeTest(boolean terminal, String login, boolean createCustom) {
//
//        persistence.getEntityManager().getDelegate().clear();
//
//        Group testGroup = metadata.create(Group.class);
//        testGroup.setName("Test group " + login);
//        daoHelper.persistOrMerge(testGroup);
//
//        //Custom
//        if (createCustom) {
//            MatchingRuleOrder customOrder = metadata.create(MatchingRuleOrder.class);
//            customOrder.setOrder(1);
//            customOrder.setCustomMatchingRuleId("com.haulmont.addon.ldap.core.custom.TestCustomLdapRule");
//
//            MatchingRuleStatus customStatus = metadata.create(MatchingRuleStatus.class);
//            customStatus.setCustomMatchingRuleId("com.haulmont.addon.ldap.core.custom.TestCustomLdapRule");
//            customStatus.setIsActive(true);
//
//            daoHelper.persistOrMerge(customOrder);
//            daoHelper.persistOrMerge(customStatus);
//        }
//
//        //Scripting 1
//        Role scriptingRole1 = metadata.create(Role.class);
//        scriptingRole1.setName("Scripting role 1 " + login);
//
//        MatchingRuleOrder scriptingOrder1 = metadata.create(MatchingRuleOrder.class);
//        scriptingOrder1.setOrder(2);
//
//        MatchingRuleStatus scriptingStatus1 = metadata.create(MatchingRuleStatus.class);
//        scriptingStatus1.setIsActive(true);
//
//        ScriptingMatchingRule scriptingMatchingRule1 = metadata.create(ScriptingMatchingRule.class);
//        scriptingMatchingRule1.setAccessGroup(testGroup);
//        scriptingMatchingRule1.setStatus(scriptingStatus1);
//        scriptingMatchingRule1.setOrder(scriptingOrder1);
//        scriptingMatchingRule1.setScriptingCondition("{ldapContext}.ldapUser.login=='" + login + "'");
//        scriptingMatchingRule1.getRoles().add(scriptingRole1);
//        scriptingMatchingRule1.updateRolesList();
//        scriptingMatchingRule1.setIsTerminalRule(terminal);
//        daoHelper.persistOrMerge(scriptingRole1);
//        daoHelper.persistOrMerge(scriptingMatchingRule1);
//
//        //Scripting 2
//        Role scriptingRole2 = metadata.create(Role.class);
//        scriptingRole2.setName("Scripting role 2 " + login);
//
//        MatchingRuleOrder scriptingOrder2 = metadata.create(MatchingRuleOrder.class);
//        scriptingOrder2.setOrder(3);
//
//        MatchingRuleStatus scriptingStatus2 = metadata.create(MatchingRuleStatus.class);
//        scriptingStatus2.setIsActive(true);
//
//        ScriptingMatchingRule scriptingMatchingRule2 = metadata.create(ScriptingMatchingRule.class);
//        scriptingMatchingRule2.setAccessGroup(testGroup);
//        scriptingMatchingRule2.setStatus(scriptingStatus2);
//        scriptingMatchingRule2.setOrder(scriptingOrder2);
//        scriptingMatchingRule2.setScriptingCondition("{ldapContext}.ldapUser.login=='" + login + "'");
//        scriptingMatchingRule2.getRoles().add(scriptingRole2);
//        scriptingMatchingRule2.updateRolesList();
//        daoHelper.persistOrMerge(scriptingRole2);
//        daoHelper.persistOrMerge(scriptingMatchingRule2);
//
//        persistence.getEntityManager().flush();
//    }
//
//    private void createRulesForOverrideAttributeTest(boolean override, String login, boolean createCustom) {
//        persistence.getEntityManager().getDelegate().clear();
//
//        //Custom rule
//        if (createCustom) {
//            MatchingRuleOrder customOrder = createOrder(1, CUSTOM_RULE_ID);
//            MatchingRuleStatus customStatus = createStatus(true, CUSTOM_RULE_ID);
//            daoHelper.persistOrMerge(customOrder);
//            daoHelper.persistOrMerge(customStatus);
//        }
//
//        //Scripting rule 1
//        Role scriptingRole1 = createRole("Scripting role 1");
//        Group scriptingGroup1 = createGroup("Test group 1", daoHelper::persistOrMerge);
//        ScriptingMatchingRule scriptingMatchingRule1 = createScriptingRule(scriptingGroup1, true, 2,
//                "{ldapContext}.ldapUser.login=='" + login + "'", scriptingRole1);
//        scriptingMatchingRule1.setIsOverrideExistingAccessGroup(override);
//        scriptingMatchingRule1.setIsOverrideExistingRoles(override);
//
//        daoHelper.persistOrMerge(scriptingRole1);
//        daoHelper.persistOrMerge(scriptingMatchingRule1);
//
//        //Scripting rule 2
//        Role scriptingRole2 = metadata.create(Role.class);
//        Group scriptingGroup2 = createGroup("Test group 2", daoHelper::persistOrMerge);
//        scriptingRole2.setName("Scripting role 2");
//        ScriptingMatchingRule scriptingMatchingRule2 = createScriptingRule(scriptingGroup2, true, 3,
//                "{ldapContext}.ldapUser.login=='" + login + "'", scriptingRole2);
//        scriptingMatchingRule2.setIsOverrideExistingAccessGroup(override);
//        scriptingMatchingRule2.setIsOverrideExistingRoles(override);
//        daoHelper.persistOrMerge(scriptingRole2);
//        daoHelper.persistOrMerge(scriptingMatchingRule2);
//
//        persistence.getEntityManager().flush();
//    }
//
//    @Test
//    public void intActiveUserTest() {
//        try (Transaction tx = persistence.createTransaction()) {
//            //Create ldap config
//            daoHelper.persistOrMerge(setupLdapConfig("uid", "roomNumber", "initials"));
//
//            //Custom
//            daoHelper.persistOrMerge(createOrder(1, CUSTOM_RULE_ID));
//            daoHelper.persistOrMerge(createStatus(true, CUSTOM_RULE_ID));
//
//            Group testGroup1 = createGroup("Test group 1", daoHelper::persistOrMerge);
//            Group testGroup2 = createGroup("Test group 2", daoHelper::persistOrMerge);
//
//            //Get 'barts' user then set group and 'Initial role'
//            User barts = cubaUserDao.getOrCreateCubaUser("barts");
//            barts.setGroup(testGroup1);
//            Role initialRole = createRole("Initial role");
//            UserRole userRole = addUserRole(initialRole, barts);
//            daoHelper.persistOrMerge(initialRole);
//            daoHelper.persistOrMerge(userRole);
//            daoHelper.persistOrMerge(barts);
//
//            //Simple
//            Role simpleRole = createRole("Simple role");
//            SimpleRuleCondition simpleRuleCondition1 = createSimpleRuleCondition("givenName", "Bart");
//            SimpleRuleCondition simpleRuleCondition2 = createSimpleRuleCondition("mail", "barts@example.com");
//            SimpleMatchingRule simpleMatchingRule = createSimpleRule(true, 2, testGroup2,
//                    simpleRole, simpleRuleCondition1, simpleRuleCondition2);
//            simpleMatchingRule.setUuid(UUID.fromString("a8ba61d2-c0a3-2a78-3322-c4f754807599"));
//            daoHelper.persistOrMerge(simpleRole);
//            daoHelper.persistOrMerge(simpleMatchingRule);
//
//            //Scripting
//            Role scriptingRole = createRole("Scripting role");
//            String ruleExpression = "{ldapContext}.ldapUser.login=='barts'";
//            ScriptingMatchingRule scriptingMatchingRule = createScriptingRule(testGroup1, createStatus(true),
//                    createOrder(4), ruleExpression, scriptingRole);
//            scriptingMatchingRule.setUuid(UUID.fromString("d5ce5200-2274-1c09-8b03-4f16a2f8e73e"));
//            daoHelper.persistOrMerge(scriptingRole);
//            daoHelper.persistOrMerge(scriptingMatchingRule);
//
//            //Scripting2
//            Role scriptingRole2 = createRole("Scripting role 2");
//            String ruleExpression2 = "{ldapContext}.ldapUser.login=='joes'";
//            ScriptingMatchingRule scriptingMatchingRule2 = createScriptingRule(testGroup2, createStatus(true),
//                    createOrder(3), ruleExpression2, scriptingRole2);
//            daoHelper.persistOrMerge(scriptingRole2);
//            daoHelper.persistOrMerge(scriptingMatchingRule2);
//
//            tx.commit();
//
//            // Do 'barts' sync
//            UserSynchronizationResultDto userSynchronizationResultDto =
//                    syncUser("barts", true);
//
//            assertTrue(userSynchronizationResultDto.isUserPrivilegesChanged());
//
//            User syncedUser = cubaUserDao.getOrCreateCubaUser("barts");
//
//            // Check synced user attrs
//            assertEquals("barts@example.com", syncedUser.getEmail());
//            assertEquals("Bart Smeth", syncedUser.getName());
//            assertEquals("Bart", syncedUser.getFirstName());
//            assertEquals("Smeth", syncedUser.getLastName());
//            assertEquals("Petrovich", syncedUser.getMiddleName());
//            assertEquals("ru", syncedUser.getLanguage());
//            assertEquals("Manager", syncedUser.getPosition());
//            assertEquals(true, syncedUser.getActive());
//            assertEquals("Company", syncedUser.getGroup().getName());
//            assertEquals(4, syncedUser.getUserRoles().size());
//            assertTrue(syncedUser.getUserRoles().stream().anyMatch(ur -> ur.getRole() != null && ur.getRole().getName().equals("Initial role")));
//            assertTrue(syncedUser.getUserRoles().stream().anyMatch(ur -> ur.getRole() != null && ur.getRole().getName().equals("Simple role")));
//            assertTrue(syncedUser.getUserRoles().stream().anyMatch(ur -> ur.getRole() != null && ur.getRole().getName().equals("Scripting role")));
//
//            List<UserSynchronizationLog> logs = userSynchronizationLogDao.getByLogin("barts");
//            assertEquals(1, logs.size());
//            assertEquals(UserSynchronizationResultEnum.SUCCESS_SYNC, logs.get(0).getResult());
//            assertEquals("Initial role\n" +
//                            "system-minimal",
//                    logs.get(0).getRolesBefore());
//            assertEquals("Initial role\n" +
//                    "system-minimal\n" +
//                    "Simple role\n" +
//                    "Scripting role", logs.get(0).getRolesAfter());
//            assertEquals("Test group 1", logs.get(0).getAccessGroupBefore());
//            assertEquals("Company", logs.get(0).getAccessGroupAfter());
//            assertEquals("1)Custom. Test custom Rule(com.haulmont.addon.ldap.core.custom.TestCustomLdapRule)\n" +
//                    "\n" +
//                    "2)Simple. null(a8ba61d2-c0a3-2a78-3322-c4f754807599)\n" +
//                    "   Override existing group: false. Override existing roles: false. Terminal rule: false.\n" +
//                    "   Group: Test group 2.\n" +
//                    "   Roles: Simple role.\n" +
//                    "   Additional rule info: givenName:Bart,mail:barts@example.com\n" +
//                    "3)Scripting. null(d5ce5200-2274-1c09-8b03-4f16a2f8e73e)\n" +
//                    "   Override existing group: false. Override existing roles: false. Terminal rule: false.\n" +
//                    "   Group: Test group 1.\n" +
//                    "   Roles: Scripting role.\n" +
//                    "   Additional rule info: {ldapContext}.ldapUser.login=='barts'\n", logs.get(0).getAppliedRules());
//            assertEquals(UserSynchronizationResultEnum.SUCCESS_SYNC, logs.get(0).getResult());
//        }
//    }
//
//    /**
//     * If user is disabled on Cuba and not disabled on LDAP sync should be performed
//     */
//    @Test
//    public void intInactiveUserTest() {
//        try (Transaction ignored = persistence.createTransaction()) {
//            LdapConfig ldapConfig = ldapConfigDao.getDefaultLdapConfig();
//            ldapConfig.setLoginAttribute("uid");
//            ldapConfig.setInactiveUserAttribute("roomNumber");
//            daoHelper.persistOrMerge(ldapConfig);
//
//            //Custom
//            createAndPersistCustomRule(CUSTOM_RULE_ID, 1);
//
//            //Create user
//            User joes = cubaUserDao.getOrCreateCubaUser("joes");
//            Group testGroup1 = createGroup("Test group 1", daoHelper::persistOrMerge);
//            Role initialRole = createRole("Initial role");
//            UserRole userRole = assignGroupAndRolesThenPersist(joes, testGroup1, initialRole).get(0);
//
//            Group testGroup2 = createGroup("Test group 2", daoHelper::persistOrMerge);
//
//            //Simple
//            Role simpleRole = createRole("Simple role");
//            SimpleRuleCondition simpleRuleCondition2 = createSimpleRuleCondition("mail", "joes@example.com");
//            SimpleMatchingRule simpleMatchingRule = createSimpleRule(true, 2, testGroup2, simpleRole, simpleRuleCondition2);
//            daoHelper.persistOrMerge(simpleRole);
//            daoHelper.persistOrMerge(simpleMatchingRule);
//
//            //Scripting
//            Role scriptingRole2 = createRole("Scripting role 2");
//            ScriptingMatchingRule scriptingMatchingRule2 = createScriptingRule(testGroup2, true, 3,
//                    "{ldapContext}.ldapUser.login=='joes'", scriptingRole2);
//            daoHelper.persistOrMerge(scriptingRole2);
//            daoHelper.persistOrMerge(scriptingMatchingRule2);
//
//            persistence.getEntityManager().flush();
//            persistence.getEntityManager().getDelegate().detach(joes);
//            persistence.getEntityManager().getDelegate().detach(initialRole);
//            persistence.getEntityManager().getDelegate().detach(userRole);
//
//            UserSynchronizationResultDto userSynchronizationResultDto = syncUser("joes", true);
//            assertTrue(userSynchronizationResultDto.isUserPrivilegesChanged());
//
//            User updated = cubaUserDao.getOrCreateCubaUser("joes");
//
//            assertEquals(false, updated.getActive());
//
//            assertEquals("Test group 1", updated.getGroup().getName());
//            assertTrue(hasRole(updated, initialRole.getName()));
//            List<UserSynchronizationLog> logs = userSynchronizationLogDao.getByLogin("joes");
//
//            assertEquals(1, logs.size());
//            assertEquals(UserSynchronizationResultEnum.SUCCESS_SYNC, logs.get(0).getResult());
//        }
//    }
//
//    /**
//     * If user is disabled on LDAP his roles and groups should not be changed and it should be marked as disabled
//     */
//    @Test
//    public void disabledLoginTest() {
//        try (Transaction tx = persistence.createTransaction()) {
//            LdapConfig ldapConfig = ldapConfigDao.getDefaultLdapConfig();
//            ldapConfig.setLoginAttribute("uid");
//            ldapConfig.setInactiveUserAttribute("roomNumber");
//            daoHelper.persistOrMerge(ldapConfig);
//
//            //User
//            User joes = cubaUserDao.getOrCreateCubaUser("joes");
//            joes.setActive(false);
//            Role initialRole = createRole("Initial role");
//            Group initialGroup = createGroup("Initial group", daoHelper::persistOrMerge);
//            assignGroupAndRolesThenPersist(joes, initialGroup, initialRole);
//
//            //Custom rule [no]
//            createAndPersistCustomRule(CUSTOM_RULE_ID, 1);
//
//            //Simple rule [yes]
//            Role simpleRole = createRole("Simple role");
//            Group simpleGroup = createGroup("Simple group", daoHelper::persistOrMerge);
//            SimpleRuleCondition simpleRuleCondition = createSimpleRuleCondition("mail", "joes@example.com");
//            SimpleMatchingRule simpleMatchingRule = createSimpleRule(true, 2, simpleGroup, simpleRole, simpleRuleCondition);
//            simpleMatchingRule.setIsOverrideExistingAccessGroup(true);
//            simpleMatchingRule.setIsOverrideExistingRoles(true);
//            daoHelper.persistOrMerge(simpleRole);
//            daoHelper.persistOrMerge(simpleMatchingRule);
//
//            //persistence.getEntityManager().flush();
//            tx.commit();
//
//            UserSynchronizationResultDto userSynchronizationResultDto = syncUser("joes", true);
//            assertFalse(userSynchronizationResultDto.isUserPrivilegesChanged());
//
//            User updatedUser = cubaUserDao.getOrCreateCubaUser("joes");
//            assertFalse(updatedUser.getActive());
//            assertEquals(initialGroup.getName(), updatedUser.getGroup().getName());
//            assertEquals(2, updatedUser.getUserRoles().size());
//            assertTrue(hasRole(updatedUser, initialRole.getName()));
//
//            List<UserSynchronizationLog> logs = userSynchronizationLogDao.getByLogin("joes");
//            assertEquals(1, logs.size());
//            assertEquals(UserSynchronizationResultEnum.DISABLED_USER_TRY_LOGIN, logs.get(0).getResult());
//        }
//    }
//
//    @Test
//    public void noPrivilegesChangedTest() {
//        try (Transaction tx = persistence.createTransaction()) {
//            LdapConfig ldapConfig = ldapConfigDao.getDefaultLdapConfig();
//            ldapConfig.setLoginAttribute("uid");
//            ldapConfig.setInactiveUserAttribute("roomNumber");
//            daoHelper.persistOrMerge(ldapConfig);
//
//            //Custom
//            createAndPersistCustomRule(CUSTOM_RULE_ID, 1);
//
//            Group testGroup1 = metadata.create(Group.class);
//            testGroup1.setName("Test group 1");
//
//            daoHelper.persistOrMerge(testGroup1);
//
//            Role role = createRole("Initial role");
//
//            User bobh = cubaUserDao.getOrCreateCubaUser("bobh");
//            bobh.setGroup(testGroup1);
//
//            UserRole userRole = addUserRole(role, bobh);
//
//            daoHelper.persistOrMerge(role);
//            daoHelper.persistOrMerge(userRole);
//            daoHelper.persistOrMerge(bobh);
//
//            tx.commit();
//
//            //Simple
//            MatchingRuleOrder simpleOrder = metadata.create(MatchingRuleOrder.class);
//            simpleOrder.setOrder(2);
//
//            MatchingRuleStatus simpleStatus = metadata.create(MatchingRuleStatus.class);
//            simpleStatus.setIsActive(true);
//
//            SimpleRuleCondition simpleRuleCondition2 = createSimpleRuleCondition("mail", "bobh@example.com");
//
//            SimpleMatchingRule simpleMatchingRule = metadata.create(SimpleMatchingRule.class);
//            simpleMatchingRule.setStatus(simpleStatus);
//            simpleMatchingRule.setOrder(simpleOrder);
//            simpleRuleCondition2.setSimpleMatchingRule(simpleMatchingRule);
//            simpleMatchingRule.getConditions().add(simpleRuleCondition2);
//            simpleMatchingRule.setAccessGroup(testGroup1);
//            simpleMatchingRule.getRoles().add(role);
//            simpleMatchingRule.updateRolesList();
//
//            daoHelper.persistOrMerge(simpleMatchingRule);
//
//            tx.commit();
//
//            UserSynchronizationResultDto userSynchronizationResultDto = syncUser("bobh", null, true);
//
//            assertFalse(userSynchronizationResultDto.isUserPrivilegesChanged());
//
//            User updated = cubaUserDao.getOrCreateCubaUser("bobh");
//
//            assertEquals(true, updated.getActive());
//
//            assertEquals("Test group 1", updated.getGroup().getName());
//            assertEquals(2, updated.getUserRoles().size());
//            assertTrue(updated.getUserRoles().stream().anyMatch(ur -> ur.getRole() != null &&
//                    ur.getRole().getName().equals("Initial role")));
//
//            List<UserSynchronizationLog> logs = userSynchronizationLogDao.getByLogin("bobh");
//
//            assertEquals(1, logs.size());
//            assertEquals(UserSynchronizationResultEnum.SUCCESS_SYNC, logs.get(0).getResult());
//        }
//    }
//
//    private void createAndPersistCustomRule(String id, int orderNum) {
//        MatchingRuleOrder customOrder = createOrder(orderNum, id);
//        MatchingRuleStatus customStatus = createStatus(true, id);
//        daoHelper.persistOrMerge(customOrder);
//        daoHelper.persistOrMerge(customStatus);
//    }
//
//    private UserRole addUserRole(Role role, User user) {
//        UserRole userRole = metadata.create(UserRole.class);
//        userRole.setUser(user);
//        userRole.setRole(role);
//        user.getUserRoles().add(userRole);
//        return userRole;
//    }
//
//    private List<UserRole> assignGroupAndRolesThenPersist(User user, Group group, Role... roles) {
//        user.setGroup(group);
//        Arrays.stream(roles).forEach(daoHelper::persistOrMerge);
//        List<UserRole> cratedUserRoles = Arrays.stream(roles)
//                .map(role -> addUserRole(role, user))
//                .collect(Collectors.toList());
//        cratedUserRoles.forEach(daoHelper::persistOrMerge);
//        daoHelper.persistOrMerge(user);
//        return cratedUserRoles;
//    }
//
//    private Role createRole(String s) {
//        Role role = metadata.create(Role.class);
//        role.setName(s);
//        return role;
//    }
//
//    private Group createGroup(String name, Consumer<Group> thenConsume) {
//        Group group = createGroup(name);
//        if (thenConsume != null) {
//            thenConsume.accept(group);
//        }
//        return group;
//    }
//
//    private Group createGroup(String name) {
//        Group group = metadata.create(Group.class);
//        group.setName(name);
//        return group;
//    }
//
//    private MatchingRuleStatus createStatus(boolean active) {
//        return createStatus(active, null);
//    }
//
//    private MatchingRuleStatus createStatus(boolean active, String customMatchingRuleId) {
//        MatchingRuleStatus customStatus = metadata.create(MatchingRuleStatus.class);
//        customStatus.setIsActive(active);
//        if (customMatchingRuleId != null) {
//            customStatus.setCustomMatchingRuleId(customMatchingRuleId);
//        }
//        return customStatus;
//    }
//
//    private LdapConfig setupLdapConfig(String loginAttribute, String inactiveUserAttribute, String middleNameAttribute) {
//        LdapConfig ldapConfig = ldapConfigDao.getDefaultLdapConfig();
//        ldapConfig.setLoginAttribute(loginAttribute);
//        ldapConfig.setInactiveUserAttribute(inactiveUserAttribute);
//        ldapConfig.setMiddleNameAttribute(middleNameAttribute);
//        return ldapConfig;
//    }
//
//    private SimpleRuleCondition createSimpleRuleCondition(String attribute, String value) {
//        SimpleRuleCondition ruleCondition = metadata.create(SimpleRuleCondition.class);
//        ruleCondition.setAttribute(attribute);
//        ruleCondition.setAttributeValue(value);
//        return ruleCondition;
//    }
//
//    private SimpleMatchingRule createSimpleRule(boolean enabled,
//                                                int orderNum,
//                                                Group group,
//                                                Role role,
//                                                SimpleRuleCondition... conditions) {
//        SimpleMatchingRule simpleMatchingRule = metadata.create(SimpleMatchingRule.class);
//        simpleMatchingRule.setStatus(createStatus(enabled));
//        simpleMatchingRule.setOrder(createOrder(orderNum));
//
//        List<SimpleRuleCondition> conditionList = Arrays.asList(conditions);
//        conditionList.forEach(condition -> condition.setSimpleMatchingRule(simpleMatchingRule));
//        simpleMatchingRule.getConditions().addAll(conditionList);
//
//        simpleMatchingRule.setAccessGroup(group);
//        simpleMatchingRule.getRoles().add(role);
//        simpleMatchingRule.updateRolesList();
//        return simpleMatchingRule;
//    }
//
//    private ScriptingMatchingRule createScriptingRule(Group group,
//                                                      boolean active,
//                                                      int order,
//                                                      String conditionExpr,
//                                                      Role... roles) {
//        return createScriptingRule(group, createStatus(active), createOrder(order), conditionExpr, roles);
//    }
//
//    private ScriptingMatchingRule createScriptingRule(Group group,
//                                                      MatchingRuleStatus status,
//                                                      MatchingRuleOrder order,
//                                                      String conditionExpr,
//                                                      Role... roles) {
//        ScriptingMatchingRule matchingRule = metadata.create(ScriptingMatchingRule.class);
//        matchingRule.setAccessGroup(group);
//        matchingRule.setStatus(status);
//        matchingRule.setOrder(order);
//        matchingRule.setScriptingCondition(conditionExpr);
//        matchingRule.getRoles().addAll(Arrays.asList(roles));
//        matchingRule.updateRolesList();
//        return matchingRule;
//    }
//
//    private MatchingRuleOrder createOrder(int order) {
//        return createOrder(order, null);
//    }
//
//    private MatchingRuleOrder createOrder(int order, String matchingRuleId) {
//        MatchingRuleOrder matchingRuleOrder = metadata.create(MatchingRuleOrder.class);
//        matchingRuleOrder.setOrder(order);
//        if (matchingRuleId != null) {
//            matchingRuleOrder.setCustomMatchingRuleId(matchingRuleId);
//        }
//        return matchingRuleOrder;
//    }
//
//    private LdapMatchingRuleContext createLdapMatchingRuleContext(LdapUser ldapUser, User user) {
//        return new LdapMatchingRuleContext(ldapUser, user, matchingRuleUtils.getRoles(user), user.getGroup());
//    }
//
//    private UserSynchronizationResultDto syncUser(String login, @Nullable String tenantId, boolean saveSynchronizationResult) {
//        return userSynchronizationService
//                .synchronizeUser(login, tenantId, saveSynchronizationResult, null, null, null);
//    }
}
