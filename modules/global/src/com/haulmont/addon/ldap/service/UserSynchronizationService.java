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
     * Synchronizes the state of a CUBA user in accordance with LDAP using persisted matching rules.
     *
     * @param login                     user login
     * @param saveSynchronizationResult persists the user state after synchronization
     */
    UserSynchronizationResultDto synchronizeUser(String login, boolean saveSynchronizationResult);

    /**
     * Synchronizes the state of a CUBA user in accordance with LDAP using provided matching rules.
     * After synchronization, the user state is not persisted.
     *
     * @param login        user login
     * @param rulesToApply matching rules provided on LDAP Matching Rule Screen.
     */
    TestUserSynchronizationDto testUserSynchronization(String login, List<AbstractCommonMatchingRule> rulesToApply);

    /**
     * Returns expired user sessions<br>
     * A session becomes expired if an access group or roles assigned to the user are changed since
     * the last LDAP synchronization or a user is deactivated via LDAP.
     */
    Set<ExpiredSession> getExpiringSession();

}
