package com.company.ldap.core.utils;

import com.company.ldap.config.LdapConfig;
import com.company.ldap.core.dto.LdapUser;
import org.springframework.ldap.core.AuthenticatedLdapEntryContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapEntryIdentification;

import javax.naming.directory.DirContext;
import java.util.Arrays;

public class LdapAuthentificationMapper implements AuthenticatedLdapEntryContextMapper<LdapUser> {

    private final LdapConfig ldapConfig;

    public LdapAuthentificationMapper(LdapConfig ldapConfig) {
        this.ldapConfig = ldapConfig;
    }

    @Override
    public LdapUser mapWithContext(DirContext ctx, LdapEntryIdentification ldapEntryIdentification) {
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
