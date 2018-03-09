package com.haulmont.addon.ldap.core.rule.appliers;

import com.haulmont.addon.ldap.core.rule.ApplyMatchingRuleContext;
import com.haulmont.addon.ldap.core.rule.programmatic.CustomLdapMatchingRule;
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
    public boolean applyMatchingRule(CommonMatchingRule matchingRule, ApplyMatchingRuleContext applyMatchingRuleContext) {
        CustomLdapMatchingRule programmaticMatchingRule = (CustomLdapMatchingRule) matchingRule;
        return programmaticMatchingRule.applyCustomMatchingRule(applyMatchingRuleContext);
    }
}
