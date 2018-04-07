package com.haulmont.addon.ldap.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

@Table(name = "LDAP_MATCHING_RULE_STATUS")
@Entity(name = "ldap$MatchingRuleStatus")
public class MatchingRuleStatus extends StandardEntity {
    private static final long serialVersionUID = 1395034729351281363L;

    @Column(name = "CUSTOM_MATCHING_RULE_ID", unique = true)
    private String customMatchingRuleId;


    @NotNull
    @Column(name = "IS_ACTIVE", nullable = false)
    private Boolean isActive = true;

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsActive() {
        return isActive;
    }


    public void setCustomMatchingRuleId(String customMatchingRuleId) {
        this.customMatchingRuleId = customMatchingRuleId;
    }

    public String getCustomMatchingRuleId() {
        return customMatchingRuleId;
    }


}