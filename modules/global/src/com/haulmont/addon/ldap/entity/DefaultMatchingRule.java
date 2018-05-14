package com.haulmont.addon.ldap.entity;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

/**
 * Default matching rule.<br>
 * This rule is a predefined one. It is used if none of other rules were applied. <br>
 * That is why it contains the 'LAST' value in the *Order* field. The rule cannot be removed or deactivated.
 */
@DiscriminatorValue("DEFAULT")
@Entity(name = "ldap$DefaultMatchingRule")
public class DefaultMatchingRule extends AbstractDbStoredMatchingRule {
    private static final long serialVersionUID = 3273589414637071323L;

    public DefaultMatchingRule() {
        super();
        setRuleType(MatchingRuleType.DEFAULT);
    }
}