package com.haulmont.addon.ldap.service;

import com.haulmont.addon.ldap.entity.AbstractCommonMatchingRule;
import com.haulmont.addon.ldap.entity.CommonMatchingRule;

import java.util.List;

public interface MatchingRuleService {
    String NAME = "ldap_MatchingRuleService";

    List<CommonMatchingRule> getMatchingRules();

    int getMatchingRulesCount();

    List<AbstractCommonMatchingRule> getMatchingRulesGui();

    void saveMatchingRulesWithOrder(List<AbstractCommonMatchingRule> matchingRules, List<AbstractCommonMatchingRule> matchingRulesToDelete);

}
