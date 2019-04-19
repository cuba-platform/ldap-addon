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

package com.haulmont.addon.ldap.entity;


/**
 * Methods for Custom matching rules and rules stored in the DB.
 */
public interface CommonMatchingRule {

    /**
     * Returns a unique identifier of a matching rule.<br>
     * For matching rules stored in the DB, it is the value provided in the FK column.<br>
     * For Custom rules, it is a canonical class name of the Custom rule class.
     */
    String getMatchingRuleId();

    /**
     * Returns a rule type.
     */
    MatchingRuleType getRuleType();

    /**
     * Returns a rule description.
     */
    String getDescription();

    /**
     * Returns a matching rule order number.
     */
    MatchingRuleOrder getOrder();

    /**
     * Returns a matching rule status.
     */
    MatchingRuleStatus getStatus();
}
