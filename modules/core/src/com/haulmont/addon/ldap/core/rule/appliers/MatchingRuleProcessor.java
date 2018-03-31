package com.haulmont.addon.ldap.core.rule.appliers;

import com.haulmont.addon.ldap.core.rule.LdapMatchingRuleContext;
import com.haulmont.addon.ldap.entity.CommonMatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRuleType;

public abstract class MatchingRuleProcessor {

    private MatchingRuleType matchingRuleType;

    public MatchingRuleProcessor(MatchingRuleType matchingRuleType) {
        this.matchingRuleType = matchingRuleType;
    }

    abstract boolean applyMatchingRule(CommonMatchingRule matchingRule, LdapMatchingRuleContext ldapMatchingRuleContext);

    public MatchingRuleType getMatchingRuleType() {
        return matchingRuleType;
    }

}
