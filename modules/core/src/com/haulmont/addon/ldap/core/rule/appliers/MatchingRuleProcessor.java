package com.haulmont.addon.ldap.core.rule.appliers;

import com.haulmont.addon.ldap.core.rule.ApplyMatchingRuleContext;
import com.haulmont.addon.ldap.entity.CommonMatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRuleType;
import com.haulmont.cuba.security.entity.Role;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserRole;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class MatchingRuleProcessor {

    private MatchingRuleType matchingRuleType;

    public MatchingRuleProcessor(MatchingRuleType matchingRuleType) {
        this.matchingRuleType = matchingRuleType;
    }

    abstract void applyMatchingRule(CommonMatchingRule matchingRule, ApplyMatchingRuleContext applyMatchingRuleContext);

    public MatchingRuleType getMatchingRuleType() {
        return matchingRuleType;
    }

}
