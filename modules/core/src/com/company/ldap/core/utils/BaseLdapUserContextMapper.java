package com.company.ldap.core.utils;

import com.company.ldap.config.LdapConfig;
import com.company.ldap.core.dto.LdapUser;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;

import javax.naming.NamingException;
import java.util.Arrays;

public class BaseLdapUserContextMapper implements ContextMapper<LdapUser> {

    private final LdapConfig ldapConfig;

    public BaseLdapUserContextMapper(LdapConfig ldapConfig) {
        this.ldapConfig = ldapConfig;
    }

    @Override
    public LdapUser mapFromContext(Object ctx) throws NamingException {
        LdapUser ldapUser = new LdapUser();
        DirContextAdapter context = (DirContextAdapter)ctx;

        ldapUser.setEmail(context.getStringAttribute(ldapConfig.getEmailAttribute()));
        ldapUser.setLogin(context.getStringAttribute(ldapConfig.getLoginAttribute()));
        ldapUser.setName(context.getStringAttribute(ldapConfig.getCnAttribute()));
        ldapUser.setLastName(context.getStringAttribute(ldapConfig.getSnAttribute()));
        ldapUser.setMemberOf(Arrays.asList(context.getStringAttributes(ldapConfig.getMemberOfAttribute())));
        return ldapUser;
    }


}
