package com.haulmont.addon.ldap.utils;

import com.haulmont.addon.ldap.config.LdapConfig;
import com.haulmont.addon.ldap.entity.SimpleRuleCondition;
import com.haulmont.addon.ldap.entity.SimpleRuleConditionAttribute;
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

    public String getStringCondition(List<SimpleRuleCondition> conditions) {
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
}
