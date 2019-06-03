package com.haulmont.addon.ldap.utils;

import com.haulmont.addon.ldap.dto.CustomLdapMatchingRuleDto;
import com.haulmont.addon.ldap.entity.*;
import com.haulmont.cuba.security.entity.Role;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserRole;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static com.haulmont.addon.ldap.entity.MatchingRuleType.CUSTOM;
import static com.haulmont.addon.ldap.utils.MatchingRuleUtils.NAME;

@Component(NAME)
public class MatchingRuleUtils {
    public static final String NAME = "ldap_MatchingRuleUtils";

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

    public static List<Role> getRoles(User user) {
        return user.getUserRoles().stream()
                .map(UserRole::getRole)
                .collect(Collectors.toList());
    }
}
