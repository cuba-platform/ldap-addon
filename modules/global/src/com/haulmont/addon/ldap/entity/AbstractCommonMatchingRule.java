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

    @Column(name = "RULE_TYPE")
    @Enumerated(EnumType.STRING)
    private MatchingRuleType ruleType;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "MATCHING_RULE_STATUS_ID")
    @NotNull
    private MatchingRuleStatus status = new MatchingRuleStatus();

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "MATCHING_RULE_ORDER_ID")
    @NotNull
    private MatchingRuleOrder order = new MatchingRuleOrder();

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
        return ruleType;
    }

    public void setRuleType(MatchingRuleType ruleType) {
        this.ruleType = ruleType;
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
