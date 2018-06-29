package com.haulmont.addon.ldap.core.spring.events;

import com.haulmont.addon.ldap.core.rule.LdapMatchingRuleContext;
import com.haulmont.cuba.security.entity.User;
import org.springframework.context.ApplicationEvent;

public class UserActivatedFromLdapEvent extends ApplicationEvent {

    private final LdapMatchingRuleContext ldapMatchingRuleContext;
    private final User cubaUser;
    private final SynchronizationMode synchronizationMode;

    public UserActivatedFromLdapEvent(Object source, LdapMatchingRuleContext ldapMatchingRuleContext, User cubaUser,
                                      SynchronizationMode synchronizationMode) {
        super(source);
        this.ldapMatchingRuleContext = ldapMatchingRuleContext;
        this.cubaUser = cubaUser;
        this.synchronizationMode = synchronizationMode;
    }

    public LdapMatchingRuleContext getLdapMatchingRuleContext() {
        return ldapMatchingRuleContext;
    }

    public User getCubaUser() {
        return cubaUser;
    }

    public SynchronizationMode getSynchronizationMode() {
        return synchronizationMode;
    }
}
