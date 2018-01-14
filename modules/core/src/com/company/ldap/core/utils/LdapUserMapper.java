package com.company.ldap.core.utils;

import com.company.ldap.core.dto.LdapUser;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;

import javax.naming.NamingException;

public class LdapUserMapper implements ContextMapper<LdapUser> {

    @Override
    public LdapUser mapFromContext(Object ctx) throws NamingException {
        DirContextAdapter context = (DirContextAdapter) ctx;
        return LdapUserMapperUtils.mapLdapUser(context);
    }
}
