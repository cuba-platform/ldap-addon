package com.haulmont.addon.ldap.utils;

import com.haulmont.addon.ldap.config.LdapConfig;
import com.haulmont.addon.ldap.entity.*;
import com.haulmont.cuba.security.entity.Role;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import java.util.List;

import static com.haulmont.addon.ldap.utils.MatchingRuleUtils.NAME;

@Component(NAME)
public class MatchingRuleUtils {
    public static final String NAME = "ldap_MatchingRuleUtils";

    @Inject
    private LdapConfig ldapConfig;

    public String getLdapAttributeName(SimpleRuleConditionAttribute attribute) {
        switch (attribute) {
            case EMAIL:
                return ldapConfig.getEmailAttribute();
            case CN:
                return ldapConfig.getCnAttribute();
            case SN:
                return ldapConfig.getSnAttribute();
            case MEMBER_OF:
                return ldapConfig.getMemberOfAttribute();
            case ACCESS_GROUP:
                return ldapConfig.getAccessGroupAttribute();
            case POSITION:
                return ldapConfig.getPositionAttribute();
            case LANGUAGE:
                return ldapConfig.getLanguageAttribute();
            case OU:
                return ldapConfig.getOuAttribute();
            default:
                return null;
        }
    }

    private String getStringCondition(List<SimpleRuleCondition> conditions) {
        StringBuilder sb = new StringBuilder();
        if (CollectionUtils.isNotEmpty(conditions)) {
            for (SimpleRuleCondition simpleRuleCondition : conditions) {
                sb.append(getLdapAttributeName(simpleRuleCondition.getAttribute()));
                sb.append("=");
                sb.append(simpleRuleCondition.getAttributeValue());
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public String generateMatchingRuleOptionsColumn (AbstractMatchingRule entity){
        StringBuilder sb = new StringBuilder();
        if (entity.getIsTerminalRule()) {
            sb.append("Terminal; ");
        } else {
            sb.append("Pass-through; ");
        }

        if (entity.getIsOverrideExistingAccessGroup()) {
            sb.append("Override access group; ");
        } else {
            sb.append("Don't override access group; ");
        }

        if (entity.getIsOverrideExistingRoles()) {
            sb.append("Override existing roles; ");
        } else {
            sb.append("Don't Override existing roles; ");
        }
        return sb.toString();
    }

    public String generateMatchingRuleRolesAccessGroupColumn (AbstractMatchingRule entity){
        StringBuilder sb = new StringBuilder("Roles: ");
        for (Role role : entity.getRoles()) {
            sb.append(role.getName());
            sb.append(";");
        }
        sb.append("\n");
        sb.append("Access group: ");
        sb.append(entity.getAccessGroup() == null ? StringUtils.EMPTY : entity.getAccessGroup().getName());

        return sb.toString();
    }

    public String generateMatchingRuleTableConditionColumn(AbstractMatchingRule entity){
        if (entity instanceof SimpleMatchingRule) {
            SimpleMatchingRule simpleMatchingRule = (SimpleMatchingRule) entity;
            return getStringCondition(simpleMatchingRule.getConditions());
        } else if ((entity instanceof ScriptingMatchingRule)) {
            ScriptingMatchingRule scriptingMatchingRule = (ScriptingMatchingRule) entity;
            return scriptingMatchingRule.getScriptingCondition();
        } else {
            return StringUtils.EMPTY;
        }
    }
}
