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

import com.haulmont.addon.ldap.core.dao.CubaUserDao;
import com.haulmont.addon.ldap.core.dao.DaoHelper;
import com.haulmont.addon.ldap.core.dao.GroupDao;
import com.haulmont.addon.ldap.core.dao.LdapConfigDao;
import com.haulmont.addon.ldap.entity.LdapConfig;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.Role;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserRole;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import static org.junit.Assert.*;

public class SimpleAddonTest {

    @ClassRule
    public static LdapTestContainer cont = LdapTestContainer.Common.INSTANCE;

    private Metadata metadata;
    private Persistence persistence;
    private CubaUserDao cubaUserDao;
    private GroupDao groupDao;
    private DaoHelper daoHelper;
    private LdapConfigDao ldapConfigDao;


    @Before
    public void setUp() throws Exception {
        cubaUserDao = AppBeans.get(CubaUserDao.class);
        groupDao = AppBeans.get(GroupDao.class);
        daoHelper = AppBeans.get(DaoHelper.class);
        ldapConfigDao = AppBeans.get(LdapConfigDao.class);
        metadata = AppBeans.get(Metadata.class);
        persistence = AppBeans.get(Persistence.class);

    }

    @Test
    public void simpleTest() {
        try (Transaction tx = persistence.createTransaction()) {
            //Create test group
            Group testGroup = metadata.create(Group.class);
            testGroup.setName("Test group");
            daoHelper.persistOrMerge(testGroup);
            persistence.getEntityManager().flush();

            //Change default access group in ldap config
            LdapConfig ldapConfig = ldapConfigDao.getLdapConfig();
            assertNotNull(ldapConfig);
            ldapConfig.setDefaultAccessGroupName("Test group");
            ldapConfig = daoHelper.persistOrMerge(ldapConfig);
            persistence.getEntityManager().flush();

            assertEquals("Test group", ldapConfig.getDefaultAccessGroupName());
            assertEquals("uid=admin,ou=system", ldapConfig.getContextSourceUserName());
            assertEquals("ldap://localhost:10389", ldapConfig.getContextSourceUrl());
            assertEquals("dc=springframework,dc=org", ldapConfig.getContextSourceBase());

            persistence.getEntityManager().getDelegate().detach(ldapConfig);
            persistence.getEntityManager().getDelegate().detach(testGroup);

            Group group = groupDao.getDefaultGroup();
            assertNotNull(group);
            assertEquals("Test group", group.getName());

            User testAdmin = metadata.create(User.class);
            testAdmin.setLogin("testAdmin");
            testAdmin.setGroup(group);

            Role testRole = metadata.create(Role.class);
            testRole.setName("Test role");
            UserRole testUserRole = metadata.create(UserRole.class);
            testUserRole.setRole(testRole);
            testUserRole.setUser(testAdmin);

            daoHelper.persistOrMerge(testAdmin);
            daoHelper.persistOrMerge(testRole);
            daoHelper.persistOrMerge(testUserRole);
            persistence.getEntityManager().flush();
            persistence.getEntityManager().getDelegate().detach(testAdmin);
            persistence.getEntityManager().getDelegate().detach(testRole);
            persistence.getEntityManager().getDelegate().detach(testUserRole);


            User newUser = cubaUserDao.getOrCreateCubaUser("test");
            assertNotNull(newUser);
            assertNotNull(newUser.getUserRoles());
            assertTrue(PersistenceHelper.isNew(newUser));
            assertEquals("test", newUser.getLogin());

            User dbUser = cubaUserDao.getOrCreateCubaUser("testAdmin");
            assertNotNull(dbUser);
            assertFalse(PersistenceHelper.isNew(dbUser));
            assertEquals("testAdmin", dbUser.getLogin());
            assertEquals("Test group", dbUser.getGroup().getName());
            assertEquals(2, dbUser.getUserRoles().size());
            assertEquals("Test role", dbUser.getUserRoles().stream()
                    .filter(ur -> ur.getRole() != null)
                    .findFirst().get().getRole().getName());

        }
    }

}
