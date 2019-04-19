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

package com.haulmont.addon.ldap.core.dao;

import com.haulmont.addon.ldap.config.LdapPropertiesConfig;
import com.haulmont.addon.ldap.entity.LdapConfig;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.TypedQuery;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import static com.haulmont.addon.ldap.core.dao.LdapConfigDao.NAME;

@Component(NAME)
public class LdapConfigDao {

    public final static String NAME = "ldap_LdapConfigDao";

    @Inject
    private Persistence persistence;

    @Inject
    private LdapPropertiesConfig ldapContextConfig;

    @Transactional(readOnly = true)
    public LdapConfig getLdapConfig() {
        TypedQuery<LdapConfig> query = persistence.getEntityManager()
                .createQuery("select lc from ldap$LdapPropertiesConfig lc", LdapConfig.class);
        LdapConfig lc = query.getSingleResult();

        lc.setContextSourceUrl(ldapContextConfig.getContextSourceUrl());
        lc.setContextSourceBase(ldapContextConfig.getContextSourceBase());
        lc.setContextSourceUserName(ldapContextConfig.getContextSourceUserName());

        return lc;

    }
}
