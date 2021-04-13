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

package com.haulmont.addon.ldap.utils;

import com.haulmont.addon.ldap.dto.CustomLdapMatchingRuleDto;
import com.haulmont.addon.ldap.entity.*;
import com.haulmont.cuba.security.entity.Role;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserRole;
import com.haulmont.cuba.security.role.RolesService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.haulmont.addon.ldap.entity.MatchingRuleType.CUSTOM;
import static com.haulmont.addon.ldap.utils.MatchingRuleUtils.NAME;
import static org.apache.commons.collections4.CollectionUtils.isEqualCollection;

@Component(NAME)
public class MatchingRuleUtils {
    public static final String NAME = "ldap_MatchingRuleUtils";

    @Inject
    private RolesService rolesService;

    private String getStringCondition(List<SimpleRuleCondition> conditions) {
        StringBuilder sb = new StringBuilder();
        if (CollectionUtils.isNotEmpty(conditions)) {
            for (SimpleRuleCondition simpleRuleCondition : conditions) {
                sb.append(simpleRuleCondition.getAttribute());
                sb.append("=");
                sb.append(simpleRuleCondition.getAttributeValue());
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public String generateMatchingRuleOptionsColumn(AbstractCommonMatchingRule entity) {
        if (CUSTOM == entity.getRuleType()) return StringUtils.EMPTY;

        AbstractDbStoredMatchingRule dbStoredMatchingRule = ((AbstractDbStoredMatchingRule) entity);
        StringBuilder sb = new StringBuilder();
        if (dbStoredMatchingRule.getIsTerminalRule()) {
            sb.append("Terminal; ");
        } else {
            sb.append("Pass-through; ");
        }

        if (dbStoredMatchingRule.getIsOverrideExistingAccessGroup()) {
            sb.append("Override access group; ");
        } else {
            sb.append("Don't override access group; ");
        }

        if (dbStoredMatchingRule.getIsOverrideExistingRoles()) {
            sb.append("Override existing roles; ");
        } else {
            sb.append("Don't Override existing roles; ");
        }
        return sb.toString();
    }

    public String generateMatchingRuleRolesAccessGroupColumn(AbstractCommonMatchingRule entity) {
        if (CUSTOM == entity.getRuleType()) return StringUtils.EMPTY;

        AbstractDbStoredMatchingRule dbStoredMatchingRule = ((AbstractDbStoredMatchingRule) entity);
        StringBuilder sb = new StringBuilder("Roles: ");
        for (Role role : ((AbstractDbStoredMatchingRule) entity).getRoles()) {
            sb.append(role.getName());
            sb.append(";");
        }
        sb.append("\n");
        sb.append("Access group: ");
        sb.append(dbStoredMatchingRule.getAccessGroup() == null ? StringUtils.EMPTY : dbStoredMatchingRule.getAccessGroup().getName());

        return sb.toString();
    }

    public String generateMatchingRuleTableConditionColumn(AbstractCommonMatchingRule entity) {
        MatchingRuleType mrt = entity.getRuleType();

        switch (mrt) {
            case SIMPLE:
                SimpleMatchingRule simpleMatchingRule = (SimpleMatchingRule) entity;
                return getStringCondition(simpleMatchingRule.getConditions());
            case SCRIPTING:
                ScriptingMatchingRule scriptingMatchingRule = (ScriptingMatchingRule) entity;
                return scriptingMatchingRule.getScriptingCondition();
            case CUSTOM:
                CustomLdapMatchingRuleDto customLdapMatchingRule = ((CustomLdapMatchingRuleDto) entity);
                return customLdapMatchingRule.getDescription();
            default:
                return StringUtils.EMPTY;
        }
    }

    public String generateMatchingRuleTableOrderColumn(AbstractCommonMatchingRule entity) {
        return entity.getOrder().getOrder().toString();
    }

    public String generateMatchingRuleTableDescriptionColumn(AbstractCommonMatchingRule entity) {
        if (CUSTOM == entity.getRuleType()) {
            CustomLdapMatchingRuleDto customLdapMatchingRule = ((CustomLdapMatchingRuleDto) entity);
            return customLdapMatchingRule.getName();
        } else {
            return entity.getDescription();
        }
    }

    public boolean validateRuleOrder(Integer order) {
        if (order == null || order <= 0 || order == Integer.MAX_VALUE) {
            return false;
        }
        return true;
    }

    public List<Role> getRoles(User user) {
        return user.getUserRoles().stream()
                .peek(ur -> {
                    if (ur.getRole() == null) {
                        ur.setRole(rolesService.getRoleDefinitionAndTransformToRole(ur.getRoleName()));
                    }
                })
                .map(UserRole::getRole)
                .collect(Collectors.toList());
    }

    public boolean isEqualRoles(User first, User second) {
        return isEqualCollection(getRoles(first), getRoles(second));
    }

    public boolean isEqualGroups(User first, User second) {
        return Objects.equals(first.getGroup(), second.getGroup());
    }
}
