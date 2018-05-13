package com.haulmont.addon.ldap.service;

import com.haulmont.cuba.security.global.LoginException;

import javax.naming.Name;
import java.util.Locale;

public interface AuthUserService {

    String NAME = "ldap_AuthUserService";

    /**
     * Try to authenticate user in LDAP using {@link org.springframework.ldap.core.LdapTemplate#authenticate(Name, String, String)}
     *
     * @throws LoginException if user with provided credentials not exists in LDAP.
     */
    void ldapAuth(String login, String password, Locale messagesLocale) throws LoginException;
}
