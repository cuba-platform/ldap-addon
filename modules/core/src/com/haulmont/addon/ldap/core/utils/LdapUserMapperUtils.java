package com.haulmont.addon.ldap.core.utils;

import com.haulmont.addon.ldap.config.LdapConfig;
import com.haulmont.addon.ldap.core.dto.LdapUser;
import org.springframework.ldap.core.DirContextAdapter;

import java.util.Arrays;


public class LdapUserMapperUtils {

    public static LdapUser mapLdapUser(DirContextAdapter context, LdapConfig ldapConfig) {
        LdapUser ldapUser = new LdapUser();
        ldapUser.setLogin(context.getStringAttribute(ldapConfig.getLoginAttribute()));
        ldapUser.setCn(context.getStringAttribute(ldapConfig.getCnAttribute()));
        ldapUser.setSn(context.getStringAttribute(ldapConfig.getSnAttribute()));
        ldapUser.setEmail(context.getStringAttribute(ldapConfig.getEmailAttribute()));
        ldapUser.setMemberOf(Arrays.asList(context.getStringAttributes(ldapConfig.getMemberOfAttribute())));
        ldapUser.setAccessGroups(Arrays.asList(context.getStringAttributes(ldapConfig.getAccessGroupAttribute())));
        ldapUser.setDisabled(mapDisabledProperty(context.getObjectAttribute(ldapConfig.getInactiveUserAttribute())));
        return ldapUser;
    }

    private static Boolean mapDisabledProperty(Object disabled) {
        if (disabled == null) {
            return Boolean.FALSE;
        }
        if (disabled instanceof Boolean) {
            return (Boolean) disabled;
        }
        if (disabled instanceof String) {
            String stringDisabled = (String) disabled;
            if ("0".equalsIgnoreCase(stringDisabled) || "false".equalsIgnoreCase(stringDisabled)) {
                return Boolean.FALSE;
            }
            if ("1".equalsIgnoreCase(stringDisabled) || "true".equalsIgnoreCase(stringDisabled)) {
                return Boolean.TRUE;
            }
        }
        throw new RuntimeException("Can't map idDisabled attribute from ldap. isDisabled class: " + disabled.getClass().getName());

    }


}
