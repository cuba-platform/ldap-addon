package com.haulmont.addon.ldap.core.rule.custom;

import com.haulmont.addon.ldap.core.rule.ApplyMatchingRuleContext;

public interface CustomLdapMatchingRule {

    boolean applyCustomMatchingRule(ApplyMatchingRuleContext applyMatchingRuleContext);

}
