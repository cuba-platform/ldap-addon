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

public class ApplyMatchingRuleContext {

    private final LdapUser ldapUser;
    private final Map<String, Object> ldapUserAttributesMap;
    private final Set<CommonMatchingRule> appliedRules = new LinkedHashSet<>();
    private final Set<Role> currentRoles = new LinkedHashSet<>();
    private Group currentGroup;
    private final User cubaUser;
    private boolean isAnyRuleApply = false;
    private boolean isTerminalRuleApply = false;

    public ApplyMatchingRuleContext(LdapUser ldapUser, Attributes ldapUserAttributes, User cubaUser) {
        this.ldapUser = ldapUser;
        ldapUserAttributesMap = Collections.unmodifiableMap(setLdapAttributesMap(ldapUserAttributes));
        this.cubaUser = cubaUser;
    }

    public ApplyMatchingRuleContext(LdapUser ldapUser, Map<String, Object> ldapUserAttributesMap, User cubaUser) {
        this.ldapUser = ldapUser;
        this.ldapUserAttributesMap = Collections.unmodifiableMap(ldapUserAttributesMap);
        this.cubaUser = cubaUser;
    }

    public LdapUser getLdapUser() {
        return ldapUser;
    }

    public Map<String, Object> getLdapUserAttributes() {
        return ldapUserAttributesMap;
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

    private Map<String, Object> setLdapAttributesMap(Attributes ldapUserAttributes) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            NamingEnumeration<String> attrs = ldapUserAttributes.getIDs();
            while (attrs.hasMore()) {
                String attrName = attrs.next();
                NamingEnumeration values = ldapUserAttributes.get(attrName).getAll();
                List<Object> attrValues = new ArrayList<>();
                while (values.hasMore()) {
                    Object attr = values.next();
                    if (attr != null) {
                        attrValues.add(attr);
                    }
                }
                if (attrValues.size() == 1) {
                    resultMap.put(attrName, attrValues.get(0));
                } else if (attrValues.size() == 0) {
                    resultMap.put(attrName, null);
                } else {
                    resultMap.put(attrName, attrValues);
                }

            }
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
        return resultMap;
    }
}
