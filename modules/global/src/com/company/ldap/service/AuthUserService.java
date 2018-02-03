package com.company.ldap.service;

import com.haulmont.cuba.security.global.LoginException;

import java.util.Locale;

public interface AuthUserService {

    String NAME = "ldap_AuthUserService";

    void ldapAuth(String login, String password, Locale messagesLocale) throws LoginException;
}
