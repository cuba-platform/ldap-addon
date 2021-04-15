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

package com.haulmont.addon.ldap.service;

import com.haulmont.addon.ldap.entity.AbstractCommonMatchingRule;
import com.haulmont.addon.ldap.entity.AbstractDbStoredMatchingRule;
import com.haulmont.cuba.security.entity.Group;

import java.util.List;

public interface MatchingRuleService {
	String NAME = "ldap_MatchingRuleService";

	/**
	 * Returns the amount of existing matching rules.
	 */
	int getMatchingRulesCount();

	/**
	 * Returns the GUI representation of matching rules.
	 */
	List<AbstractCommonMatchingRule> getMatchingRulesGui();

	/**
	 * Persists the state of matching rules from LDAP Matching Rule Screen.
	 */
	void saveMatchingRules(List<AbstractCommonMatchingRule> matchingRules, List<AbstractCommonMatchingRule> matchingRulesToDelete);

	/**
	 * Returns access group for matching rule. If group is db stored just returns that group.
	 * But if group is predefined creating representation for it and returns that.
	 */
	Group getAccessGroupForMatchingRule(AbstractDbStoredMatchingRule rule);
}
