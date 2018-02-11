package com.haulmont.addon.ldap.core.utils;

import com.haulmont.addon.ldap.config.LdapConfig;
import com.haulmont.addon.ldap.core.dto.LdapUser;
import org.apache.commons.lang.StringUtils;
import org.springframework.ldap.core.DirContextAdapter;

import java.util.Arrays;


public class LdapUserMapperUtils {

    public static LdapUser mapLdapUser(DirContextAdapter context, LdapConfig ldapConfig) {
        LdapUser ldapUser = new LdapUser();
        if (StringUtils.isNotEmpty(ldapConfig.getLoginAttribute())) {
            ldapUser.setLogin(context.getStringAttribute(ldapConfig.getLoginAttribute()));
        }
        if (StringUtils.isNotEmpty(ldapConfig.getCnAttribute())) {
            ldapUser.setCn(context.getStringAttribute(ldapConfig.getCnAttribute()));
        }
        if (StringUtils.isNotEmpty(ldapConfig.getSnAttribute())) {
            ldapUser.setSn(context.getStringAttribute(ldapConfig.getSnAttribute()));
        }
        if (StringUtils.isNotEmpty(ldapConfig.getEmailAttribute())) {
            ldapUser.setEmail(context.getStringAttribute(ldapConfig.getEmailAttribute()));
        }
        if (StringUtils.isNotEmpty(ldapConfig.getMemberOfAttribute())) {
            ldapUser.setMemberOf(Arrays.asList(context.getStringAttributes(ldapConfig.getMemberOfAttribute())));
        }
        if (StringUtils.isNotEmpty(ldapConfig.getAccessGroupAttribute())) {
            ldapUser.setAccessGroups(Arrays.asList(context.getStringAttributes(ldapConfig.getAccessGroupAttribute())));
        }
        if (StringUtils.isNotEmpty(ldapConfig.getInactiveUserAttribute())) {
            ldapUser.setDisabled(mapDisabledProperty(context.getObjectAttribute(ldapConfig.getInactiveUserAttribute())));
        }
        if (StringUtils.isNotEmpty(ldapConfig.getPositionAttribute())) {
            ldapUser.setPosition(context.getStringAttribute(ldapConfig.getPositionAttribute()));
        }
        if (StringUtils.isNotEmpty(ldapConfig.getLanguageAttribute())) {
            ldapUser.setLanguage(context.getStringAttribute(ldapConfig.getLanguageAttribute()));
        }
        if (StringUtils.isNotEmpty(ldapConfig.getOuAttribute())) {
            ldapUser.setOu(context.getStringAttribute(ldapConfig.getOuAttribute()));
        }
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
