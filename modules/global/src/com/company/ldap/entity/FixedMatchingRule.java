package com.company.ldap.entity;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

@DiscriminatorValue("FIXED")
@Entity(name = "ldap$FixedMatchingRule")
public class FixedMatchingRule extends AbstractMatchingRule {
    private static final long serialVersionUID = 3273589414637071323L;

    public FixedMatchingRule() {
        super();
        setRuleType(MatchingRuleType.FIXED);
    }
}