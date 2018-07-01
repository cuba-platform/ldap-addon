package com.haulmont.addon.ldap.core.service;

import com.haulmont.addon.ldap.dto.ExpiredSession;

import java.util.Set;

public interface UserSynchronizationSchedulerService {

    String NAME = "ldap_UserSynchronizationSchedulerService";

    void checkExpiredSessions();

    void killExpiredSessions();

    Set<ExpiredSession> getExpiredSessions();

    void synchronizeUsersFromLdap();
}
