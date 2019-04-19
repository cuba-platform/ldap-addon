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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Default matching rule.<br>
 * This rule is a predefined one. It is used if none of other rules were applied. <br>
 * That is why it contains the 'LAST' value in the *Order* field. The rule cannot be removed or deactivated.
 */
@DiscriminatorValue("DEFAULT")
@Entity(name = "ldap$DefaultMatchingRule")
public class DefaultMatchingRule extends AbstractDbStoredMatchingRule {
    private static final long serialVersionUID = 3273589414637071323L;

    public DefaultMatchingRule() {
        super();
        setRuleType(MatchingRuleType.DEFAULT);
    }
}