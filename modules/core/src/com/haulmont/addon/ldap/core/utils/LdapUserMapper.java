package com.haulmont.addon.ldap.core.utils;

import com.haulmont.addon.ldap.config.LdapContextConfig;
import com.haulmont.addon.ldap.core.dto.LdapUser;
import com.haulmont.addon.ldap.entity.LdapConfig;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;

import javax.naming.NamingException;

public class LdapUserMapper implements ContextMapper<LdapUser> {

    private final LdapConfig ldapConfig;

    public LdapUserMapper(LdapConfig ldapConfig) {
        this.ldapConfig = ldapConfig;
    }

    @Override
    public LdapUser mapFromContext(Object ctx) throws NamingException {
        DirContextAdapter context = (DirContextAdapter) ctx;
        return LdapHelper.mapLdapUser(context, ldapConfig);
    }
}
