package com.haulmont.addon.ldap.core.spring;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ldap.core.support.LdapContextSource;

import javax.naming.directory.DirContext;

public class AnonymousLdapContextSource extends LdapContextSource {

    @Override
    public DirContext getContext(String principal, String credentials) {
        return super.getContext(StringUtils.EMPTY, StringUtils.EMPTY);
    }
}
