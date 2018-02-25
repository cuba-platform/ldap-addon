package com.haulmont.addon.ldap.core.rule.appliers;

import com.haulmont.addon.ldap.core.rule.ApplyMatchingRuleContext;
import com.haulmont.addon.ldap.entity.MatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRuleType;
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

    public void applyMatchingRule(MatchingRule matchingRule, ApplyMatchingRuleContext applyMatchingRuleContext, User cubaUser) {
        if (!matchingRule.getIsDisabled() && isMatchingRuleTypeSupported(matchingRule) && checkRule(matchingRule, applyMatchingRuleContext)) {
            applyRuleToUser(matchingRule, cubaUser);
            applyMatchingRuleContext.setAnyRuleApply(true);
            applyMatchingRuleContext.getAppliedRules().add(matchingRule);
            if (matchingRule.getAccessGroup() != null) {
                applyMatchingRuleContext.getAppliedGroups().add(matchingRule.getAccessGroup());
            }
            applyMatchingRuleContext.getAppliedRoles().addAll(matchingRule.getRoles());
            if (matchingRule.getIsTerminalRule()) {//if terminal rule was satisfied stop execution chain
                applyMatchingRuleContext.setStopExecution(true);
                return;
            }
        }
        if (next != null) {//if exists next element in chain and no terminal rule was applied
            next.applyMatchingRule(matchingRule, applyMatchingRuleContext, cubaUser);
        }
    }

    private void applyRuleToUser(MatchingRule matchingRule, User cubaUser) {
        if (matchingRule.getIsOverrideExistingAccessGroup() || cubaUser.getGroup() == null) {
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
