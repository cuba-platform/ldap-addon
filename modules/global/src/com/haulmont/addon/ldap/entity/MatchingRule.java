package com.haulmont.addon.ldap.entity;

import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.Role;

import java.util.List;

/**
 * Methods for matching rules stored in the DB.
 */
public interface MatchingRule extends CommonMatchingRule {

    Group getAccessGroup();

    List<Role> getRoles();

    Boolean getIsTerminalRule();

    Boolean getIsOverrideExistingRoles();

    Boolean getIsOverrideExistingAccessGroup();
}
