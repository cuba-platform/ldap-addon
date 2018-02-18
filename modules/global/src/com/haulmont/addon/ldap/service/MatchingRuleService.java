package com.haulmont.addon.ldap.service;

import com.haulmont.addon.ldap.entity.AbstractMatchingRule;
import com.haulmont.addon.ldap.entity.FixedMatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRule;

import java.util.List;
import java.util.UUID;

public interface MatchingRuleService {
    String NAME = "ldap_MatchingRuleService";

    List<MatchingRule> getMatchingRules();

    int getMatchingRulesCount();

    List<AbstractMatchingRule> getMatchingRulesGui();

}
