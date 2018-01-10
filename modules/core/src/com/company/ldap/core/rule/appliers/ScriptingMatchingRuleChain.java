package com.company.ldap.core.rule.appliers;

import com.company.ldap.core.rule.ApplyMatchingRuleContext;
import com.company.ldap.entity.MatchingRule;
import com.company.ldap.entity.MatchingRuleType;
import com.haulmont.cuba.core.global.Metadata;

public class ScriptingMatchingRuleChain extends MatchingRuleChain {

    public ScriptingMatchingRuleChain(MatchingRuleChain next, MatchingRuleType matchingRuleType, Metadata metadata) {
        super(next, matchingRuleType, metadata);
    }

    @Override
    public boolean checkRule(MatchingRule matchingRule, ApplyMatchingRuleContext applyMatchingRuleContext) {
        return false;
    }
}
