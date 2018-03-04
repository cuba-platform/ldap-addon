package com.haulmont.addon.ldap.entity;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

@DiscriminatorValue("DEFAULT")
@Entity(name = "ldap$DefaultMatchingRule")
public class DefaultMatchingRule extends AbstractDbStoredMatchingRule {
    private static final long serialVersionUID = 3273589414637071323L;

    public DefaultMatchingRule() {
        super();
        setRuleType(MatchingRuleType.DEFAULT);
    }
}