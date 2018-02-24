package com.haulmont.addon.ldap.core.rule.programmatic;

import com.haulmont.addon.ldap.core.rule.ApplyMatchingRuleContext;
import com.haulmont.addon.ldap.entity.MatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRuleType;
import org.apache.commons.lang.StringUtils;

import java.lang.annotation.Annotation;
import java.util.UUID;

public abstract class CustomLdapMatchingRule implements MatchingRule {

    private Integer order = 0;
    private UUID id = UUID.randomUUID();

    public UUID getId() {
        return id;
    }

    @Override
    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public abstract boolean checkCustomMatchingRule(ApplyMatchingRuleContext applyMatchingRuleContext);

    public MatchingRuleType getRuleType() {
        return MatchingRuleType.CUSTOM;
    }

    @Override
    public String getDescription() {
        Class clazz = this.getClass();
        if (clazz.isAnnotationPresent(LdapMatchingRule.class)) {
            Annotation annotation = clazz.getAnnotation(LdapMatchingRule.class);
            LdapMatchingRule ldapMatchingRule = (LdapMatchingRule) annotation;
            return ldapMatchingRule.description();
        }
        return StringUtils.EMPTY;
    }

}
