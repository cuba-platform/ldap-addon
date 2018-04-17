package com.haulmont.addon.ldap.core.utils;

import com.haulmont.addon.ldap.core.dto.LdapUser;
import com.haulmont.addon.ldap.entity.LdapConfig;
import org.apache.commons.lang.StringUtils;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.OrFilter;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.*;


public class LdapHelper {

    private final static String AD_ENABLED = "512";
    private final static String AD_DISABLED = "514";
    private final static String AD_ENABLED_PASSWORD_NEVER_EXPIRE = "66048";
    private final static String AD_DISABLED_PASSWORD_NEVER_EXPIRE = "66050";

    public static LdapUser mapLdapUser(DirContextAdapter context, LdapConfig ldapConfig) {
        LdapUser ldapUser = new LdapUser(context.getAttributes());
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
            String[] memberOf = context.getStringAttributes(ldapConfig.getMemberOfAttribute());
            ldapUser.setMemberOf(memberOf == null ? null : Arrays.asList(memberOf));
        }
        if (StringUtils.isNotEmpty(ldapConfig.getAccessGroupAttribute())) {
            String[] accessGroups = context.getStringAttributes(ldapConfig.getAccessGroupAttribute());
            ldapUser.setAccessGroups(accessGroups == null ? null : Arrays.asList(accessGroups));
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
            if ("0".equalsIgnoreCase(stringDisabled) || "false".equalsIgnoreCase(stringDisabled) ||
                    AD_ENABLED.equalsIgnoreCase(stringDisabled) || AD_ENABLED_PASSWORD_NEVER_EXPIRE.equalsIgnoreCase(stringDisabled)) {
                return Boolean.FALSE;
            }
            if ("1".equalsIgnoreCase(stringDisabled) || "true".equalsIgnoreCase(stringDisabled) ||
                    AD_DISABLED.equalsIgnoreCase(stringDisabled) || AD_DISABLED_PASSWORD_NEVER_EXPIRE.equalsIgnoreCase(stringDisabled)) {
                return Boolean.TRUE;
            }
        }
        throw new RuntimeException("Can't map isDisabled attribute from ldap. Attribute value: " + disabled.toString());

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

    public static Map<String, Object> setLdapAttributesMap(Attributes ldapUserAttributes) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            NamingEnumeration<String> attrs = ldapUserAttributes.getIDs();
            while (attrs.hasMore()) {
                String attrName = attrs.next();
                NamingEnumeration values = ldapUserAttributes.get(attrName).getAll();
                List<Object> attrValues = new ArrayList<>();
                while (values.hasMore()) {
                    Object attr = values.next();
                    if (attr != null) {
                        attrValues.add(attr);
                    }
                }
                if (attrValues.size() == 1) {
                    resultMap.put(attrName, attrValues.get(0));
                } else if (attrValues.size() == 0) {
                    resultMap.put(attrName, null);
                } else {
                    resultMap.put(attrName, attrValues);
                }

            }
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
        return resultMap;
    }


}
