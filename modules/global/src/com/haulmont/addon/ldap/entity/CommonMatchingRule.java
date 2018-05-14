package com.haulmont.addon.ldap.entity;


/**
 * Methods for Custom matching rules and rules stored in the DB.
 */
public interface CommonMatchingRule {

    /**
     * Returns a unique identifier of a matching rule.<br>
     * For matching rules stored in the DB, it is the value provided in the FK column.<br>
     * For Custom rules, it is a canonical class name of the Custom rule class.
     */
    String getMatchingRuleId();

    /**
     * Returns a rule type.
     */
    MatchingRuleType getRuleType();

    /**
     * Returns a rule description.
     */
    String getDescription();

    /**
     * Returns a matching rule order number.
     */
    MatchingRuleOrder getOrder();

    /**
     * Returns a matching rule status.
     */
    MatchingRuleStatus getStatus();
}
