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

package com.haulmont.addon.ldap.service;

import com.haulmont.cuba.security.global.LoginException;

import javax.annotation.Nullable;
import java.util.Locale;

public interface AuthUserService {

    String NAME = "ldap_AuthUserService";

    /**
     * Tries to authenticate a user in LDAP using {@link org.springframework.ldap.core.LdapTemplate#authenticate(javax.naming.Name, String, String)}
     *
     * @throws LoginException if a user with provided credentials does not exist in LDAP.
     */
    void ldapAuth(String login, String password, Locale messagesLocale, @Nullable String tenantId) throws LoginException;
}
