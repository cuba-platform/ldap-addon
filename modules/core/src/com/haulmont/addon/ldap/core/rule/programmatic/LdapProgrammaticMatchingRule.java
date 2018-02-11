package com.haulmont.addon.ldap.core.rule.programmatic;

import com.haulmont.addon.ldap.core.rule.ApplyMatchingRuleContext;
import com.haulmont.addon.ldap.entity.MatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRuleType;

import java.util.UUID;

public interface LdapProgrammaticMatchingRule extends MatchingRule {

    default UUID getId(){
        return UUID.randomUUID();
    }

    boolean checkProgrammaticMatchingRule(ApplyMatchingRuleContext applyMatchingRuleContext);

    default MatchingRuleType getRuleType() {
        return MatchingRuleType.PROGRAMMATIC;
    }

    String getProgrammaticRuleName();
}
