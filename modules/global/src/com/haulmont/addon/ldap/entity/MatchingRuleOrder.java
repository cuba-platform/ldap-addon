package com.haulmont.addon.ldap.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "LDAP_MATCHING_RULE_ORDER")
@Entity(name = "ldap$MatchingRuleOrder")
public class MatchingRuleOrder extends StandardEntity {
    private static final long serialVersionUID = -8911284257534006739L;

    //TODO:create unique deffered constraint
    @Column(name = "ORDER_")
    protected Integer order;

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }


}