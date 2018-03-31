package com.haulmont.addon.ldap.core.rule.appliers;

import com.haulmont.addon.ldap.core.rule.LdapMatchingRuleContext;
import com.haulmont.addon.ldap.entity.AbstractDbStoredMatchingRule;
import com.haulmont.addon.ldap.entity.CommonMatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRuleType;

public abstract class DbStoredMatchingRuleProcessor extends MatchingRuleProcessor {

    public DbStoredMatchingRuleProcessor(MatchingRuleType matchingRuleType) {
        super(matchingRuleType);
    }

    public abstract boolean checkMatchingRule(AbstractDbStoredMatchingRule matchingRule, LdapMatchingRuleContext ldapMatchingRuleContext);

    @Override
    boolean applyMatchingRule(CommonMatchingRule matchingRule, LdapMatchingRuleContext ldapMatchingRuleContext) {
        AbstractDbStoredMatchingRule abstractDbStoredMatchingRule = (AbstractDbStoredMatchingRule) matchingRule;
        boolean isRuleApplied = checkMatchingRule(abstractDbStoredMatchingRule, ldapMatchingRuleContext);

        if (isRuleApplied) {
            changeGroupAndRolesInMatchingRuleContext(abstractDbStoredMatchingRule, ldapMatchingRuleContext);
            ldapMatchingRuleContext.getAppliedRules().add(abstractDbStoredMatchingRule);
            ldapMatchingRuleContext.setTerminalRuleApply(abstractDbStoredMatchingRule.getIsTerminalRule());
        }
        return isRuleApplied;
    }

    private void changeGroupAndRolesInMatchingRuleContext(AbstractDbStoredMatchingRule abstractDbStoredMatchingRule,
                                                          LdapMatchingRuleContext ldapMatchingRuleContext) {
        if (abstractDbStoredMatchingRule.getIsOverrideExistingAccessGroup() || ldapMatchingRuleContext.getGroup() == null) {
            ldapMatchingRuleContext.setGroup(abstractDbStoredMatchingRule.getAccessGroup());
        }
        if (abstractDbStoredMatchingRule.getIsOverrideExistingRoles()) {
            ldapMatchingRuleContext.getRoles().clear();
        }
        ldapMatchingRuleContext.getRoles().addAll(abstractDbStoredMatchingRule.getRoles());
    }
}
