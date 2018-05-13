package com.haulmont.addon.ldap.entity;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

/**
 * Default matching rule.<br>
 * Predefined rule. Applies only if other rules don't apply. Always applies last. Can't be removed or deactivated.
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