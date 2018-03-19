package com.haulmont.addon.ldap.core.service;

import com.haulmont.addon.ldap.core.dao.CubaUserDao;
import com.haulmont.addon.ldap.core.dao.LdapUserAttributeDao;
import com.haulmont.addon.ldap.core.dao.LdapUserDao;
import com.haulmont.addon.ldap.core.dto.LdapUserWrapper;
import com.haulmont.addon.ldap.core.rule.ApplyMatchingRuleContext;
import com.haulmont.addon.ldap.core.spring.AnonymousLdapContextSource;
import com.haulmont.addon.ldap.core.utils.LdapHelper;
import com.haulmont.addon.ldap.dto.GroovyScriptTestResultDto;
import com.haulmont.addon.ldap.service.LdapService;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Scripting;
import com.haulmont.cuba.security.entity.User;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.codehaus.groovy.control.CompilationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private LdapUserAttributeDao ldapUserAttributeDao;

    @Inject
    @Qualifier(LdapUserDao.NAME)
    private LdapUserDao ldapUserDao;

    @Inject
    @Qualifier(CubaUserDao.NAME)
    private CubaUserDao cubaUserDao;

    @Inject
    private Scripting scripting;

    @Inject
    private Messages messages;

    private Map<String, Object> getAdditionalEnvProperties(String url) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotEmpty(url) && url.toUpperCase().startsWith("LDAPS")) {
            map.put("java.naming.ldap.factory.socket", "com.haulmont.addon.ldap.core.spring.ssl.CertCheckIgnoreSSLSocketFactory");
        }
        return map;
    }


    private LdapContextSource createAuthenticatedContext(String url, String base) {
        LdapContextSource ldapContextSource = new LdapContextSource();
        ldapContextSource.setUrl(url);
        ldapContextSource.setBase(base);
        ldapContextSource.setBaseEnvironmentProperties(getAdditionalEnvProperties(url));
        ldapContextSource.afterPropertiesSet();
        return ldapContextSource;
    }

    private LdapContextSource createAnonymousContext(String url, String base) {
        LdapContextSource ldapContextSource = new AnonymousLdapContextSource();
        ldapContextSource.setUrl(url);
        ldapContextSource.setBase(base);
        ldapContextSource.setAnonymousReadOnly(true);
        ldapContextSource.setBaseEnvironmentProperties(getAdditionalEnvProperties(url));
        ldapContextSource.afterPropertiesSet();
        return ldapContextSource;
    }

    @Override
    public String testConnection(String url, String base, String userDn, String password) {
        LdapContextSource ldapContextSource = null;
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
    public void fillLdapUserAttributes(String schemaBase, String objectClasses, String metaObjectClassName, String objectClassName, String attributeClassName, String url, String user, String password) {
        LdapContextSource ldapContextSource = null;
        DirContext dirContext = null;
        List<String> schemaAttributes = new ArrayList<>();
        try {
            ldapContextSource = createAuthenticatedContext(url, schemaBase);
            dirContext = ldapContextSource.getContext(user, password);
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SUBTREE_SCOPE);
            searchControls.setTimeLimit(30_000);
            String filter = LdapHelper.createSchemaFilter(objectClasses, metaObjectClassName, objectClassName);
            NamingEnumeration objectClassesResult = dirContext.search(StringUtils.EMPTY, filter, searchControls);
            while (objectClassesResult.hasMore()) {
                SearchResult searchResult = (SearchResult) objectClassesResult.next();
                Attributes attributes = searchResult.getAttributes();
                schemaAttributes.addAll(LdapHelper.getSchemaAttributes(attributes, attributeClassName.split(";")));
            }
            ldapUserAttributeDao.refreshLdapUserAttributes(schemaAttributes);
        } catch (Exception e) {
            throw new RuntimeException("Can't load LDAP schema", e);
        } finally {
            LdapUtils.closeContext(dirContext);
        }
    }

    @Override
    public List<String> getLdapUserAttributesNames() {
        return ldapUserAttributeDao.getLdapUserAttributesNames();
    }

    @Override
    public GroovyScriptTestResultDto testGroovyScript(String groovyScript, String login) {
        LdapUserWrapper ldapUserWrapper = ldapUserDao.getLdapUserWrapper(login);
        if (ldapUserWrapper == null) {
            return new GroovyScriptTestResultDto(NO_USER, null);
        }
        User cubaUser = cubaUserDao.getCubaUserByLogin(login);
        ApplyMatchingRuleContext applyMatchingRuleContext = new ApplyMatchingRuleContext(ldapUserWrapper.getLdapUser(), ldapUserWrapper.getLdapUserAttributes(), cubaUser);

        Map<String, Object> context = new HashMap<>();
        context.put("__context__", applyMatchingRuleContext);
        Object scriptExecutionResult = null;
        try {
            scriptExecutionResult = scripting.evaluateGroovy(groovyScript.replace("{E}", "__context__"), context);
        } catch (CompilationFailedException e) {
            logger.error(messages.formatMessage(LdapServiceBean.class, "errorDuringGroovyScriptEvaluation", login), e);
            return new GroovyScriptTestResultDto(COMPILATION_ERROR, ExceptionUtils.getFullStackTrace(e));
        } catch (Exception e) {
            logger.error(messages.formatMessage(LdapServiceBean.class, "errorDuringGroovyScriptEvaluation", login), e);
            return new GroovyScriptTestResultDto(OTHER_ERROR, ExceptionUtils.getFullStackTrace(e));
        }

        if (scriptExecutionResult instanceof Boolean) {
            Boolean bool = (Boolean) scriptExecutionResult;
            return bool ? new GroovyScriptTestResultDto(TRUE, null) : new GroovyScriptTestResultDto(FALSE, null);
        } else {
            return new GroovyScriptTestResultDto(NON_BOOLEAN_RESULT, null);
        }
    }
}
