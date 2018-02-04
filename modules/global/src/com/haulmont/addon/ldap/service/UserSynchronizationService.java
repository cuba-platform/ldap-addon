package com.haulmont.addon.ldap.service;

public interface UserSynchronizationService {
    String NAME = "ldap_UserSynchronizationService";

    void synchronizeUser(String login);

}
