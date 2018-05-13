package com.haulmont.addon.ldap.core.rule.custom;

import com.haulmont.addon.ldap.core.rule.LdapMatchingRuleContext;

/**
 * Every Custom rule must implement this interface
 */
public interface CustomLdapMatchingRule {

    boolean applyCustomMatchingRule(LdapMatchingRuleContext ldapMatchingRuleContext);

}
