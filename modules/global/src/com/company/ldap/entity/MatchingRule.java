package com.company.ldap.entity;

import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.Role;

import java.util.List;

public interface MatchingRule {

    MatchingRuleType getRuleType();

    Group getAccessGroup();

    List<Role> getRoles();

    Boolean isTerminalRule();

    Boolean isOverrideExistingRoles();

    Boolean isOverrideExistingAccessGroup();

    Boolean isDisabled();
}
