package com.company.ldap.core.rule.appliers;

import com.company.ldap.core.rule.ApplyMatchingRuleContext;
import com.company.ldap.entity.AbstractMatchingRule;
import com.company.ldap.entity.MatchingRule;
import com.company.ldap.entity.MatchingRuleType;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;

public class FixedMatchingRuleChain extends MatchingRuleChain {

    public FixedMatchingRuleChain(MatchingRuleChain next, Metadata metadata) {
        super(next, MatchingRuleType.FIXED, metadata);
    }

    @Override
    public boolean checkRule(MatchingRule matchingRule, ApplyMatchingRuleContext applyMatchingRuleContext) {
        return false;
    }
}
