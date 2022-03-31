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

import com.haulmont.addon.ldap.core.dao.CubaUserDao;
import com.haulmont.addon.ldap.core.dao.LdapConfigDao;
import com.haulmont.addon.ldap.core.dao.LdapUserDao;
import com.haulmont.addon.ldap.core.rule.LdapMatchingRuleContext;
import com.haulmont.addon.ldap.core.spring.AnonymousLdapContextSource;
import com.haulmont.addon.ldap.core.utils.LdapHelper;
import com.haulmont.addon.ldap.dto.GroovyScriptTestResultDto;
import com.haulmont.addon.ldap.dto.LdapUser;
import com.haulmont.addon.ldap.entity.LdapConfig;
import com.haulmont.addon.ldap.service.LdapService;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Scripting;
import com.haulmont.cuba.security.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.codehaus.groovy.control.CompilationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.haulmont.addon.ldap.dto.GroovyScriptTestResult.*;
import static javax.naming.directory.SearchControls.SUBTREE_SCOPE;

@Service(LdapService.NAME)
public class LdapServiceBean implements LdapService {

    private final static String DUMMY_FILTER = "ou=system";

    private final Logger logger = LoggerFactory.getLogger(LdapServiceBean.class);

    @Inject
    private LdapUserDao ldapUserDao;
    @Inject
    private CubaUserDao cubaUserDao;
    @Inject
    private LdapConfigDao ldapConfigDao;
    @Inject
    private Scripting scripting;
    @Inject
    private Messages messages;

    private LdapContextSource createAuthenticatedContext(String url, String base) {
        LdapContextSource ldapContextSource = new LdapContextSource();
        ldapContextSource.setUrl(url);
        ldapContextSource.setBase(base);
        ldapContextSource.afterPropertiesSet();
        return ldapContextSource;
    }

    private LdapContextSource createAnonymousContext(String url, String base) {
        LdapContextSource ldapContextSource = new AnonymousLdapContextSource();
        ldapContextSource.setUrl(url);
        ldapContextSource.setBase(base);
        ldapContextSource.setAnonymousReadOnly(true);
        ldapContextSource.afterPropertiesSet();
        return ldapContextSource;
    }

    @Override
    public String testConnection(String url, String base, String userDn, String password) {
        LdapContextSource ldapContextSource;
        DirContext dirContext = null;
        try {
            if (StringUtils.isEmpty(userDn) && StringUtils.isEmpty(password)) {
                ldapContextSource = createAnonymousContext(url, base);
            } else {
                ldapContextSource = createAuthenticatedContext(url, base);
            }
            dirContext = ldapContextSource.getContext(userDn, password);
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SUBTREE_SCOPE);
            searchControls.setCountLimit(1L);
            dirContext.search(StringUtils.EMPTY, DUMMY_FILTER, searchControls);//try to search dummy value in specified context base
            return "SUCCESS";
        } catch (Exception e) {
            return ExceptionUtils.getStackTrace(e);
        } finally {
            LdapUtils.closeContext(dirContext);
        }
    }

    @Override
    public List<String> getLdapUserAttributes(LdapConfig ldapConfig) {
        DirContext dirContext = null;
        List<String> schemaAttributes = new ArrayList<>();
        String url = ldapConfig.getContextSourceUrl();
        String schemaBase = ldapConfig.getSchemaBase();
        String userDn = ldapConfig.getContextSourceUserName();
        String password = ldapConfig.getContextSourcePassword();
        String objectClasses = ldapConfig.getLdapUserObjectClasses();
        String objectClassName = ldapConfig.getObjectClassPropertyName();
        String attributeClassName = ldapConfig.getAttributePropertyNames();
        try {
            LdapContextSource ldapContextSource = createAuthenticatedContext(url, schemaBase);
            dirContext = ldapContextSource.getContext(userDn, password);
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SUBTREE_SCOPE);
            searchControls.setTimeLimit(30_000);
            String filter = LdapHelper.createSchemaFilter(objectClasses, objectClassName);
            NamingEnumeration<SearchResult> objectClassesResult = dirContext.search(StringUtils.EMPTY, filter, searchControls);
            while (objectClassesResult.hasMore()) {
                SearchResult searchResult = objectClassesResult.next();
                Attributes attributes = searchResult.getAttributes();
                schemaAttributes.addAll(LdapHelper.getSchemaAttributes(attributes, attributeClassName.split(";")));
            }
            return schemaAttributes;
        } catch (Exception e) {
            throw new RuntimeException("Can't load LDAP schema", e);
        } finally {
            LdapUtils.closeContext(dirContext);
        }
    }

    @Override
    public GroovyScriptTestResultDto testGroovyScript(String groovyScript, String login, String tenantId) {
        LdapUser ldapUser = ldapUserDao.getLdapUser(login, tenantId);
        if (ldapUser == null) {
            return new GroovyScriptTestResultDto(NO_USER, null);
        }
        User cubaUser = cubaUserDao.getOrCreateCubaUser(login);
        LdapMatchingRuleContext ldapMatchingRuleContext =
                new LdapMatchingRuleContext(ldapUser, cubaUser);

        Map<String, Object> context = new HashMap<>();
        context.put("__context__", ldapMatchingRuleContext);
        Object scriptExecutionResult = null;
        try {
            scriptExecutionResult = scripting.evaluateGroovy(groovyScript.replace("{ldapContext}", "__context__"), context);
        } catch (CompilationFailedException e) {
            logger.error(messages.formatMessage(LdapServiceBean.class, "errorDuringGroovyScriptEvaluation", login), e);
            return new GroovyScriptTestResultDto(COMPILATION_ERROR, ExceptionUtils.getStackTrace(e));
        } catch (Exception e) {
            logger.error(messages.formatMessage(LdapServiceBean.class, "errorDuringGroovyScriptEvaluation", login), e);
            return new GroovyScriptTestResultDto(OTHER_ERROR, ExceptionUtils.getStackTrace(e));
        }

        if (scriptExecutionResult instanceof Boolean) {
            Boolean bool = (Boolean) scriptExecutionResult;
            return bool ? new GroovyScriptTestResultDto(TRUE, null) : new GroovyScriptTestResultDto(FALSE, null);
        } else {
            return new GroovyScriptTestResultDto(NON_BOOLEAN_RESULT, null);
        }
    }

}
