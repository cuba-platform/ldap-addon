package com.haulmont.addon.ldap.service;

import com.haulmont.addon.ldap.entity.AbstractCommonMatchingRule;

import java.util.List;

public interface MatchingRuleService {
    String NAME = "ldap_MatchingRuleService";

    /**
     * Returns the amount of existing matching rules.
     */
    int getMatchingRulesCount();

    /**
     * Returns the GUI representation of matching rules.
     */
    List<AbstractCommonMatchingRule> getMatchingRulesGui();

    /**
     * Persists the state of matching rules from LDAP Matching Rule Screen.
     */
    void saveMatchingRules(List<AbstractCommonMatchingRule> matchingRules, List<AbstractCommonMatchingRule> matchingRulesToDelete);

}
