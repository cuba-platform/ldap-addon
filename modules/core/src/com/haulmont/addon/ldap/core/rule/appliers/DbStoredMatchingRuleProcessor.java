package com.haulmont.addon.ldap.core.rule.appliers;

import com.haulmont.addon.ldap.core.rule.ApplyMatchingRuleContext;
import com.haulmont.addon.ldap.entity.AbstractDbStoredMatchingRule;
import com.haulmont.addon.ldap.entity.CommonMatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRuleType;

public abstract class DbStoredMatchingRuleProcessor extends MatchingRuleProcessor {

    public DbStoredMatchingRuleProcessor(MatchingRuleType matchingRuleType) {
        super(matchingRuleType);
    }

    public abstract boolean checkMatchingRule(AbstractDbStoredMatchingRule matchingRule, ApplyMatchingRuleContext applyMatchingRuleContext);

    @Override
    void applyMatchingRule(CommonMatchingRule matchingRule, ApplyMatchingRuleContext applyMatchingRuleContext) {
        AbstractDbStoredMatchingRule abstractDbStoredMatchingRule = (AbstractDbStoredMatchingRule) matchingRule;

        if (checkMatchingRule(abstractDbStoredMatchingRule, applyMatchingRuleContext)) {
            changeGroupAndRolesInMatchingRuleContext(abstractDbStoredMatchingRule, applyMatchingRuleContext);
            applyMatchingRuleContext.setAnyRuleApply(true);
            applyMatchingRuleContext.getAppliedRules().add(abstractDbStoredMatchingRule);
            if (abstractDbStoredMatchingRule.getAccessGroup() != null) {
                applyMatchingRuleContext.getAppliedGroups().add(abstractDbStoredMatchingRule.getAccessGroup());
            }
            applyMatchingRuleContext.getAppliedRoles().addAll(abstractDbStoredMatchingRule.getRoles());
            applyMatchingRuleContext.setTerminalRuleApply(abstractDbStoredMatchingRule.getIsTerminalRule());
        }
    }

    private void changeGroupAndRolesInMatchingRuleContext(AbstractDbStoredMatchingRule abstractDbStoredMatchingRule, ApplyMatchingRuleContext applyMatchingRuleContext) {
        if (abstractDbStoredMatchingRule.getIsOverrideExistingAccessGroup()) {
            applyMatchingRuleContext.setCurrentGroup(abstractDbStoredMatchingRule.getAccessGroup());
        }
        if (abstractDbStoredMatchingRule.getIsOverrideExistingRoles()) {
            applyMatchingRuleContext.getCurrentRoles().clear();
        }
        applyMatchingRuleContext.getCurrentRoles().addAll(abstractDbStoredMatchingRule.getRoles());
    }
}
