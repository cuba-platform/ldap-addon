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

package com.haulmont.addon.ldap.core.utils;

import com.haulmont.addon.ldap.dto.LdapUser;
import com.haulmont.addon.ldap.entity.LdapConfig;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;

import javax.naming.NamingException;

public class LdapUserMapper implements ContextMapper<LdapUser> {

    private final LdapConfig ldapConfig;

    public LdapUserMapper(LdapConfig ldapConfig) {
        this.ldapConfig = ldapConfig;
    }

    @Override
    public LdapUser mapFromContext(Object ctx) throws NamingException {
        DirContextAdapter context = (DirContextAdapter) ctx;
        return LdapHelper.mapLdapUser(context, ldapConfig);
    }
}
