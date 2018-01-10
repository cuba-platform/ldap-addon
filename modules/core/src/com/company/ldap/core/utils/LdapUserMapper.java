package com.company.ldap.core.utils;

import com.company.ldap.config.LdapConfig;
import com.company.ldap.core.dto.LdapUser;
import org.springframework.ldap.core.AuthenticatedLdapEntryContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapEntryIdentification;

import javax.naming.directory.DirContext;
import java.util.Arrays;

public class LdapUserMapper implements AuthenticatedLdapEntryContextMapper<LdapUser> {

    private final LdapConfig ldapConfig;

    public LdapUserMapper(LdapConfig ldapConfig) {
        this.ldapConfig = ldapConfig;
    }

    @Override
    public LdapUser mapWithContext(DirContext ctx, LdapEntryIdentification ldapEntryIdentification) {
        LdapUser ldapUser = new LdapUser();
        DirContextAdapter context = (DirContextAdapter) ctx;

        ldapUser.setLogin(context.getStringAttribute(ldapConfig.getLoginAttribute()));
        ldapUser.setCn(context.getStringAttribute(ldapConfig.getCnAttribute()));
        ldapUser.setSn(context.getStringAttribute(ldapConfig.getSnAttribute()));
        ldapUser.setEmail(context.getStringAttribute(ldapConfig.getEmailAttribute()));
        ldapUser.setMemberOf(Arrays.asList(context.getStringAttributes(ldapConfig.getMemberOfAttribute())));
        ldapUser.setAccessGroups(Arrays.asList(context.getStringAttributes(ldapConfig.getAccessGroupAttribute())));
        return ldapUser;
    }
}
