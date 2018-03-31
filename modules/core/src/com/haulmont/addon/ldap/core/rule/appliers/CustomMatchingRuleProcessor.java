package com.haulmont.addon.ldap.core.rule.appliers;

import com.haulmont.addon.ldap.core.rule.LdapMatchingRuleContext;
import com.haulmont.addon.ldap.core.rule.custom.CustomLdapMatchingRule;
import com.haulmont.addon.ldap.entity.CommonMatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRuleType;
import org.springframework.stereotype.Component;

import static com.haulmont.addon.ldap.core.rule.appliers.CustomMatchingRuleProcessor.NAME;


@Component(NAME)
public class CustomMatchingRuleProcessor extends MatchingRuleProcessor {

    public static final String NAME = "ldap_CustomMatchingRuleProcessor";

    public CustomMatchingRuleProcessor() {
        super(MatchingRuleType.CUSTOM);
    }

    @Override
    public boolean applyMatchingRule(CommonMatchingRule matchingRule, LdapMatchingRuleContext ldapMatchingRuleContext) {
        CustomLdapMatchingRule programmaticMatchingRule = (CustomLdapMatchingRule) matchingRule;
        boolean isCustomRuleApplied = programmaticMatchingRule.applyCustomMatchingRule(ldapMatchingRuleContext);
        if (isCustomRuleApplied) {
            ldapMatchingRuleContext.getAppliedRules().add(matchingRule);
        }
        return isCustomRuleApplied;
    }
}
