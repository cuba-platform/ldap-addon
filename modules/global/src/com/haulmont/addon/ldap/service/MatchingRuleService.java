package com.haulmont.addon.ldap.service;

import com.haulmont.addon.ldap.entity.AbstractMatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRule;

import java.util.List;

public interface MatchingRuleService {
    String NAME = "ldap_MatchingRuleService";

    List<MatchingRule> getMatchingRules();

    int getMatchingRulesCount();

    List<AbstractMatchingRule> getMatchingRulesGui();

}
