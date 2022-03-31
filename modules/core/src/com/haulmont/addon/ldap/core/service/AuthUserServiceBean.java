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

package com.haulmont.addon.ldap.core.service;

import com.haulmont.addon.ldap.core.dao.LdapUserDao;
import com.haulmont.addon.ldap.service.AuthUserService;
import com.haulmont.cuba.security.global.LoginException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Locale;

@Service(AuthUserService.NAME)
public class AuthUserServiceBean implements AuthUserService {

    @Inject
    private LdapUserDao ldapUserDao;

    @Override
    public void ldapAuth(String login, String password, Locale messagesLocale, String tenantId) throws LoginException {
        ldapUserDao.authenticateLdapUser(login, password, messagesLocale, tenantId);
    }
}
