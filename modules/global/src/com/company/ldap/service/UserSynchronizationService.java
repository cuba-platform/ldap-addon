package com.company.ldap.service;

import com.haulmont.cuba.security.global.LoginException;

public interface UserSynchronizationService {
    String NAME = "ldap_UserSynchronizationService";

    void synchronizeUser(String login, String password) throws LoginException;

}
