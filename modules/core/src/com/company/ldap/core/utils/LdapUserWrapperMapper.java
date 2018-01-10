package com.company.ldap.core.utils;

import com.company.ldap.config.LdapConfig;
import com.company.ldap.core.dto.LdapUser;
import com.company.ldap.core.dto.LdapUserWrapper;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;

import javax.naming.directory.Attributes;
import java.util.Arrays;

public class LdapUserWrapperMapper implements ContextMapper<LdapUserWrapper> {

    private final LdapConfig ldapConfig;

    public LdapUserWrapperMapper(LdapConfig ldapConfig) {
        this.ldapConfig = ldapConfig;
    }

    @Override
    public LdapUserWrapper mapFromContext(Object ctx) {
        LdapUser ldapUser = new LdapUser();
        DirContextAdapter context = (DirContextAdapter) ctx;

        ldapUser.setLogin(context.getStringAttribute(ldapConfig.getLoginAttribute()));
        ldapUser.setCn(context.getStringAttribute(ldapConfig.getCnAttribute()));
        ldapUser.setSn(context.getStringAttribute(ldapConfig.getSnAttribute()));
        ldapUser.setEmail(context.getStringAttribute(ldapConfig.getEmailAttribute()));
        ldapUser.setMemberOf(Arrays.asList(context.getStringAttributes(ldapConfig.getMemberOfAttribute())));
        ldapUser.setAccessGroups(Arrays.asList(context.getStringAttributes(ldapConfig.getAccessGroupAttribute())));

        Attributes attributes = context.getAttributes();

        return new LdapUserWrapper(ldapUser, attributes);
    }


}
