package com.company.ldap.core.auth;


import com.company.ldap.encryption.PlainTextPasswordEncryptionModule;
import com.company.ldap.service.UserSynchronizationService;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.security.auth.AuthenticationDetails;
import com.haulmont.cuba.security.auth.Credentials;
import com.haulmont.cuba.security.auth.LoginPasswordCredentials;
import com.haulmont.cuba.security.auth.providers.LoginPasswordAuthenticationProvider;
import com.haulmont.cuba.security.global.LoginException;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.inject.Inject;

//TODO: подумать как можно лучше сделать
//1)переопределять LoginWindow->новый тип Credential->новый провайдер
//2)использовать CubaAuthProvider, это на мой взгляд проще, но какие могут быть минусы???
public class LdapExtensionLoginPasswordAuthenticationProvider extends LoginPasswordAuthenticationProvider {

    @Inject
    @Qualifier(UserSynchronizationService.NAME)
    private UserSynchronizationService userSynchronizationService;

    @Inject
    @Qualifier(PlainTextPasswordEncryptionModule.NAME)
    private PlainTextPasswordEncryptionModule plainTextPasswordEncryptionModule;


    @Inject
    public LdapExtensionLoginPasswordAuthenticationProvider(Persistence persistence, Messages messages) {
        super(persistence, messages);
    }

    @Override
    public AuthenticationDetails authenticate(Credentials credentials) throws LoginException {
        LoginPasswordCredentials loginPassword = (LoginPasswordCredentials) credentials;
        if (!"admin".equalsIgnoreCase(loginPassword.getLogin())) {
            userSynchronizationService.synchronizeUser(loginPassword.getLogin(), loginPassword.getPassword());
        }
        loginPassword.setPassword(plainTextPasswordEncryptionModule.getSuperPlainHash(loginPassword.getPassword()));
        return super.authenticate(credentials);
    }


}