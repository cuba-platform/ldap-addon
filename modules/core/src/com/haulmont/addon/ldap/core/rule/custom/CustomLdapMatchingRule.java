package com.haulmont.addon.ldap.core.rule.custom;

import com.haulmont.addon.ldap.core.rule.LdapMatchingRuleContext;

public interface CustomLdapMatchingRule {

    boolean applyCustomMatchingRule(LdapMatchingRuleContext ldapMatchingRuleContext);

}
