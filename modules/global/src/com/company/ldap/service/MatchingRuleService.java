package com.company.ldap.service;

import com.company.ldap.entity.AbstractMatchingRule;
import com.company.ldap.entity.FixedMatchingRule;
import com.company.ldap.entity.MatchingRule;

import java.util.List;
import java.util.UUID;

public interface MatchingRuleService {
    String NAME = "ldap_MatchingRuleService";

    List<MatchingRule> getMatchingRules();

    int getMatchingRulesCount();

    List<AbstractMatchingRule> getMatchingRulesGui();

    FixedMatchingRule getFixedMatchingRule();

    void updateDisabledStateForMatchingRule(UUID id, Boolean isDisabled);
}
