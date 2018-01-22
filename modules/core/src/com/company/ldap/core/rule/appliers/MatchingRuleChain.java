package com.company.ldap.core.rule.appliers;

import com.company.ldap.core.rule.ApplyMatchingRuleContext;
import com.company.ldap.entity.AbstractMatchingRule;
import com.company.ldap.entity.MatchingRule;
import com.company.ldap.entity.MatchingRuleType;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.security.entity.Role;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserRole;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class MatchingRuleChain {

    private MatchingRuleChain next;

    private MatchingRuleType matchingRuleType;

    private Metadata metadata;

    public MatchingRuleChain(MatchingRuleChain next, MatchingRuleType matchingRuleType, Metadata metadata) {
        this.next = next;
        this.matchingRuleType = matchingRuleType;
        this.metadata = metadata;
    }

    private boolean isMatchingRuleTypeSupported(MatchingRule matchingRule) {
        return matchingRuleType.equals(matchingRule.getRuleType());
    }

    abstract boolean checkRule(MatchingRule matchingRule, ApplyMatchingRuleContext applyMatchingRuleContext);

    public void applyMatchingRules(List<MatchingRule> matchingRules, ApplyMatchingRuleContext applyMatchingRuleContext, User cubaUser) {
        //TODO:add sort by terminal and etc
        for (MatchingRule matchingRule : matchingRules) {
            if (isMatchingRuleTypeSupported(matchingRule) && checkRule(matchingRule, applyMatchingRuleContext)) {
                applyRuleToUser(matchingRule, cubaUser);
                if (matchingRule.getIsTerminalRule()) {//if terminal rule was satisfied stop execution chain
                    return;
                }
            }
        }
        if (next != null) {//if exists next element in chain and no terminal rule was applied
            next.applyMatchingRules(matchingRules, applyMatchingRuleContext, cubaUser);
        }
    }

    private void applyRuleToUser(MatchingRule matchingRule, User cubaUser) {
        if (matchingRule.getIsOverrideExistingAccessGroup()) {
            cubaUser.setGroup(matchingRule.getAccessGroup());
        }
        if (matchingRule.getIsOverrideExistingRoles()) {
            cubaUser.getUserRoles().clear();
            cubaUser.setUserRoles(createUserRoles(matchingRule.getRoles(), cubaUser));
        } else {
            List<Role> currentRoles = cubaUser.getUserRoles().stream().map(UserRole::getRole).collect(Collectors.toList());
            List<Role> newRoles = matchingRule.getRoles().stream().filter(role -> !currentRoles.contains(role)).collect(Collectors.toList());
            cubaUser.getUserRoles().addAll(createUserRoles(newRoles, cubaUser));
        }

    }

    private List<UserRole> createUserRoles(List<Role> roles, User user) {
        List<UserRole> result = new ArrayList<>(roles.size());
        for (Role role : roles) {
            UserRole userRole = metadata.create(UserRole.class);
            userRole.setUser(user);
            userRole.setRole(role);
            result.add(userRole);
        }
        return result;
    }

    public MatchingRuleType getMatchingRuleType() {
        return matchingRuleType;
    }

    public MatchingRuleChain getNext() {
        return next;
    }
}
