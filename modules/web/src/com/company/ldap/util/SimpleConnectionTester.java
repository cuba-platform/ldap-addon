package com.company.ldap.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Component;

import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;

import static javax.naming.directory.SearchControls.SUBTREE_SCOPE;

@Component("ldap_SimpleConnectionTester")
public class SimpleConnectionTester {

    private final String DUMMY_FILTER = "ou=system";


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
