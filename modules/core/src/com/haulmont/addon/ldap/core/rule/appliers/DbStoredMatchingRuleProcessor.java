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
    boolean applyMatchingRule(CommonMatchingRule matchingRule, ApplyMatchingRuleContext applyMatchingRuleContext) {
        AbstractDbStoredMatchingRule abstractDbStoredMatchingRule = (AbstractDbStoredMatchingRule) matchingRule;
        boolean isRuleApplied = checkMatchingRule(abstractDbStoredMatchingRule, applyMatchingRuleContext);

        if (isRuleApplied) {
            changeGroupAndRolesInMatchingRuleContext(abstractDbStoredMatchingRule, applyMatchingRuleContext);
            applyMatchingRuleContext.getAppliedRules().add(abstractDbStoredMatchingRule);
            applyMatchingRuleContext.setTerminalRuleApply(abstractDbStoredMatchingRule.getIsTerminalRule());
        }
        return isRuleApplied;
    }

    private void changeGroupAndRolesInMatchingRuleContext(AbstractDbStoredMatchingRule abstractDbStoredMatchingRule, ApplyMatchingRuleContext applyMatchingRuleContext) {
        if (abstractDbStoredMatchingRule.getIsOverrideExistingAccessGroup() || applyMatchingRuleContext.getGroup() == null) {
            applyMatchingRuleContext.setGroup(abstractDbStoredMatchingRule.getAccessGroup());
        }
        if (abstractDbStoredMatchingRule.getIsOverrideExistingRoles()) {
            applyMatchingRuleContext.getRoles().clear();
        }
        applyMatchingRuleContext.getRoles().addAll(abstractDbStoredMatchingRule.getRoles());
    }
}
