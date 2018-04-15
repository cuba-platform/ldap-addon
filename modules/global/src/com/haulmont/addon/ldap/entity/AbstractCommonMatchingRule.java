package com.haulmont.addon.ldap.entity;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public abstract class AbstractCommonMatchingRule extends StandardEntity implements CommonMatchingRule {
    private static final long serialVersionUID = 1956446424046023194L;

    @NotNull
    @Column(name = "RULE_TYPE", nullable = false)
    private String ruleType;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "MATCHING_RULE_STATUS_ID", nullable = false)
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @NotNull
    private MatchingRuleStatus status;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "MATCHING_RULE_ORDER_ID")
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @NotNull
    private MatchingRuleOrder order;

    @Column(name = "DESCRIPTION", length = 1500)
    private String description;


    public void setStatus(MatchingRuleStatus status) {
        this.status = status;
    }

    public MatchingRuleStatus getStatus() {
        return status;
    }


    public void setOrder(MatchingRuleOrder order) {
        this.order = order;
    }

    public MatchingRuleOrder getOrder() {
        return order;
    }


    @Override
    public MatchingRuleType getRuleType() {
        return ruleType == null ? null : MatchingRuleType.fromId(ruleType);
    }

    public void setRuleType(MatchingRuleType ruleType) {
        this.ruleType = ruleType == null ? null : ruleType.getId();
    }


    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public String getMatchingRuleId() {
        return getId() == null ? null : getId().toString();
    }
}
