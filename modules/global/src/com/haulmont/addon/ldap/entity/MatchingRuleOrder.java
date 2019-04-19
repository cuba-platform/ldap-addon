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

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * An order for applying matching rules.<br>
 * The smaller the value is, the earlier a paticular rule is applied. Starts with 1.
 */
@Table(name = "LDAP_MATCHING_RULE_ORDER")
@Entity(name = "ldap$MatchingRuleOrder")
public class MatchingRuleOrder extends StandardEntity {
    private static final long serialVersionUID = -8911284257534006739L;

    //TODO:create unique deffered constraint
    @NotNull
    @Column(name = "ORDER_", nullable = false)
    private Integer order = 0;

    @Column(name = "CUSTOM_MATCHING_RULE_ID", unique = true)
    private String customMatchingRuleId;

    public void setCustomMatchingRuleId(String customMatchingRuleId) {
        this.customMatchingRuleId = customMatchingRuleId;
    }

    public String getCustomMatchingRuleId() {
        return customMatchingRuleId;
    }


    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }


}