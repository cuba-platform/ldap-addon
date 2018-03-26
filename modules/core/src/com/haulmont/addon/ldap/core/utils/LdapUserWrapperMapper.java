package com.haulmont.addon.ldap.core.utils;

import com.haulmont.addon.ldap.core.dto.LdapUser;
import com.haulmont.addon.ldap.core.dto.LdapUserWrapper;
import com.haulmont.addon.ldap.entity.LdapConfig;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;

import javax.naming.directory.Attributes;

public class LdapUserWrapperMapper implements ContextMapper<LdapUserWrapper> {

    private final LdapConfig ldapConfig;

    public LdapUserWrapperMapper(LdapConfig ldapConfig) {
        this.ldapConfig = ldapConfig;
    }

    @Override
    public LdapUserWrapper mapFromContext(Object ctx) {
        DirContextAdapter context = (DirContextAdapter) ctx;
        LdapUser ldapUser = LdapHelper.mapLdapUser(context, ldapConfig);
        Attributes attributes = context.getAttributes();
        return new LdapUserWrapper(ldapUser, attributes);
    }


}
