package com.haulmont.addon.ldap.dto;

import com.haulmont.addon.ldap.entity.AbstractMatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRule;
import com.haulmont.cuba.security.entity.Role;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class TestUserSynchronizationDto implements Serializable{

    private final Set<AbstractMatchingRule> appliedMatchingRules = new HashSet<>();
    private final Set<Role> appliedCubaRoles = new HashSet<>();

    public Set<AbstractMatchingRule> getAppliedMatchingRules() {
        return appliedMatchingRules;
    }

    public Set<Role> getAppliedCubaRoles() {
        return appliedCubaRoles;
    }

}
