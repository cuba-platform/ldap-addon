package com.haulmont.addon.ldap.service;

import com.haulmont.addon.ldap.entity.AbstractCommonMatchingRule;
import com.haulmont.addon.ldap.entity.CommonMatchingRule;

import java.util.List;

public interface MatchingRuleService {
    String NAME = "ldap_MatchingRuleService";

    /**
     * Returns count of matching rules.
     */
    int getMatchingRulesCount();

    /**
     * Returns GUI representation of matching rules.
     */
    List<AbstractCommonMatchingRule> getMatchingRulesGui();

    /**
     * Persists matching rules state from LDAP Matching Rule screen.
     */
    void saveMatchingRules(List<AbstractCommonMatchingRule> matchingRules, List<AbstractCommonMatchingRule> matchingRulesToDelete);

}
