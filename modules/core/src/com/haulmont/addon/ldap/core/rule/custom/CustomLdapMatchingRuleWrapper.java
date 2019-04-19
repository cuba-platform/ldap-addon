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

package com.haulmont.addon.ldap.core.rule.custom;

import com.haulmont.addon.ldap.core.dao.MatchingRuleOrderDao;
import com.haulmont.addon.ldap.core.dao.MatchingRuleStatusDao;
import com.haulmont.addon.ldap.core.rule.LdapMatchingRuleContext;
import com.haulmont.addon.ldap.entity.CommonMatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRuleOrder;
import com.haulmont.addon.ldap.entity.MatchingRuleStatus;
import com.haulmont.addon.ldap.entity.MatchingRuleType;
import com.haulmont.cuba.core.global.AppBeans;

import java.lang.annotation.Annotation;

public class CustomLdapMatchingRuleWrapper implements CustomLdapMatchingRule, CommonMatchingRule {

    private CustomLdapMatchingRule customLdapMatchingRule;

    private MatchingRuleOrder matchingRuleOrder;

    private MatchingRuleStatus matchingRuleStatus;

    private String description;

    private String matchingRuleId;

    private String name;

    public CustomLdapMatchingRuleWrapper(CustomLdapMatchingRule customLdapMatchingRule) {
        this.customLdapMatchingRule = customLdapMatchingRule;
        this.matchingRuleId = customLdapMatchingRule.getClass().getName();
        this.matchingRuleOrder = getOrder(customLdapMatchingRule);
        this.matchingRuleStatus = getStatus(customLdapMatchingRule);
        this.description = getDescription(customLdapMatchingRule);
        this.name = getName(customLdapMatchingRule);
    }

    @Override
    public boolean applyCustomMatchingRule(LdapMatchingRuleContext ldapMatchingRuleContext) {
        return customLdapMatchingRule.applyCustomMatchingRule(ldapMatchingRuleContext);
    }

    @Override
    public MatchingRuleOrder getOrder() {
        return matchingRuleOrder;
    }

    @Override
    public MatchingRuleStatus getStatus() {
        return matchingRuleStatus;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getMatchingRuleId() {
        return matchingRuleId;
    }

    @Override
    public MatchingRuleType getRuleType() {
        return MatchingRuleType.CUSTOM;
    }

    public String getName() {
        return name;
    }

    private String getDescription(CustomLdapMatchingRule customLdapMatchingRule) {
        Class clazz = customLdapMatchingRule.getClass();
        Annotation annotation = clazz.getAnnotation(LdapMatchingRule.class);
        LdapMatchingRule ldapMatchingRule = (LdapMatchingRule) annotation;
        return ldapMatchingRule.condition();
    }

    private MatchingRuleOrder getOrder(CustomLdapMatchingRule customLdapMatchingRule) {
        MatchingRuleOrderDao matchingRuleOrderDao = AppBeans.get(MatchingRuleOrderDao.class);
        return matchingRuleOrderDao.getCustomRuleOrder(customLdapMatchingRule.getClass().getName());
    }

    private MatchingRuleStatus getStatus(CustomLdapMatchingRule customLdapMatchingRule) {
        MatchingRuleStatusDao matchingRuleStatusDao = AppBeans.get(MatchingRuleStatusDao.class);
        return matchingRuleStatusDao.getMatchingRuleStatus(customLdapMatchingRule.getClass().getName());
    }

    private String getName(CustomLdapMatchingRule customLdapMatchingRule) {
        Class clazz = customLdapMatchingRule.getClass();
        Annotation annotation = clazz.getAnnotation(LdapMatchingRule.class);
        LdapMatchingRule ldapMatchingRule = (LdapMatchingRule) annotation;
        return ldapMatchingRule.name();
    }
}
