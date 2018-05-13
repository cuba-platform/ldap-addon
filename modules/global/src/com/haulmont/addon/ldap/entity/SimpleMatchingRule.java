package com.haulmont.addon.ldap.entity;

import javax.persistence.*;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

import java.util.List;

/**
 * Simple matching rule.<br>
 * Applies if all provided conditions are true.
 */
@DiscriminatorValue("SIMPLE")
@Entity(name = "ldap$SimpleMatchingRule")
public class SimpleMatchingRule extends AbstractDbStoredMatchingRule {
    private static final long serialVersionUID = -2383286286785487816L;


    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "simpleMatchingRule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SimpleRuleCondition> conditions;

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