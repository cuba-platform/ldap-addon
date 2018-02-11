package com.haulmont.addon.ldap.service;

import com.haulmont.addon.ldap.dto.TestUserSynchronizationDto;

public interface UserSynchronizationService {
    String NAME = "ldap_UserSynchronizationService";

    void synchronizeUser(String login);

    TestUserSynchronizationDto testUserSynchronization(String login);

}
