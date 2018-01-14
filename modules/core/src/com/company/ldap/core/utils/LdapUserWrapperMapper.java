package com.company.ldap.core.utils;

import com.company.ldap.core.dto.LdapUser;
import com.company.ldap.core.dto.LdapUserWrapper;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;

import javax.naming.directory.Attributes;

public class LdapUserWrapperMapper implements ContextMapper<LdapUserWrapper> {

    @Override
    public LdapUserWrapper mapFromContext(Object ctx) {
        DirContextAdapter context = (DirContextAdapter) ctx;
        LdapUser ldapUser = LdapUserMapperUtils.mapLdapUser(context);
        Attributes attributes = context.getAttributes();
        return new LdapUserWrapper(ldapUser, attributes);
    }


}
