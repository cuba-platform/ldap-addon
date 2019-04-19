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
import com.haulmont.addon.ldap.core.rule.custom.CustomLdapMatchingRule;
import com.haulmont.addon.ldap.entity.CommonMatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRuleType;
import org.springframework.stereotype.Component;

import static com.haulmont.addon.ldap.core.rule.appliers.CustomMatchingRuleProcessor.NAME;


@Component(NAME)
public class CustomMatchingRuleProcessor extends MatchingRuleProcessor {

    public static final String NAME = "ldap_CustomMatchingRuleProcessor";

    public CustomMatchingRuleProcessor() {
        super(MatchingRuleType.CUSTOM);
    }

    @Override
    public boolean applyMatchingRule(CommonMatchingRule matchingRule, LdapMatchingRuleContext ldapMatchingRuleContext) {
        CustomLdapMatchingRule programmaticMatchingRule = (CustomLdapMatchingRule) matchingRule;
        boolean isCustomRuleApplied = programmaticMatchingRule.applyCustomMatchingRule(ldapMatchingRuleContext);
        if (isCustomRuleApplied) {
            ldapMatchingRuleContext.getAppliedRules().add(matchingRule);
        }
        return isCustomRuleApplied;
    }
}
