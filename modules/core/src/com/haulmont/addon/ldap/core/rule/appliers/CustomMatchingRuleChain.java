package com.haulmont.addon.ldap.core.rule.appliers;

import com.haulmont.addon.ldap.core.rule.ApplyMatchingRuleContext;
import com.haulmont.addon.ldap.core.rule.programmatic.CustomLdapMatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRuleType;
import com.haulmont.cuba.core.global.Metadata;

public class CustomMatchingRuleChain extends MatchingRuleChain {

    public CustomMatchingRuleChain(MatchingRuleChain next, Metadata metadata) {
        super(next, MatchingRuleType.PROGRAMMATIC, metadata);
    }

    @Override
    boolean checkRule(MatchingRule matchingRule, ApplyMatchingRuleContext applyMatchingRuleContext) {
        CustomLdapMatchingRule programmaticMatchingRule = (CustomLdapMatchingRule) matchingRule;
        return programmaticMatchingRule.checkCustomMatchingRule(applyMatchingRuleContext);
    }
}
