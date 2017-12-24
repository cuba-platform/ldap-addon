package com.company.ldap.core.utils;

import com.company.ldap.core.api.LdapConfig;
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

        ldapUser.setEmail(context.getStringAttribute(ldapConfig.getEmail()));
        ldapUser.setLogin(context.getStringAttribute(ldapConfig.getLogin()));
        ldapUser.setName(context.getStringAttribute(ldapConfig.getCn()));
        ldapUser.setLastName(context.getStringAttribute(ldapConfig.getSn()));
        ldapUser.setMemberOf(Arrays.asList(context.getStringAttributes(ldapConfig.getMemberOf())));
        return ldapUser;
    }
}
