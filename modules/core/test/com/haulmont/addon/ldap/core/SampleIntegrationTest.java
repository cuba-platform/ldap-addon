package com.haulmont.addon.ldap.core;

import com.haulmont.addon.ldap.core.LdapTestContainer;
import com.haulmont.addon.ldap.core.dao.CubaUserDao;
import com.haulmont.addon.ldap.core.dao.DaoHelper;
import com.haulmont.addon.ldap.core.dao.LdapConfigDao;
import com.haulmont.addon.ldap.core.dao.LdapUserDao;
import com.haulmont.addon.ldap.core.dto.LdapUser;
import com.haulmont.addon.ldap.dto.UserSynchronizationResultDto;
import com.haulmont.addon.ldap.entity.*;
import com.haulmont.addon.ldap.service.UserSynchronizationService;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.TypedQuery;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void intTest() {
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
            simpleRuleCondition2.setAttributeValue("P.Prutser@example.com");

            SimpleMatchingRule simpleMatchingRule = metadata.create(SimpleMatchingRule.class);
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

            UserSynchronizationResultDto userSynchronizationResultDto = userSynchronizationService.synchronizeUser("barts", true);


        }
    }
}