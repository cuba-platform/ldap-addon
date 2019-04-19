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

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple matching rule.<br>
 * Rules of this type are applied if all provided conditions are met.
 */
@DiscriminatorValue("SIMPLE")
@Entity(name = "ldap$SimpleMatchingRule")
public class SimpleMatchingRule extends AbstractDbStoredMatchingRule {
    private static final long serialVersionUID = -2383286286785487816L;


    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "simpleMatchingRule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SimpleRuleCondition> conditions = new ArrayList<>();

    public SimpleMatchingRule() {
        super();
        setRuleType(MatchingRuleType.SIMPLE);
    }

    public void setConditions(List<SimpleRuleCondition> conditions) {
        this.conditions = conditions;
    }

    public List<SimpleRuleCondition> getConditions() {
        return conditions;
    }

}