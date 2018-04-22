package com.haulmont.addon.ldap.service;

import com.haulmont.addon.ldap.dto.ExpiredSession;
import com.haulmont.addon.ldap.dto.TestUserSynchronizationDto;
import com.haulmont.addon.ldap.entity.AbstractCommonMatchingRule;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface UserSynchronizationService {
    String NAME = "ldap_UserSynchronizationService";

    boolean synchronizeUser(String login, boolean saveSynchronizationResult);

    TestUserSynchronizationDto testUserSynchronization(String login, List<AbstractCommonMatchingRule> rulesToApply);

    Set<ExpiredSession> getExpiringSession();

}
