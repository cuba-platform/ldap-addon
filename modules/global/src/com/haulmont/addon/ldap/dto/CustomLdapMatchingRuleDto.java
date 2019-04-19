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

package com.haulmont.addon.ldap.dto;

import com.haulmont.addon.ldap.entity.AbstractCommonMatchingRule;
import com.haulmont.chile.core.annotations.MetaClass;

@MetaClass(name = "ldap$CustomLdapMatchingRuleDto")
public class CustomLdapMatchingRuleDto extends AbstractCommonMatchingRule {

    private String matchingRuleId;

    private String name;

    @Override
    public String getMatchingRuleId() {
        return matchingRuleId;
    }

    public void setMatchingRuleId(String matchingRuleId) {
        this.matchingRuleId = matchingRuleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
