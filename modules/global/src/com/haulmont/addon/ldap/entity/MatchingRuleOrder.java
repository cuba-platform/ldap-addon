package com.haulmont.addon.ldap.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.validation.constraints.NotNull;

/**
 * Matching rule order.<br>
 * Rules with the smallest order are applied first. Starts with 1.
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