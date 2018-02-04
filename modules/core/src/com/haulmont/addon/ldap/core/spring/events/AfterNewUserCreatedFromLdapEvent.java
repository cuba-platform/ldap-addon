package com.haulmont.addon.ldap.core.spring.events;

import com.haulmont.addon.ldap.core.rule.ApplyMatchingRuleContext;
import com.haulmont.cuba.security.entity.User;
import org.springframework.context.ApplicationEvent;

public class AfterNewUserCreatedFromLdapEvent extends ApplicationEvent {

    private final ApplyMatchingRuleContext applyMatchingRuleContext;
    private final User cubaUser;

    public AfterNewUserCreatedFromLdapEvent(Object source, ApplyMatchingRuleContext applyMatchingRuleContext, User cubaUser) {
        super(source);
        this.applyMatchingRuleContext = applyMatchingRuleContext;
        this.cubaUser = cubaUser;
    }
}
