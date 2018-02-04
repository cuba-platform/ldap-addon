package com.haulmont.addon.ldap.core.dto;

import javax.naming.directory.Attributes;

public class LdapUserWrapper {

    private final LdapUser ldapUser;
    private final Attributes ldapUserAttributes;

    public LdapUserWrapper(LdapUser ldapUser, Attributes ldapUserAttributes) {
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
