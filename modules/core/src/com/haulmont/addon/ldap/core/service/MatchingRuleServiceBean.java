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

package com.haulmont.addon.ldap.core.service;

import com.google.common.base.Strings;
import com.haulmont.addon.ldap.core.dao.MatchingRuleDao;
import com.haulmont.addon.ldap.entity.AbstractCommonMatchingRule;
import com.haulmont.addon.ldap.entity.AbstractDbStoredMatchingRule;
import com.haulmont.addon.ldap.service.MatchingRuleService;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.group.AccessGroupsService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@Service(MatchingRuleService.NAME)
public class MatchingRuleServiceBean implements MatchingRuleService {

    @Inject
    private MatchingRuleDao matchingRuleDao;

    @Inject
    private AccessGroupsService accessGroupsService;

    @Override
    public List<AbstractCommonMatchingRule> getMatchingRules(UUID ldapConfigId) {
        return matchingRuleDao.getMatchingRulesByLdapConfig(ldapConfigId);
    }

    @Override
    public Group getAccessGroupForMatchingRule(AbstractDbStoredMatchingRule rule) {
        if (rule.getAccessGroup() == null && !Strings.isNullOrEmpty(rule.getAccessGroupName())) {
            return accessGroupsService.findPredefinedGroupByName(rule.getAccessGroupName());
        }
        return rule.getAccessGroup();
    }
}
