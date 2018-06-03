package com.haulmont.addon.ldap.core;

import com.haulmont.addon.ldap.core.dao.*;
import com.haulmont.addon.ldap.core.dto.LdapUser;
import com.haulmont.addon.ldap.core.rule.LdapMatchingRuleContext;
import com.haulmont.addon.ldap.core.rule.appliers.MatchingRuleApplier;
import com.haulmont.addon.ldap.entity.*;
import com.haulmont.addon.ldap.service.UserSynchronizationService;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.Role;
import com.haulmont.cuba.security.entity.User;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import javax.naming.directory.BasicAttributes;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
//TODO check simple , scripting , custom rule
public class MatchingRuleTest {

    @ClassRule
    public static LdapTestContainer cont = LdapTestContainer.Common.INSTANCE;

    private Metadata metadata;
    private Persistence persistence;
    private MatchingRuleDao matchingRuleDao;
    private DaoHelper daoHelper;
    private CubaUserDao cubaUserDao;
    private UserSynchronizationService userSynchronizationService;
    private MatchingRuleApplier matchingRuleApplier;


    @Before
    public void setUp() throws Exception {
        matchingRuleDao = AppBeans.get(MatchingRuleDao.class);
        metadata = AppBeans.get(Metadata.class);
        persistence = AppBeans.get(Persistence.class);
        daoHelper = AppBeans.get(DaoHelper.class);
        cubaUserDao = AppBeans.get(CubaUserDao.class);
        userSynchronizationService = AppBeans.get(UserSynchronizationService.class);
        matchingRuleApplier = AppBeans.get(MatchingRuleApplier.class);

    }


    @Test
    public void simpleRuleTest() {
        try (Transaction tx = persistence.createTransaction()) {
            prepareSimpleTest();
            List<CommonMatchingRule> rules = matchingRuleDao.getMatchingRules();
            assertEquals(4, rules.size());
            assertEquals(MatchingRuleType.SIMPLE, rules.get(0).getRuleType());
            assertEquals(MatchingRuleType.CUSTOM, rules.get(1).getRuleType());
            assertEquals(MatchingRuleType.SCRIPTING, rules.get(2).getRuleType());
            assertEquals(MatchingRuleType.DEFAULT, rules.get(3).getRuleType());
            SimpleMatchingRule simpleMatchingRule = (SimpleMatchingRule) rules.get(0);
            assertEquals("login", simpleMatchingRule.getConditions().get(0).getAttribute());


        }
    }

    @Test
    public void testDefaultRule() {
        try (Transaction tx = persistence.createTransaction()) {

            //Custom
            MatchingRuleOrder customOrder = metadata.create(MatchingRuleOrder.class);
            customOrder.setOrder(1);
            customOrder.setCustomMatchingRuleId("com.haulmont.addon.ldap.core.custom.TestCustomLdapRule");

            MatchingRuleStatus customStatus = metadata.create(MatchingRuleStatus.class);
            customStatus.setCustomMatchingRuleId("com.haulmont.addon.ldap.core.custom.TestCustomLdapRule");
            customStatus.setIsActive(true);

            daoHelper.persistOrMerge(customOrder);
            daoHelper.persistOrMerge(customStatus);

            Group testGroup = metadata.create(Group.class);
            testGroup.setName("Test group");
            daoHelper.persistOrMerge(testGroup);

            User joes = cubaUserDao.getCubaUserByLogin("joes");
            joes.setGroup(testGroup);
            daoHelper.persistOrMerge(joes);

            Role simpleRole = metadata.create(Role.class);
            simpleRole.setName("Simple role");

            MatchingRuleOrder simpleOrder = metadata.create(MatchingRuleOrder.class);
            simpleOrder.setOrder(2);

            MatchingRuleStatus simpleStatus = metadata.create(MatchingRuleStatus.class);
            simpleStatus.setIsActive(false);

            SimpleRuleCondition simpleRuleCondition = metadata.create(SimpleRuleCondition.class);
            simpleRuleCondition.setAttribute("uid");
            simpleRuleCondition.setAttributeValue("joes");

            SimpleMatchingRule simpleMatchingRule = metadata.create(SimpleMatchingRule.class);
            simpleMatchingRule.setStatus(simpleStatus);
            simpleMatchingRule.setOrder(simpleOrder);
            simpleRuleCondition.setSimpleMatchingRule(simpleMatchingRule);
            simpleMatchingRule.getConditions().add(simpleRuleCondition);
            simpleMatchingRule.setAccessGroup(testGroup);
            simpleMatchingRule.getRoles().add(simpleRole);

            daoHelper.persistOrMerge(simpleRole);
            daoHelper.persistOrMerge(simpleMatchingRule);

            persistence.getEntityManager().getDelegate().flush();

            List<CommonMatchingRule> rules = matchingRuleDao.getMatchingRules();
            assertEquals(3, rules.size());

            //userSynchronizationService.synchronizeUser("joes", true);
            LdapUser ldapUser = new LdapUser(new BasicAttributes());
            ldapUser.setLogin("joes");
            LdapMatchingRuleContext ldapMatchingRuleContext = new LdapMatchingRuleContext(ldapUser, joes);
            matchingRuleApplier.applyMatchingRules(matchingRuleDao.getMatchingRules(), ldapMatchingRuleContext, joes);

            assertEquals(1, ldapMatchingRuleContext.getAppliedRules().size());
            assertEquals(true, ldapMatchingRuleContext.getAppliedRules().stream().allMatch(mr -> MatchingRuleType.DEFAULT == mr.getRuleType()));

            //cubaUserDao.saveCubaUser(joes, joes, ldapMatchingRuleContext);

            //MatchingRuleOrder defaultRuleOrder = persistence.getEntityManager().getDelegate().find(MatchingRuleOrder.class, UUID.fromString("ff2ebe74-3836-465b-9185-60141a6a0548"));

            //persistence.getEntityManager().getDelegate().detach(defaultRuleOrder);

            //User updated = cubaUserDao.getCubaUserByLogin("joes");
            //int t = 1;


        }
    }

    @Test
    public void testTerminalAttribute() {
        try (Transaction tx = persistence.createTransaction()) {
            prepareTerminalAttributeTest(true, "joes");

            User joes = cubaUserDao.getCubaUserByLogin("joes");

            LdapUser ldapUser = new LdapUser(new BasicAttributes());
            ldapUser.setLogin("joes");

            LdapMatchingRuleContext ldapMatchingRuleContext = new LdapMatchingRuleContext(ldapUser, joes);
            matchingRuleApplier.applyMatchingRules(matchingRuleDao.getMatchingRules(), ldapMatchingRuleContext, joes);

            assertEquals(1, ldapMatchingRuleContext.getAppliedRules().size());
            assertEquals(true, ldapMatchingRuleContext.getAppliedRules().stream().allMatch(mr -> MatchingRuleType.SCRIPTING == mr.getRuleType()));
            assertEquals("Test group", joes.getGroup().getName());
            assertEquals(1, joes.getUserRoles().size());
            assertEquals("Scripting role 1", joes.getUserRoles().get(0).getRole().getName());
            assertEquals(true, ldapMatchingRuleContext.isTerminalRuleApply());

            //cubaUserDao.saveCubaUser(joes, joes, ldapMatchingRuleContext);

            prepareTerminalAttributeTest(false, "bena");

            User bena = cubaUserDao.getCubaUserByLogin("bena");

            ldapUser = new LdapUser(new BasicAttributes());
            ldapUser.setLogin("bena");

            ldapMatchingRuleContext = new LdapMatchingRuleContext(ldapUser, bena);
            matchingRuleApplier.applyMatchingRules(matchingRuleDao.getMatchingRules(), ldapMatchingRuleContext, bena);

            assertEquals(2, ldapMatchingRuleContext.getAppliedRules().size());
            assertEquals(true, ldapMatchingRuleContext.getAppliedRules().stream().allMatch(mr -> MatchingRuleType.SCRIPTING == mr.getRuleType()));
            assertEquals("Test group", bena.getGroup().getName());
            assertEquals(2, bena.getUserRoles().size());
            assertEquals(true, bena.getUserRoles().stream().anyMatch(ur -> ur.getRole().getName().equals("Scripting role 1")));
            assertEquals(true, bena.getUserRoles().stream().anyMatch(ur -> ur.getRole().getName().equals("Scripting role 2")));
            assertEquals(false, ldapMatchingRuleContext.isTerminalRuleApply());

            //cubaUserDao.saveCubaUser(bena, bena, ldapMatchingRuleContext);

        }
    }

    @Test
    public void testOverrideAttribute() {
        try (Transaction tx = persistence.createTransaction()) {
            prepareOverrideAttributeTest(true, "joes");

            User joes = cubaUserDao.getCubaUserByLogin("joes");

            LdapUser ldapUser = new LdapUser(new BasicAttributes());
            ldapUser.setLogin("joes");

            LdapMatchingRuleContext ldapMatchingRuleContext = new LdapMatchingRuleContext(ldapUser, joes);
            matchingRuleApplier.applyMatchingRules(matchingRuleDao.getMatchingRules(), ldapMatchingRuleContext, joes);

            assertEquals(2, ldapMatchingRuleContext.getAppliedRules().size());
            assertEquals(true, ldapMatchingRuleContext.getAppliedRules().stream().allMatch(mr -> MatchingRuleType.SCRIPTING == mr.getRuleType()));
            assertEquals("Test group 2", joes.getGroup().getName());
            assertEquals(1, joes.getUserRoles().size());
            assertEquals("Scripting role 2", joes.getUserRoles().get(0).getRole().getName());

            //cubaUserDao.saveCubaUser(joes, joes, ldapMatchingRuleContext);

            prepareOverrideAttributeTest(false, "bena");

            User bena = cubaUserDao.getCubaUserByLogin("bena");

            ldapUser = new LdapUser(new BasicAttributes());
            ldapUser.setLogin("bena");

            ldapMatchingRuleContext = new LdapMatchingRuleContext(ldapUser, bena);
            matchingRuleApplier.applyMatchingRules(matchingRuleDao.getMatchingRules(), ldapMatchingRuleContext, bena);

            assertEquals(2, ldapMatchingRuleContext.getAppliedRules().size());
            assertEquals(true, ldapMatchingRuleContext.getAppliedRules().stream().allMatch(mr -> MatchingRuleType.SCRIPTING == mr.getRuleType()));
            assertEquals("Test group", bena.getGroup().getName());
            assertEquals(2, bena.getUserRoles().size());
            assertEquals(true, bena.getUserRoles().stream().anyMatch(ur -> ur.getRole().getName().equals("Scripting role 1")));
            assertEquals(true, bena.getUserRoles().stream().anyMatch(ur -> ur.getRole().getName().equals("Scripting role 2")));

            //cubaUserDao.saveCubaUser(bena, bena, ldapMatchingRuleContext);

        }
    }

    private void prepareSimpleTest() {
        Group testGroup = metadata.create(Group.class);
        testGroup.setName("Test group");
        daoHelper.persistOrMerge(testGroup);

        //Custom
        MatchingRuleOrder customOrder = metadata.create(MatchingRuleOrder.class);
        customOrder.setOrder(2);
        customOrder.setCustomMatchingRuleId("com.haulmont.addon.ldap.core.custom.TestCustomLdapRule");

        MatchingRuleStatus customStatus = metadata.create(MatchingRuleStatus.class);
        customStatus.setCustomMatchingRuleId("com.haulmont.addon.ldap.core.custom.TestCustomLdapRule");
        customStatus.setIsActive(true);

        daoHelper.persistOrMerge(customOrder);
        daoHelper.persistOrMerge(customStatus);

        //Simple
        Role simpleRole = metadata.create(Role.class);
        simpleRole.setName("Simple role");

        MatchingRuleOrder simpleOrder = metadata.create(MatchingRuleOrder.class);
        simpleOrder.setOrder(1);

        MatchingRuleStatus simpleStatus = metadata.create(MatchingRuleStatus.class);
        simpleStatus.setIsActive(true);

        SimpleRuleCondition simpleRuleCondition = metadata.create(SimpleRuleCondition.class);
        simpleRuleCondition.setAttribute("login");
        simpleRuleCondition.setAttributeValue("barts");

        SimpleMatchingRule simpleMatchingRule = metadata.create(SimpleMatchingRule.class);
        simpleMatchingRule.setStatus(simpleStatus);
        simpleMatchingRule.setOrder(simpleOrder);
        simpleRuleCondition.setSimpleMatchingRule(simpleMatchingRule);
        simpleMatchingRule.getConditions().add(simpleRuleCondition);
        simpleMatchingRule.setAccessGroup(testGroup);
        simpleMatchingRule.getRoles().add(simpleRole);

        daoHelper.persistOrMerge(simpleRole);
        daoHelper.persistOrMerge(simpleMatchingRule);

        //Scripting
        Role scriptingRole = metadata.create(Role.class);
        scriptingRole.setName("Scripting role");

        MatchingRuleOrder scriptingOrder = metadata.create(MatchingRuleOrder.class);
        scriptingOrder.setOrder(3);

        MatchingRuleStatus scriptingStatus = metadata.create(MatchingRuleStatus.class);
        scriptingStatus.setIsActive(false);

        ScriptingMatchingRule scriptingMatchingRule = metadata.create(ScriptingMatchingRule.class);
        scriptingMatchingRule.setAccessGroup(testGroup);
        scriptingMatchingRule.setStatus(scriptingStatus);
        scriptingMatchingRule.setOrder(scriptingOrder);
        scriptingMatchingRule.setScriptingCondition("{ldapContext}.ldapUser.login=='admin'");
        scriptingMatchingRule.getRoles().add(scriptingRole);
        daoHelper.persistOrMerge(scriptingRole);
        daoHelper.persistOrMerge(scriptingMatchingRule);

        persistence.getEntityManager().flush();

        persistence.getEntityManager().getDelegate().clear();

    }

    private void prepareTerminalAttributeTest(boolean terminal, String login) {

        persistence.getEntityManager().getDelegate().clear();

        Group testGroup = metadata.create(Group.class);
        testGroup.setName("Test group");
        daoHelper.persistOrMerge(testGroup);

        //Custom
        MatchingRuleOrder customOrder = metadata.create(MatchingRuleOrder.class);
        customOrder.setOrder(1);
        customOrder.setCustomMatchingRuleId("com.haulmont.addon.ldap.core.custom.TestCustomLdapRule");

        MatchingRuleStatus customStatus = metadata.create(MatchingRuleStatus.class);
        customStatus.setCustomMatchingRuleId("com.haulmont.addon.ldap.core.custom.TestCustomLdapRule");
        customStatus.setIsActive(true);

        daoHelper.persistOrMerge(customOrder);
        daoHelper.persistOrMerge(customStatus);

        //Scripting 1
        Role scriptingRole1 = metadata.create(Role.class);
        scriptingRole1.setName("Scripting role 1");

        MatchingRuleOrder scriptingOrder1 = metadata.create(MatchingRuleOrder.class);
        scriptingOrder1.setOrder(2);

        MatchingRuleStatus scriptingStatus1 = metadata.create(MatchingRuleStatus.class);
        scriptingStatus1.setIsActive(true);

        ScriptingMatchingRule scriptingMatchingRule1 = metadata.create(ScriptingMatchingRule.class);
        scriptingMatchingRule1.setAccessGroup(testGroup);
        scriptingMatchingRule1.setStatus(scriptingStatus1);
        scriptingMatchingRule1.setOrder(scriptingOrder1);
        scriptingMatchingRule1.setScriptingCondition("{ldapContext}.ldapUser.login=='" + login + "'");
        scriptingMatchingRule1.getRoles().add(scriptingRole1);
        scriptingMatchingRule1.setIsTerminalRule(terminal);
        daoHelper.persistOrMerge(scriptingRole1);
        daoHelper.persistOrMerge(scriptingMatchingRule1);

        //Scripting 2
        Role scriptingRole2 = metadata.create(Role.class);
        scriptingRole2.setName("Scripting role 2");

        MatchingRuleOrder scriptingOrder2 = metadata.create(MatchingRuleOrder.class);
        scriptingOrder2.setOrder(3);

        MatchingRuleStatus scriptingStatus2 = metadata.create(MatchingRuleStatus.class);
        scriptingStatus2.setIsActive(true);

        ScriptingMatchingRule scriptingMatchingRule2 = metadata.create(ScriptingMatchingRule.class);
        scriptingMatchingRule2.setAccessGroup(testGroup);
        scriptingMatchingRule2.setStatus(scriptingStatus2);
        scriptingMatchingRule2.setOrder(scriptingOrder2);
        scriptingMatchingRule2.setScriptingCondition("{ldapContext}.ldapUser.login=='" + login + "'");
        scriptingMatchingRule2.getRoles().add(scriptingRole2);
        daoHelper.persistOrMerge(scriptingRole2);
        daoHelper.persistOrMerge(scriptingMatchingRule2);


        //MatchingRuleOrder defaultRuleOrder = persistence.getEntityManager().getDelegate().find(MatchingRuleOrder.class, UUID.fromString("ff2ebe74-3836-465b-9185-60141a6a0548"));
        persistence.getEntityManager().flush();

        persistence.getEntityManager().getDelegate().clear();
    }

    private void prepareOverrideAttributeTest(boolean override, String login) {

        persistence.getEntityManager().getDelegate().clear();

        Group testGroup1 = metadata.create(Group.class);
        testGroup1.setName("Test group 1");
        daoHelper.persistOrMerge(testGroup1);

        Group testGroup2 = metadata.create(Group.class);
        testGroup2.setName("Test group 2");
        daoHelper.persistOrMerge(testGroup2);

        //Custom
        MatchingRuleOrder customOrder = metadata.create(MatchingRuleOrder.class);
        customOrder.setOrder(1);
        customOrder.setCustomMatchingRuleId("com.haulmont.addon.ldap.core.custom.TestCustomLdapRule");

        MatchingRuleStatus customStatus = metadata.create(MatchingRuleStatus.class);
        customStatus.setCustomMatchingRuleId("com.haulmont.addon.ldap.core.custom.TestCustomLdapRule");
        customStatus.setIsActive(true);

        daoHelper.persistOrMerge(customOrder);
        daoHelper.persistOrMerge(customStatus);

        //Scripting 1
        Role scriptingRole1 = metadata.create(Role.class);
        scriptingRole1.setName("Scripting role 1");

        MatchingRuleOrder scriptingOrder1 = metadata.create(MatchingRuleOrder.class);
        scriptingOrder1.setOrder(2);

        MatchingRuleStatus scriptingStatus1 = metadata.create(MatchingRuleStatus.class);
        scriptingStatus1.setIsActive(true);

        ScriptingMatchingRule scriptingMatchingRule1 = metadata.create(ScriptingMatchingRule.class);
        scriptingMatchingRule1.setAccessGroup(testGroup1);
        scriptingMatchingRule1.setStatus(scriptingStatus1);
        scriptingMatchingRule1.setOrder(scriptingOrder1);
        scriptingMatchingRule1.setScriptingCondition("{ldapContext}.ldapUser.login=='" + login + "'");
        scriptingMatchingRule1.getRoles().add(scriptingRole1);
        scriptingMatchingRule1.setIsOverrideExistingAccessGroup(override);
        scriptingMatchingRule1.setIsOverrideExistingRoles(override);
        daoHelper.persistOrMerge(scriptingRole1);
        daoHelper.persistOrMerge(scriptingMatchingRule1);

        //Scripting 2
        Role scriptingRole2 = metadata.create(Role.class);
        scriptingRole2.setName("Scripting role 2");

        MatchingRuleOrder scriptingOrder2 = metadata.create(MatchingRuleOrder.class);
        scriptingOrder2.setOrder(3);

        MatchingRuleStatus scriptingStatus2 = metadata.create(MatchingRuleStatus.class);
        scriptingStatus2.setIsActive(true);

        ScriptingMatchingRule scriptingMatchingRule2 = metadata.create(ScriptingMatchingRule.class);
        scriptingMatchingRule2.setAccessGroup(testGroup2);
        scriptingMatchingRule2.setStatus(scriptingStatus2);
        scriptingMatchingRule2.setOrder(scriptingOrder2);
        scriptingMatchingRule2.setScriptingCondition("{ldapContext}.ldapUser.login=='" + login + "'");
        scriptingMatchingRule2.getRoles().add(scriptingRole2);
        scriptingMatchingRule2.setIsOverrideExistingAccessGroup(override);
        scriptingMatchingRule2.setIsOverrideExistingRoles(override);
        daoHelper.persistOrMerge(scriptingRole2);
        daoHelper.persistOrMerge(scriptingMatchingRule2);


        //MatchingRuleOrder defaultRuleOrder = persistence.getEntityManager().getDelegate().find(MatchingRuleOrder.class, UUID.fromString("ff2ebe74-3836-465b-9185-60141a6a0548"));
        persistence.getEntityManager().flush();

        persistence.getEntityManager().getDelegate().clear();
    }
}
