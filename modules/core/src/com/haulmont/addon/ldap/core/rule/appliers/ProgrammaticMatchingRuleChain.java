package com.haulmont.addon.ldap.core.rule.appliers;

import com.haulmont.addon.ldap.core.rule.ApplyMatchingRuleContext;
import com.haulmont.addon.ldap.core.rule.programmatic.ProgrammaticMatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRuleType;
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
