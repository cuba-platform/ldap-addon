package com.company.ldap.core.rule.programmatic;

import com.company.ldap.core.rule.ApplyMatchingRuleContext;
import com.company.ldap.entity.MatchingRule;
import com.company.ldap.entity.MatchingRuleType;

public interface ProgrammaticMatchingRule extends MatchingRule {

    boolean checkProgrammaticMatchingRule(ApplyMatchingRuleContext applyMatchingRuleContext);

    default MatchingRuleType getRuleType() {
        return MatchingRuleType.PROGRAMMATIC;
    }
}
