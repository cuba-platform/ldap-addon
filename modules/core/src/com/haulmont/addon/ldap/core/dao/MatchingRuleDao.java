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

package com.haulmont.addon.ldap.core.dao;

import com.haulmont.addon.ldap.core.rule.custom.CustomLdapMatchingRule;
import com.haulmont.addon.ldap.core.rule.custom.CustomLdapMatchingRuleWrapper;
import com.haulmont.addon.ldap.dto.CustomLdapMatchingRuleDto;
import com.haulmont.addon.ldap.entity.*;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.haulmont.addon.ldap.core.dao.MatchingRuleDao.NAME;
import static com.haulmont.addon.ldap.entity.MatchingRuleType.CUSTOM;
import static com.haulmont.addon.ldap.entity.MatchingRuleType.DEFAULT;

@Component(NAME)
public class MatchingRuleDao {

    public final static String NAME = "ldap_MatchingRuleDao";

    private final static Integer DEFAULT_RULE_ORDER = 0;

    @Inject
    private Persistence persistence;

    @Inject
    private Metadata metadata;

    @Inject
    private Messages messages;

    @Inject
    private MatchingRuleOrderDao matchingRuleOrderDao;

    @Inject
    private MatchingRuleStatusDao matchingRuleStatusDao;

    @Inject
    private DaoHelper daoHelper;

    public CustomLdapMatchingRuleDto mapCustomRuleToDto(CustomLdapMatchingRuleWrapper customLdapMatchingRule) {
        CustomLdapMatchingRuleDto customLdapMatchingRuleDto = metadata.create(CustomLdapMatchingRuleDto.class);
        customLdapMatchingRuleDto.setMatchingRuleId(customLdapMatchingRule.getMatchingRuleId());
        customLdapMatchingRuleDto.setDescription(customLdapMatchingRule.getDescription());
        customLdapMatchingRuleDto.setOrder(customLdapMatchingRule.getOrder());
        customLdapMatchingRuleDto.setRuleType(customLdapMatchingRule.getRuleType());
        customLdapMatchingRuleDto.setStatus(customLdapMatchingRule.getStatus());
        customLdapMatchingRuleDto.setName(customLdapMatchingRule.getName());
        return customLdapMatchingRuleDto;
    }

    public List<CustomLdapMatchingRuleWrapper> getCustomMatchingRules() {
        List<CustomLdapMatchingRuleWrapper> result = new ArrayList<>();
        Map<String, CustomLdapMatchingRule> map = AppBeans.getAll(CustomLdapMatchingRule.class);
        if (map != null) {
            for (Map.Entry<String, CustomLdapMatchingRule> me : map.entrySet()) {
                CustomLdapMatchingRule cmr = me.getValue();
                CustomLdapMatchingRuleWrapper wrapper = new CustomLdapMatchingRuleWrapper(cmr);
                result.add(wrapper);
            }
        }
        return result;
    }

    @Transactional
    public List<CommonMatchingRule> getMatchingRules() {
        List<CommonMatchingRule> result = new ArrayList<>();
        TypedQuery<AbstractDbStoredMatchingRule> query = persistence.getEntityManager()
                .createQuery("select mr from ldap$AbstractDbStoredMatchingRule mr", AbstractDbStoredMatchingRule.class);
        query.setViewName("abstractDbStoredMatchingRule-view-with-roles-order-status-group");
        List<? extends CommonMatchingRule> dbMatchingRules = query.getResultList();
        initializeDbMatchingRules(dbMatchingRules);
        List<? extends CommonMatchingRule> programmaticMatchingRules = getCustomMatchingRules();
        result.addAll(dbMatchingRules);
        result.addAll(programmaticMatchingRules);
        result.sort(Comparator.comparing(mr -> mr.getOrder().getOrder()));
        return result;
    }

    @Transactional(readOnly = true)
    public int getMatchingRulesCount() {
        Query query = persistence.getEntityManager().createQuery("select count(mr.id) from ldap$AbstractDbStoredMatchingRule mr");
        int dbRulesCount = (int) query.getSingleResult();
        int programmaticRulesCount = getCustomMatchingRules().size();
        return dbRulesCount + programmaticRulesCount;
    }

    @Transactional(readOnly = true)
    public List<AbstractCommonMatchingRule> getMatchingRulesGui() {
        List<AbstractCommonMatchingRule> result = getMatchingRules().stream()
                .map(mr -> CUSTOM == mr.getRuleType()
                        ? mapCustomRuleToDto((CustomLdapMatchingRuleWrapper) mr) : (AbstractCommonMatchingRule) mr)
                .collect(Collectors.toList());
        int i = result.stream()
                      .filter(mr -> DEFAULT != mr.getRuleType())
                      .max(Comparator.comparing(mr -> mr.getOrder().getOrder())).map(mr -> mr.getOrder().getOrder()).orElse(DEFAULT_RULE_ORDER) + 1;
        for (AbstractCommonMatchingRule acmr : result) {
            if (DEFAULT_RULE_ORDER.equals(acmr.getOrder().getOrder())) {
                acmr.getOrder().setOrder(i);
                i++;
            }
        }
        result.sort(Comparator.comparing(mr -> mr.getOrder().getOrder()));

        return result;
    }

    @Transactional
    public void saveMatchingRules(List<AbstractCommonMatchingRule> matchingRules, List<AbstractCommonMatchingRule> matchingRulesToDelete) {

        List<CommonMatchingRule> defaultRules = matchingRules.stream()
                .filter(mr -> DEFAULT == mr.getRuleType())
                .collect(Collectors.toList());
        if (defaultRules.size() != 1) {
            throw new RuntimeException(messages.formatMessage(MatchingRuleDao.class, "onlySingleDefaultRule"));
        }
        EntityManager entityManager = persistence.getEntityManager();
        for (CommonMatchingRule mr : matchingRules) {
            if (CUSTOM == mr.getRuleType()) {
                matchingRuleOrderDao.saveMatchingRuleOrder(mr.getOrder());
                matchingRuleStatusDao.saveMatchingRuleStatus(mr.getStatus());
            } else {
                daoHelper.persistOrMerge((AbstractDbStoredMatchingRule) mr);
            }
        }

        matchingRulesToDelete.forEach(entityManager::remove);
    }

    private void initializeDbMatchingRules(List<? extends CommonMatchingRule> rules) {
        for (CommonMatchingRule rule : rules) {
            if (MatchingRuleType.SIMPLE == rule.getRuleType()) {
                SimpleMatchingRule simpleMatchingRule = (SimpleMatchingRule) rule;
                simpleMatchingRule.getConditions().forEach(SimpleRuleCondition::getSimpleMatchingRule);
            }
        }
    }


}
