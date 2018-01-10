package com.company.ldap.core.rule;

import com.company.ldap.core.dto.LdapUser;

import javax.naming.directory.Attributes;

public class ApplyMatchingRuleContext {

    private final LdapUser ldapUser;
    private final Attributes ldapUserAttributes;

    public ApplyMatchingRuleContext(LdapUser ldapUser, Attributes ldapUserAttributes) {
        this.ldapUser = ldapUser;
        this.ldapUserAttributes = ldapUserAttributes;
    }

    public LdapUser getLdapUser() {
        return ldapUser;
    }

    public Attributes getLdapUserAttributes() {
        return ldapUserAttributes;
    }

}
