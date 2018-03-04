package com.haulmont.addon.ldap.entity;


public interface CommonMatchingRule {

    String getMatchingRuleId();

    MatchingRuleType getRuleType();

    String getDescription();

    MatchingRuleOrder getOrder();

    Boolean getIsDisabled();
}
