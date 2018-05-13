package com.haulmont.addon.ldap.service;

import com.haulmont.addon.ldap.dto.ExpiredSession;
import com.haulmont.addon.ldap.dto.TestUserSynchronizationDto;
import com.haulmont.addon.ldap.dto.UserSynchronizationResultDto;
import com.haulmont.addon.ldap.entity.AbstractCommonMatchingRule;

import java.util.List;
import java.util.Set;

public interface UserSynchronizationService {
    String NAME = "ldap_UserSynchronizationService";

    /**
     * Synchronize CUBA user state with LDAP using persisted matching rules.
     *
     * @param login                     user login
     * @param saveSynchronizationResult persist user state after synchronization
     */
    UserSynchronizationResultDto synchronizeUser(String login, boolean saveSynchronizationResult);

    /**
     * Synchronize CUBA user state with LDAP using provided matching rules. User state after synchronization don't persists.
     *
     * @param login        user login
     * @param rulesToApply matching rules provided from LDAP Matching Rule screen.
     */
    TestUserSynchronizationDto testUserSynchronization(String login, List<AbstractCommonMatchingRule> rulesToApply);

    /**
     * Returns expiring user sessions<br>
     * Session becomes expiring if user's group or roles changes since last LDAP synchronization or user was deactivated in LDAP.
     */
    Set<ExpiredSession> getExpiringSession();

}
