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

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

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
            assertEquals(MatchingRuleType.CUSTOM, rules.get(0).getRuleType());
            assertEquals(MatchingRuleType.SIMPLE, rules.get(1).getRuleType());
            assertEquals(MatchingRuleType.SCRIPTING, rules.get(2).getRuleType());
            assertEquals(MatchingRuleType.DEFAULT, rules.get(3).getRuleType());
            SimpleMatchingRule simpleMatchingRule = (SimpleMatchingRule) rules.get(1);
            assertEquals("login", simpleMatchingRule.getConditions().get(0).getAttribute());


        }
    }

    @Test
    public void applyDefaultRule() {
        try (Transaction tx = persistence.createTransaction()) {

            Group testGroup = metadata.create(Group.class);
            testGroup.setName("Test group");
            daoHelper.persistOrMerge(testGroup);

            User joes = cubaUserDao.getCubaUserByLogin("joes");
            joes.setGroup(testGroup);
            daoHelper.persistOrMerge(joes);

            Role simpleRole = metadata.create(Role.class);
            simpleRole.setName("Simple role");

            MatchingRuleOrder simpleOrder = metadata.create(MatchingRuleOrder.class);
            simpleOrder.setOrder(1);

            MatchingRuleStatus simpleStatus = metadata.create(MatchingRuleStatus.class);
            simpleStatus.setIsActive(false);

            SimpleRuleCondition simpleRuleCondition = metadata.create(SimpleRuleCondition.class);
            simpleRuleCondition.setAttribute("login");
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

            persistence.getEntityManager().flush();

            //userSynchronizationService.synchronizeUser("joes", true);
            LdapMatchingRuleContext ldapMatchingRuleContext = new LdapMatchingRuleContext(null, joes);
            matchingRuleApplier.applyMatchingRules(matchingRuleDao.getMatchingRules(), ldapMatchingRuleContext, joes);
            cubaUserDao.saveCubaUser(joes, joes, ldapMatchingRuleContext);

            //MatchingRuleOrder defaultRuleOrder = persistence.getEntityManager().getDelegate().find(MatchingRuleOrder.class, UUID.fromString("ff2ebe74-3836-465b-9185-60141a6a0548"));

            //persistence.getEntityManager().getDelegate().detach(defaultRuleOrder);

            User updated = cubaUserDao.getCubaUserByLogin("joes");
            int t = 1;


        }
    }

    private void prepareSimpleTest() {
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

        //Simple
        Role simpleRole = metadata.create(Role.class);
        simpleRole.setName("Simple role");

        MatchingRuleOrder simpleOrder = metadata.create(MatchingRuleOrder.class);
        simpleOrder.setOrder(2);

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
}
