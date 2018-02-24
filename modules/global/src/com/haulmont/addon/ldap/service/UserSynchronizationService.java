package com.haulmont.addon.ldap.service;

import com.haulmont.addon.ldap.dto.TestUserSynchronizationDto;
import com.haulmont.addon.ldap.entity.AbstractMatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRule;

import java.util.List;

public interface UserSynchronizationService {
    String NAME = "ldap_UserSynchronizationService";

    void synchronizeUser(String login);

    TestUserSynchronizationDto testUserSynchronization(String login, List<AbstractMatchingRule> rulesToApply);

}
