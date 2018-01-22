package com.company.ldap.core.rule.appliers;

import com.company.ldap.core.rule.ApplyMatchingRuleContext;
import com.company.ldap.core.rule.programmatic.ProgrammaticMatchingRule;
import com.company.ldap.entity.MatchingRule;
import com.company.ldap.entity.MatchingRuleType;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;

public class ProgrammaticMatchingRuleChain extends MatchingRuleChain {

    public ProgrammaticMatchingRuleChain(MatchingRuleChain next, Metadata metadata) {
        super(next, MatchingRuleType.PROGRAMMATIC, metadata);
    }

    @Override
    boolean checkRule(MatchingRule matchingRule, ApplyMatchingRuleContext applyMatchingRuleContext) {
        ProgrammaticMatchingRule programmaticMatchingRule = (ProgrammaticMatchingRule) matchingRule;
        return programmaticMatchingRule.checkProgrammaticMatchingRule(applyMatchingRuleContext);
    }
}
