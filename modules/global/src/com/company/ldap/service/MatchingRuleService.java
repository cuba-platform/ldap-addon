package com.company.ldap.service;

import com.company.ldap.entity.AbstractMatchingRule;
import com.company.ldap.entity.MatchingRule;

import java.util.List;

public interface MatchingRuleService {
    String NAME = "ldap_MatchingRuleService";

    List<MatchingRule> getMatchingRules();

    int getMatchingRulesCount();

    List<AbstractMatchingRule> getMatchingRulesGui();
}
