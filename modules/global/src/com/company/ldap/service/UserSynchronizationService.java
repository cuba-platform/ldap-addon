package com.company.ldap.service;

public interface UserSynchronizationService {
    String NAME = "ldap_UserSynchronizationService";

    void synchronizeUser(String login);

}
