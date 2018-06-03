package com.haulmont.addon.ldap.core;

import com.haulmont.addon.ldap.core.LdapTestContainer;
import com.haulmont.addon.ldap.core.dao.DaoHelper;
import com.haulmont.addon.ldap.core.dao.LdapConfigDao;
import com.haulmont.addon.ldap.core.dao.LdapUserDao;
import com.haulmont.addon.ldap.core.dto.LdapUser;
import com.haulmont.addon.ldap.entity.LdapConfig;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.security.entity.User;
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

    @Before
    public void setUp() throws Exception {
        metadata = cont.metadata();
        persistence = cont.persistence();
        dataManager = AppBeans.get(DataManager.class);
        ldapUserDao = AppBeans.get(LdapUserDao.class);
        ldapConfigDao = AppBeans.get(LdapConfigDao.class);
        daoHelper = AppBeans.get(DaoHelper.class);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void intTest() {
        try (Transaction tx = persistence.createTransaction()) {
            LdapConfig ldapConfig = ldapConfigDao.getLdapConfig();
            assertNotNull(ldapConfig);
            ldapConfig.setLoginAttribute("uid");
            ldapConfig.setInactiveUserAttribute("roomNumber");
            daoHelper.persistOrMerge(ldapConfig);
            persistence.getEntityManager().flush();

            LdapUser ldapUser = ldapUserDao.getLdapUser("bena");
            int t = 1;
        }
    }
}