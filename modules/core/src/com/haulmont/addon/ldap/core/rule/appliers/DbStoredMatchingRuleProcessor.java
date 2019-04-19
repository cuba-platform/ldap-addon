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

import com.haulmont.addon.ldap.core.rule.LdapMatchingRuleContext;
import com.haulmont.addon.ldap.entity.AbstractDbStoredMatchingRule;
import com.haulmont.addon.ldap.entity.CommonMatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRuleType;

public abstract class DbStoredMatchingRuleProcessor extends MatchingRuleProcessor {

    public DbStoredMatchingRuleProcessor(MatchingRuleType matchingRuleType) {
        super(matchingRuleType);
    }

    public abstract boolean checkMatchingRule(AbstractDbStoredMatchingRule matchingRule, LdapMatchingRuleContext ldapMatchingRuleContext);

    @Override
    boolean applyMatchingRule(CommonMatchingRule matchingRule, LdapMatchingRuleContext ldapMatchingRuleContext) {
        AbstractDbStoredMatchingRule abstractDbStoredMatchingRule = (AbstractDbStoredMatchingRule) matchingRule;
        boolean isRuleApplied = checkMatchingRule(abstractDbStoredMatchingRule, ldapMatchingRuleContext);

        if (isRuleApplied) {
            changeGroupAndRolesInMatchingRuleContext(abstractDbStoredMatchingRule, ldapMatchingRuleContext);
            ldapMatchingRuleContext.getAppliedRules().add(abstractDbStoredMatchingRule);
            ldapMatchingRuleContext.setTerminalRuleApply(abstractDbStoredMatchingRule.getIsTerminalRule());
        }
        return isRuleApplied;
    }

    private void changeGroupAndRolesInMatchingRuleContext(AbstractDbStoredMatchingRule abstractDbStoredMatchingRule,
                                                          LdapMatchingRuleContext ldapMatchingRuleContext) {
        if (abstractDbStoredMatchingRule.getIsOverrideExistingAccessGroup() || ldapMatchingRuleContext.getGroup() == null) {
            ldapMatchingRuleContext.setGroup(abstractDbStoredMatchingRule.getAccessGroup());
        }
        if (abstractDbStoredMatchingRule.getIsOverrideExistingRoles()) {
            ldapMatchingRuleContext.getRoles().clear();
        }
        ldapMatchingRuleContext.getRoles().addAll(abstractDbStoredMatchingRule.getRoles());
    }
}
