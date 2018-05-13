package com.haulmont.addon.ldap.entity;

import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.Role;

import java.util.List;

/**
 * Methods DB stored matching rules.
 */
public interface MatchingRule extends CommonMatchingRule {

    Group getAccessGroup();

    List<Role> getRoles();

    Boolean getIsTerminalRule();

    Boolean getIsOverrideExistingRoles();

    Boolean getIsOverrideExistingAccessGroup();
}
