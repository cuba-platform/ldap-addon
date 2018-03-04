package com.haulmont.addon.ldap.core.rule;

import com.haulmont.addon.ldap.core.dto.LdapUser;
import com.haulmont.addon.ldap.entity.CommonMatchingRule;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.Role;
import com.haulmont.cuba.security.entity.User;

import javax.naming.directory.Attributes;
import java.util.LinkedHashSet;
import java.util.Set;

public class ApplyMatchingRuleContext {

    private final LdapUser ldapUser;
    private final Attributes ldapUserAttributes;
    private final Set<CommonMatchingRule> appliedRules = new LinkedHashSet<>();
    private final Set<Role> appliedRoles = new LinkedHashSet<>();
    private final Set<Group> appliedGroups = new LinkedHashSet<>();
    private final Set<Role> currentRoles = new LinkedHashSet<>();
    private Group currentGroup;
    private final User cubaUser;
    private boolean isAnyRuleApply = false;
    private boolean isTerminalRuleApply = false;

    public ApplyMatchingRuleContext(LdapUser ldapUser, Attributes ldapUserAttributes, User cubaUser) {
        this.ldapUser = ldapUser;
        this.ldapUserAttributes = ldapUserAttributes;
        this.cubaUser= cubaUser;
    }

    public LdapUser getLdapUser() {
        return ldapUser;
    }

    public Attributes getLdapUserAttributes() {
        return ldapUserAttributes;
    }

    public boolean isAnyRuleApply() {
        return isAnyRuleApply;
    }

    public void setAnyRuleApply(boolean anyRuleApply) {
        isAnyRuleApply = anyRuleApply;
    }

    public Set<CommonMatchingRule> getAppliedRules() {
        return appliedRules;
    }

    public Set<Role> getAppliedRoles() {
        return appliedRoles;
    }

    public Set<Group> getAppliedGroups() {
        return appliedGroups;
    }

    public User getCubaUser() {
        return cubaUser;
    }

    public boolean isTerminalRuleApply() {
        return isTerminalRuleApply;
    }

    public void setTerminalRuleApply(boolean terminalRuleApply) {
        this.isTerminalRuleApply = terminalRuleApply;
    }

    public Set<Role> getCurrentRoles() {
        return currentRoles;
    }

    public Group getCurrentGroup() {
        return currentGroup;
    }

    public void setCurrentGroup(Group currentGroup) {
        this.currentGroup = currentGroup;
    }
}
