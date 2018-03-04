package com.haulmont.addon.ldap.core.rule.programmatic;

import com.haulmont.addon.ldap.core.dao.MatchingRuleOrderDao;
import com.haulmont.addon.ldap.core.rule.ApplyMatchingRuleContext;
import com.haulmont.addon.ldap.entity.CommonMatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRuleOrder;
import com.haulmont.addon.ldap.entity.MatchingRuleType;
import com.haulmont.cuba.core.global.AppBeans;

import java.lang.annotation.Annotation;

public interface CustomLdapMatchingRule extends CommonMatchingRule {


    void applyCustomMatchingRule(ApplyMatchingRuleContext applyMatchingRuleContext);

    default MatchingRuleType getRuleType() {
        return MatchingRuleType.CUSTOM;
    }

    default String getMatchingRuleId() {
        return this.getClass().getName();
    }

    @Override
    default String getDescription() {
        Class clazz = this.getClass();
        Annotation annotation = clazz.getAnnotation(LdapMatchingRule.class);
        LdapMatchingRule ldapMatchingRule = (LdapMatchingRule) annotation;
        return ldapMatchingRule.description();
    }

    @Override
    default MatchingRuleOrder getOrder() {
        MatchingRuleOrderDao matchingRuleOrderDao = AppBeans.get(MatchingRuleOrderDao.class);
        return matchingRuleOrderDao.getCustomRuleOrder(getMatchingRuleId());
    }

}
