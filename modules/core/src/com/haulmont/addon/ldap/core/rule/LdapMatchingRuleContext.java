package com.haulmont.addon.ldap.core.rule;

import com.haulmont.addon.ldap.core.dto.LdapUser;
import com.haulmont.addon.ldap.entity.CommonMatchingRule;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.Role;
import com.haulmont.cuba.security.entity.User;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.*;

/**
 * Stores information about user's LDAP synchronization process.
 */
public class LdapMatchingRuleContext {

    /**
     * LDAP representation of synchronized CUBA user.
     */
    private final LdapUser ldapUser;

    /**
     * Matching rules applied to the user.
     */
    private final Set<CommonMatchingRule> appliedRules = new LinkedHashSet<>();

    /**
     * Roles from applied matching rules
     */
    private final Set<Role> roles = new LinkedHashSet<>();

    /**
     * Access group from applied matching rules.
     */
    private Group group;

    /**
     * CUBA user which synchronized state with LDAP.
     */
    private final User cubaUser;

    /**
     * This field set if terminal matching rule was applied.<br>
     * Stops applying of matching rules for this context.
     */
    private boolean isTerminalRuleApply = false;

    public LdapMatchingRuleContext(LdapUser ldapUser, User cubaUser) {
        this.ldapUser = ldapUser;
        this.cubaUser = cubaUser;
    }

    public LdapUser getLdapUser() {
        return ldapUser;
    }

    public Set<CommonMatchingRule> getAppliedRules() {
        return appliedRules;
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

    public Set<Role> getRoles() {
        return roles;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

}
