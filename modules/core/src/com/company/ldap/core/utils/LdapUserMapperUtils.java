package com.company.ldap.core.utils;

import com.company.ldap.config.LdapConfig;
import com.company.ldap.core.dto.LdapUser;
import com.haulmont.cuba.core.global.AppBeans;
import org.springframework.ldap.core.DirContextAdapter;

import java.util.Arrays;


public class LdapUserMapperUtils {

    public static LdapUser mapLdapUser(DirContextAdapter context) {
        LdapUser ldapUser = new LdapUser();
        LdapConfig ldapConfig = AppBeans.get(LdapConfig.class);
        ldapUser.setLogin(context.getStringAttribute(ldapConfig.getLoginAttribute()));
        ldapUser.setCn(context.getStringAttribute(ldapConfig.getCnAttribute()));
        ldapUser.setSn(context.getStringAttribute(ldapConfig.getSnAttribute()));
        ldapUser.setEmail(context.getStringAttribute(ldapConfig.getEmailAttribute()));
        ldapUser.setMemberOf(Arrays.asList(context.getStringAttributes(ldapConfig.getMemberOfAttribute())));
        ldapUser.setAccessGroups(Arrays.asList(context.getStringAttributes(ldapConfig.getAccessGroupAttribute())));
        ldapUser.setDisabled(((Boolean) context.getObjectAttribute(ldapConfig.getInactiveUserAttribute())));
        return ldapUser;
    }


}
