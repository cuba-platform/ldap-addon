package com.haulmont.addon.ldap.service;

import com.haulmont.addon.ldap.dto.TestUserSynchronizationDto;
import com.haulmont.addon.ldap.entity.AbstractCommonMatchingRule;

import java.util.List;

public interface UserSynchronizationService {
    String NAME = "ldap_UserSynchronizationService";

    void synchronizeUserAfterLdapLogin(String login);

    TestUserSynchronizationDto testUserSynchronization(String login, List<AbstractCommonMatchingRule> rulesToApply);

}
