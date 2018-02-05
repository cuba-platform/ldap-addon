package com.haulmont.addon.ldap.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.Lob;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import javax.persistence.OneToMany;

@DiscriminatorValue("SIMPLE")
@Entity(name = "ldap$SimpleMatchingRule")
public class SimpleMatchingRule extends AbstractMatchingRule {
    private static final long serialVersionUID = -2383286286785487816L;


    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "simpleMatchingRule")
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