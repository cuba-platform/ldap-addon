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

package com.haulmont.addon.ldap.core.rule.appliers;

import com.haulmont.addon.ldap.core.dao.LdapUserDao;
import com.haulmont.addon.ldap.core.rule.LdapMatchingRuleContext;
import com.haulmont.addon.ldap.dto.LdapUser;
import com.haulmont.addon.ldap.entity.AbstractDbStoredMatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRuleType;
import com.haulmont.addon.ldap.entity.SimpleMatchingRule;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static com.haulmont.addon.ldap.core.rule.appliers.SimpleMatchingRuleProcessor.NAME;

@Component(NAME)
public class SimpleMatchingRuleProcessor extends DbStoredMatchingRuleProcessor {

    public static final String NAME = "ldap_SimpleMatchingRuleProcessor";

    @Inject
    private LdapUserDao ldapUserDao;

    public SimpleMatchingRuleProcessor() {
        super(MatchingRuleType.SIMPLE);
    }

    @Override
    public boolean checkMatchingRule(AbstractDbStoredMatchingRule matchingRule, LdapMatchingRuleContext ldapMatchingRuleContext) {
        SimpleMatchingRule simpleMatchingRule = (SimpleMatchingRule) matchingRule;
        String tenantId = matchingRule.getLdapConfig().getSysTenantId();
        LdapUser ldapUser = ldapUserDao.findLdapUserByFilter(simpleMatchingRule.getConditions(), ldapMatchingRuleContext.getLdapUser().getLogin(), tenantId);
        return ldapUser != null;
    }
}
