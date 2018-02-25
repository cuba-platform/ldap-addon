package com.haulmont.addon.ldap.dto;

import com.haulmont.addon.ldap.entity.AbstractMatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRule;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.Role;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class TestUserSynchronizationDto implements Serializable{

    private final Set<AbstractMatchingRule> appliedMatchingRules = new LinkedHashSet<>();
    private final Set<Role> appliedCubaRoles = new LinkedHashSet<>();
    private Group group;
    private boolean isUserExistsInLdap = false;

    public Set<AbstractMatchingRule> getAppliedMatchingRules() {
        return appliedMatchingRules;
    }

    public Set<Role> getAppliedCubaRoles() {
        return appliedCubaRoles;
    }

    public boolean isUserExistsInLdap() {
        return isUserExistsInLdap;
    }

    public void setUserExistsInLdap(boolean userExistsInLdap) {
        isUserExistsInLdap = userExistsInLdap;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
