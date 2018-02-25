package com.haulmont.addon.ldap.core.rule.programmatic;

import com.haulmont.addon.ldap.core.rule.ApplyMatchingRuleContext;
import com.haulmont.addon.ldap.entity.MatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRuleOrder;
import com.haulmont.addon.ldap.entity.MatchingRuleType;
import org.apache.commons.lang.StringUtils;

import java.lang.annotation.Annotation;
import java.util.UUID;

//TODO: добавить ApplyMatchingRuleContext как ThreadLocal
public abstract class CustomLdapMatchingRule implements MatchingRule, Cloneable {

    private MatchingRuleOrder order;

    public abstract boolean checkCustomMatchingRule(ApplyMatchingRuleContext applyMatchingRuleContext);

    public final MatchingRuleType getRuleType() {
        return MatchingRuleType.CUSTOM;
    }

    public final UUID getId() {
        Class clazz = this.getClass();
        Annotation annotation = clazz.getAnnotation(LdapMatchingRule.class);
        LdapMatchingRule ldapMatchingRule = (LdapMatchingRule) annotation;
        return UUID.fromString(ldapMatchingRule.uuid());
    }

    @Override
    public final String getDescription() {
        Class clazz = this.getClass();
        Annotation annotation = clazz.getAnnotation(LdapMatchingRule.class);
        LdapMatchingRule ldapMatchingRule = (LdapMatchingRule) annotation;
        return ldapMatchingRule.description();
    }

    @Override
    public final MatchingRuleOrder getOrder() {
        return order;
    }

    public final void setOrder(MatchingRuleOrder order) {
        this.order = order;
    }

    public final CustomLdapMatchingRule clone(CustomLdapMatchingRule source) {
        CustomLdapMatchingRule temp = null;
        try {
            temp = (CustomLdapMatchingRule) this.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return temp;
    }
}
