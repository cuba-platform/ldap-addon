package com.haulmont.addon.ldap.service;

import com.haulmont.cuba.security.global.LoginException;

import java.util.Locale;

public interface AuthUserService {

    String NAME = "ldap_AuthUserService";

    /**
     * Tries to authenticate a user in LDAP using {@link org.springframework.ldap.core.LdapTemplate#authenticate(javax.naming.Name, String, String)}
     *
     * @throws LoginException if a user with provided credentials does not exist in LDAP.
     */
    void ldapAuth(String login, String password, Locale messagesLocale) throws LoginException;
}
