package com.haulmont.addon.ldap.entity;


/**
 * Methods for Custom and DB stored matching rules.
 */
public interface CommonMatchingRule {

    /**
     * Returns unique id of matching rule.<br>
     * For DB stored matching rules it is value of FK column. For Custom rule it is canonical class name of Custom rule class.
     */
    String getMatchingRuleId();

    /**
     * Returns rule type
     */
    MatchingRuleType getRuleType();

    /**
     * Returns description
     */
    String getDescription();

    /**
     * Returns matching rule order
     */
    MatchingRuleOrder getOrder();

    /**
     * Returns matching rule status.<br>
     */
    MatchingRuleStatus getStatus();
}
