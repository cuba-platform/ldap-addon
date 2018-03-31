package com.haulmont.addon.ldap.core.spring.events;

import com.haulmont.addon.ldap.core.rule.LdapMatchingRuleContext;
import com.haulmont.cuba.security.entity.User;
import org.springframework.context.ApplicationEvent;

public class BeforeNewUserCreatedFromLdapEvent extends ApplicationEvent {

    private final LdapMatchingRuleContext ldapMatchingRuleContext;
    private final User cubaUser;

    public BeforeNewUserCreatedFromLdapEvent(Object source, LdapMatchingRuleContext ldapMatchingRuleContext, User cubaUser) {
        super(source);
        this.ldapMatchingRuleContext = ldapMatchingRuleContext;
        this.cubaUser = cubaUser;
    }
}
