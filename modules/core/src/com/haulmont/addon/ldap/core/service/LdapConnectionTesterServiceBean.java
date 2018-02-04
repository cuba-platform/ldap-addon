package com.haulmont.addon.ldap.core.service;

import com.haulmont.addon.ldap.core.spring.AnonymousLdapContextSource;
import com.haulmont.addon.ldap.service.LdapConnectionTesterService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Service;

import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;

import java.util.HashMap;
import java.util.Map;

import static javax.naming.directory.SearchControls.SUBTREE_SCOPE;

@Service(LdapConnectionTesterService.NAME)
public class LdapConnectionTesterServiceBean implements LdapConnectionTesterService {

    private final String DUMMY_FILTER = "ou=system";

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
            return "FAILED \n" + ExceptionUtils.getStackTrace(e);
        } finally {
            LdapUtils.closeContext(dirContext);
        }
    }


}
