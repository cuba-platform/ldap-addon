package com.haulmont.addon.ldap.core.service;

import com.haulmont.addon.ldap.core.dao.LdapUserDao;
import com.haulmont.addon.ldap.service.AuthUserService;
import com.haulmont.cuba.security.global.LoginException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Locale;

@Service(AuthUserService.NAME)
public class AuthUserServiceBean implements AuthUserService {

    @Inject
    @Qualifier(LdapUserDao.NAME)
    private LdapUserDao ldapUserDao;

    @Override
    public void ldapAuth(String login, String password, Locale messagesLocale) throws LoginException{
        ldapUserDao.authenticateLdapUser(login, password, messagesLocale);

    }
}
