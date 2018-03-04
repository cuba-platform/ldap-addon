package com.haulmont.addon.ldap.core.rule.programmatic;

import com.haulmont.addon.ldap.core.rule.ApplyMatchingRuleContext;
import com.haulmont.addon.ldap.entity.MatchingRuleOrder;

public class CustomLdapMatchingRuleWrapper implements CustomLdapMatchingRule {

    private CustomLdapMatchingRule customLdapMatchingRule;

    private MatchingRuleOrder matchingRuleOrder;

    private Boolean isDisabled;

    private String description;

    private String matchingRuleId;

    public CustomLdapMatchingRuleWrapper(CustomLdapMatchingRule customLdapMatchingRule, String matchingRuleId, MatchingRuleOrder matchingRuleOrder, Boolean isDisabled, String description) {
        this.customLdapMatchingRule = customLdapMatchingRule;
        this.matchingRuleId = matchingRuleId;
        this.matchingRuleOrder = matchingRuleOrder;
        this.isDisabled = isDisabled;
        this.description = description;
    }

    @Override
    public void applyCustomMatchingRule(ApplyMatchingRuleContext applyMatchingRuleContext) {
        customLdapMatchingRule.applyCustomMatchingRule(applyMatchingRuleContext);
    }

    @Override
    public MatchingRuleOrder getOrder() {
        return matchingRuleOrder;
    }

    @Override
    public Boolean getIsDisabled() {
        return isDisabled;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getMatchingRuleId() {
        return matchingRuleId;
    }
}
