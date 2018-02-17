package com.haulmont.addon.ldap.core.rule;

import com.haulmont.addon.ldap.core.dto.LdapUser;
import com.haulmont.addon.ldap.entity.MatchingRule;

import javax.naming.directory.Attributes;
import java.util.HashSet;
import java.util.Set;

public class ApplyMatchingRuleContext {

    private final LdapUser ldapUser;
    private final Attributes ldapUserAttributes;
    private boolean isAnyRuleApply = false;
    private final Set<MatchingRule> appliedRules = new HashSet<>();
    //TODO: добавить в контекст какие группы роли применились давать юзера

    public ApplyMatchingRuleContext(LdapUser ldapUser, Attributes ldapUserAttributes) {
        this.ldapUser = ldapUser;
        this.ldapUserAttributes = ldapUserAttributes;
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

    public Set<MatchingRule> getAppliedRules() {
        return appliedRules;
    }
}
