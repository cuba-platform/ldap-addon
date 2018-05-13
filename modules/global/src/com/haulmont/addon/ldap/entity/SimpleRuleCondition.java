package com.haulmont.addon.ldap.entity;

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;

/**
 * Simple matching rule condition.<br>
 * Contains LDAP attribute name and attribute's value. Supports only equal relationship between attribute and value.
 */
@Table(name = "LDAP_SIMPLE_RULE_CONDITION")
@Entity(name = "ldap$SimpleRuleCondition")
public class SimpleRuleCondition extends StandardEntity {
    private static final long serialVersionUID = 1824052550142895356L;

    @Column(name = "ATTRIBUTE")
    private String attribute;

    @Lob
    @Column(name = "ATTRIBUTE_VALUE")
    private String attributeValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SIMPLE_MATCHING_RULE_ID")
    protected SimpleMatchingRule simpleMatchingRule;

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }


    public void setSimpleMatchingRule(SimpleMatchingRule simpleMatchingRule) {
        this.simpleMatchingRule = simpleMatchingRule;
    }

    public SimpleMatchingRule getSimpleMatchingRule() {
        return simpleMatchingRule;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public String getAttributePair() {
        return attribute + ":" + attributeValue;
    }
}