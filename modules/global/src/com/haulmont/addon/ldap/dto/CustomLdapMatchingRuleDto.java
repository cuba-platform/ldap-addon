package com.haulmont.addon.ldap.dto;

import com.haulmont.addon.ldap.entity.AbstractCommonMatchingRule;
import com.haulmont.chile.core.annotations.MetaClass;

@MetaClass(name = "ldap$CustomLdapMatchingRuleDto")
public class CustomLdapMatchingRuleDto extends AbstractCommonMatchingRule {

    private String matchingRuleId;

    @Override
    public String getMatchingRuleId() {
        return matchingRuleId;
    }

    public void setMatchingRuleId(String matchingRuleId) {
        this.matchingRuleId = matchingRuleId;
    }
}
