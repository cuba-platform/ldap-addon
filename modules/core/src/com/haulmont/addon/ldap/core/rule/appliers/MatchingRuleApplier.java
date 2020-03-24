/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.addon.ldap.core.rule.appliers;

import com.haulmont.addon.ldap.core.rule.LdapMatchingRuleContext;
import com.haulmont.addon.ldap.entity.CommonMatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRuleType;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.security.entity.Role;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserRole;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.haulmont.addon.ldap.core.rule.appliers.MatchingRuleApplier.NAME;


@Component(NAME)
public class MatchingRuleApplier {

    public static final String NAME = "ldap_MatchingRuleApplier";

    private final Map<MatchingRuleType, MatchingRuleProcessor> matchingRuleProcessors = new HashMap<>();

    @Inject
    private Metadata metadata;

    /**
     * Collects all user roles and group that should be applied for user into LdapMatchingRuleContext
     * then applies them for user specified in LdapMatchingRuleContext
     */
    public void applyMatchingRules(List<CommonMatchingRule> allMatchingRules,
                                   LdapMatchingRuleContext ldapMatchingRuleContext,
                                   User beforeRulesApplyUserState) {
        collectActiveMatchingRulesToContext(getOrderedRulesToApply(allMatchingRules), ldapMatchingRuleContext);

        User cubaUser = ldapMatchingRuleContext.getCubaUser();
        cubaUser.setGroup(ldapMatchingRuleContext.getGroup());
        cubaUser.getUserRoles().clear(); //all roles that user should have are now in matching rule context
        applyUserRoles(cubaUser, ldapMatchingRuleContext.getRoles(), beforeRulesApplyUserState.getUserRoles());
    }

    private void collectActiveMatchingRulesToContext(List<CommonMatchingRule> matchingRules,
                                                     LdapMatchingRuleContext ldapMatchingRuleContext) {
        for (CommonMatchingRule commonMatchingRule : matchingRules) {
            matchingRuleProcessors.get(commonMatchingRule.getRuleType()).applyMatchingRule(commonMatchingRule, ldapMatchingRuleContext);
            if (ldapMatchingRuleContext.isTerminalRuleApply()) {
                break;
            }
        }
    }

    private static List<CommonMatchingRule> getOrderedRulesToApply(List<CommonMatchingRule> allMatchingRules) {
        return allMatchingRules.stream()
                .filter(cmr -> cmr.getStatus().getIsActive())
                .sorted(Comparator.comparing(mr -> mr.getOrder().getOrder()))
                .collect(Collectors.toList());
    }

    private void applyUserRoles(User cubaUser, Collection<Role> rolesToApply, Collection<UserRole> rolesBeforeApply) {
        rolesToApply.stream()
                .map(role -> rolesBeforeApply.stream()
                        .filter(ur -> ur.getRole() != null)
                        .filter(ur -> ur.getRole().equals(role))
                        .findFirst()
                        .orElse(createUserRole(cubaUser, role)))
                .forEach(cubaUser.getUserRoles()::add);
    }

    private UserRole createUserRole(User user, Role role) {
        UserRole userRole = metadata.create(UserRole.class);
        userRole.setUser(user);
        userRole.setRoleName(role.getName());
        userRole.setRole(role);
        return userRole;
    }

    @EventListener
    public void initializeMatchingRuleProcessors(ContextRefreshedEvent event) {
        Map<String, MatchingRuleProcessor> processorMap = event.getApplicationContext().getBeansOfType(MatchingRuleProcessor.class);
        for (Map.Entry<String, MatchingRuleProcessor> mrp : processorMap.entrySet()) {
            matchingRuleProcessors.put(mrp.getValue().getMatchingRuleType(), mrp.getValue());
        }
    }
}
