package com.company.ldap.core.rule.appliers;

import com.company.ldap.core.rule.ApplyMatchingRuleContext;
import com.company.ldap.entity.MatchingRule;
import com.company.ldap.entity.MatchingRuleType;
import com.haulmont.cuba.core.global.Metadata;

public class FixedMatchingRuleChain extends MatchingRuleChain {

    public FixedMatchingRuleChain(MatchingRuleChain next, MatchingRuleType matchingRuleType, Metadata metadata) {
        super(next, matchingRuleType, metadata);
    }

    @Override
    public boolean checkRule(MatchingRule matchingRule, ApplyMatchingRuleContext applyMatchingRuleContext) {
        return false;
    }
}
