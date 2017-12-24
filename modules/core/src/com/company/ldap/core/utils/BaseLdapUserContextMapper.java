package com.company.ldap.core.utils;

import com.company.ldap.core.api.LdapConfig;
import com.company.ldap.core.dto.LdapUser;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
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

        ldapUser.setEmail(context.getStringAttribute(ldapConfig.getEmail()));
        ldapUser.setLogin(context.getStringAttribute(ldapConfig.getLogin()));
        ldapUser.setName(context.getStringAttribute(ldapConfig.getCn()));
        ldapUser.setLastName(context.getStringAttribute(ldapConfig.getSn()));
        ldapUser.setMemberOf(Arrays.asList(context.getStringAttributes(ldapConfig.getMemberOf())));
        return ldapUser;
    }


}
