package com.haulmont.addon.ldap.entity;

import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.Role;

import java.util.List;

public interface MatchingRule {

    MatchingRuleType getRuleType();

    Group getAccessGroup();

    List<Role> getRoles();

    Boolean getIsTerminalRule();

    Boolean getIsOverrideExistingRoles();

    Boolean getIsOverrideExistingAccessGroup();

    Boolean getIsDisabled();

    String getDescription();

    MatchingRuleOrder getOrder();
}
