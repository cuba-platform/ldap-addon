package com.haulmont.addon.ldap.entity;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;

@MappedSuperclass
public abstract class AbstractCommonMatchingRule extends StandardEntity implements CommonMatchingRule {
    private static final long serialVersionUID = 1956446424046023194L;

    @Column(name = "RULE_TYPE")
    @Enumerated(EnumType.STRING)
    private MatchingRuleType ruleType;


    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "MATCHING_RULE_ORDER_ID")
    protected MatchingRuleOrder order = new MatchingRuleOrder();

    @Column(name = "DESCRIPTION", length = 1500)
    private String description;

    @Column(name = "IS_DISABLED")
    private Boolean isDisabled = false;

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

    public Boolean getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(Boolean disabled) {
        isDisabled = disabled;
    }

    @Override
    public String getMatchingRuleId() {
        return getId() == null ? null : getId().toString();
    }
}
