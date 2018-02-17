package com.haulmont.addon.ldap.core.rule.programmatic;

import com.haulmont.addon.ldap.core.rule.ApplyMatchingRuleContext;
import com.haulmont.addon.ldap.entity.MatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRuleType;
import org.apache.commons.lang.StringUtils;

import java.lang.annotation.Annotation;
import java.util.UUID;

public interface CustomLdapMatchingRule extends MatchingRule {

    default UUID getId() {
        return UUID.randomUUID();
    }

    boolean checkCustomMatchingRule(ApplyMatchingRuleContext applyMatchingRuleContext);

    default MatchingRuleType getRuleType() {
        return MatchingRuleType.PROGRAMMATIC;
    }

    @Override
    default Integer getOrder() {
        return 0;
    }

    @Override
    default String getDescription() {
        Class clazz = this.getClass();
        if (clazz.isAnnotationPresent(LdapMatchingRule.class)) {
            Annotation annotation = clazz.getAnnotation(LdapMatchingRule.class);
            LdapMatchingRule ldapMatchingRule = (LdapMatchingRule) annotation;
            return ldapMatchingRule.description();
        }
        return StringUtils.EMPTY;
    }

}
