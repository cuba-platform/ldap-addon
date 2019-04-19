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

import com.haulmont.addon.ldap.dto.GroovyScriptTestResultDto;
import com.haulmont.addon.ldap.entity.LdapConfig;

import java.util.List;

public interface LdapService {

    String NAME = "ldap_LdapConnectionTesterService";

    /**
     * Tests connection to LDAP server.
     */
    String testConnection();

    /**
     * Loads attributes of provided classes from the LDAP schema
     */
    void fillLdapUserAttributes(String schemaBase, String objectClasses, String objectClassName, String attributeClassName);

    /**
     * Returns a list of LDAP attributes loaded using {@link #fillLdapUserAttributes(String, String, String, String)}
     */
    List<String> getLdapUserAttributesNames();

    /**
     * Tests a groovy script
     */
    GroovyScriptTestResultDto testGroovyScript(String groovyScript, String login);

    /**
     * Returns a configuration of the LDAP addon
     */
    LdapConfig getLdapConfig();

}
