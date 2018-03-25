package com.haulmont.addon.ldap.core.utils;

import com.haulmont.addon.ldap.config.LdapContextConfig;
import com.haulmont.addon.ldap.core.dto.LdapUser;
import com.haulmont.addon.ldap.entity.LdapConfig;
import org.apache.commons.lang.StringUtils;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.OrFilter;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class LdapHelper {

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

    public static String createSchemaFilter(String objectClasses, String objectClassName) {
        OrFilter orFilter = new OrFilter();
        String[] objectClassesArray = objectClasses.split(";");
        for (String objectClass : objectClassesArray) {
            Filter equalFilter = new EqualsFilter(objectClassName, objectClass);
            orFilter.or(equalFilter);
        }
        return orFilter.encode();
    }

    public static List<String> getSchemaAttributes(Attributes attributes, String[] attributesName) throws NamingException {
        List<String> attributesResult = new ArrayList<>();
        NamingEnumeration attrIds = attributes.getIDs();
        List<String> ldapObjectAttributeNames = new ArrayList<>();
        while (attrIds.hasMore()) {
            String attrName = (String) attrIds.next();
            ldapObjectAttributeNames.add(attrName);
        }
        for (String attributeName : attributesName) {
            if (!ldapObjectAttributeNames.contains(attributeName)) continue;
            NamingEnumeration values = attributes.get(attributeName).getAll();
            while (values.hasMore()) {
                Object val = values.next();
                attributesResult.add(val == null ? StringUtils.EMPTY : val.toString());
            }
        }
        return attributesResult;
    }


}
