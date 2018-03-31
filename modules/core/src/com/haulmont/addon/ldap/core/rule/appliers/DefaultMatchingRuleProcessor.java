package com.haulmont.addon.ldap.core.rule.appliers;

import com.haulmont.addon.ldap.core.rule.LdapMatchingRuleContext;
import com.haulmont.addon.ldap.entity.AbstractDbStoredMatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRuleType;
import org.springframework.stereotype.Component;

import static com.haulmont.addon.ldap.core.rule.appliers.DefaultMatchingRuleProcessor.NAME;

@Component(NAME)
public class DefaultMatchingRuleProcessor extends DbStoredMatchingRuleProcessor {

    public static final String NAME = "ldap_DefaultMatchingRuleProcessor";

    public DefaultMatchingRuleProcessor() {
        super(MatchingRuleType.DEFAULT);
    }

    @Override
    public boolean checkMatchingRule(AbstractDbStoredMatchingRule matchingRule, LdapMatchingRuleContext ldapMatchingRuleContext) {
        return ldapMatchingRuleContext.getAppliedRules().isEmpty();
    }
}
