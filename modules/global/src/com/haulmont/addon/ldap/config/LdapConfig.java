package com.haulmont.addon.ldap.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.DefaultBoolean;
import com.haulmont.cuba.core.config.defaults.DefaultString;

/**
 * Created by amarin on 12/4/2017.
 */
public interface LdapConfig extends Config {

    @Source(type = SourceType.DATABASE)
    @Property("ldap.loginAttribute")
    @DefaultString("sAMAccountName")
    String getLoginAttribute();

    @Source(type = SourceType.DATABASE)
    @Property("ldap.emailAttribute")
    @DefaultString("email")
    String getEmailAttribute();

    @Source(type = SourceType.DATABASE)
    @Property("ldap.cnAttribute")
    @DefaultString("cn")
    String getCnAttribute();

    @Source(type = SourceType.DATABASE)
    @Property("ldap.snAttribute")
    @DefaultString("sn")
    String getSnAttribute();

    @Source(type = SourceType.DATABASE)
    @Property("ldap.memberOfAttribute")
    @DefaultString("memberOf")
    String getMemberOfAttribute();

    @Source(type = SourceType.DATABASE)
    @Property("ldap.accessGroupAttribute")
    @DefaultString("uid")
    String getAccessGroupAttribute();

    @Source(type = SourceType.DATABASE)
    @Property("ldap.positionAttribute")
    @DefaultString("employeeType")
    String getPositionAttribute();

    @Source(type = SourceType.DATABASE)
    @Property("ldap.ouAttribute")
    @DefaultString("ou")
    String getOuAttribute();

    @Source(type = SourceType.DATABASE)
    @Property("ldap.languageAttribute")
    @DefaultString("preferredLanguage")
    String getLanguageAttribute();

    @Source(type = SourceType.DATABASE)
    @Property("ldap.inactiveUserAttribute")
    @DefaultString("accountExpires")
    String getInactiveUserAttribute();

    @Source(type = SourceType.DATABASE)
    @Property("ldap.userPasswordAttribute")
    @DefaultString("userPassword")
    String getUserPasswordAttribute();

    @Source(type = SourceType.DATABASE)
    @Property("ldap.userBase")
    @DefaultString("ou=people")
    String getUserBase();

    @Source(type = SourceType.APP)
    @Property("ldap.useContextSourcePooling")
    @DefaultBoolean(false)
    boolean getUseContextSourcePooling();

    @Source(type = SourceType.APP)
    @Property("ldap.contextSourceUrl")
    @DefaultString("ldap://localhost:10389")
    String getContextSourceUrl();

    @Source(type = SourceType.APP)
    @Property("ldap.contextSourceBase")
    @DefaultString("dc=springframework,dc=org")
    String getContextSourceBase();

    @Source(type = SourceType.APP)
    @Property("ldap.contextSourceUserName")
    @DefaultString("uid=admin,ou=system")
    String getContextSourceUserName();

    @Source(type = SourceType.APP)
    @Property("ldap.contextSourcePassword")
    @DefaultString("secret")
    String getContextSourcePassword();

    @Source(type = SourceType.DATABASE)
    @Property("ldap.schemaBase")
    @DefaultString("ou=schema")
    String getSchemaBase();

    @Source(type = SourceType.DATABASE)
    @Property("ldap.ldapUserObjectClasses")
    @DefaultString("person;inetOrgPerson")
    String getLdapUserObjectClasses();

    @Source(type = SourceType.DATABASE)
    @Property("ldap.metaObjectClassName")
    @DefaultString("metaObjectClass")
    String getMetaObjectClassName();

    @Source(type = SourceType.DATABASE)
    @Property("ldap.objectClassPropertyName")
    @DefaultString("m-name")
    String getObjectClassPropertyName();

    @Source(type = SourceType.DATABASE)
    @Property("ldap.attributePropertyName")
    @DefaultString("m-must;m-may")
    String getAttributePropertyName();

    void setContextSourceUrl(String contextSourceUrl);

    void setContextSourceBase(String contextSourceBase);

    void setContextSourceUserName(String contextSourceUserName);

    void setContextSourcePassword(String contextSourcePassword);

    void setLoginAttribute(String login);

    void setEmailAttribute(String email);

    void setCnAttribute(String cn);

    void setSnAttribute(String sn);

    void setMemberOfAttribute(String memberOf);

    void setAccessGroupAttribute(String accessGroupAttribute);

    void setPositionAttribute(String positionAttribute);

    void setOuAttribute(String ouAttribute);

    void setLanguageAttribute(String languageAttribute);

    void setInactiveUserAttribute(String inactiveUserAttribute);

    void setUserPasswordAttribute(String userPasswordAttribute);

    void setUserBase(String userBase);

    void setUseContextSourcePooling(boolean useContextSourcePooling);

    void setSchemaBase(String schemaBase);

    void setLdapUserObjectClasses(String ldapUserObjectClasses);

    void setMetaObjectClassName(String metaObjectClassName);

    void setObjectClassPropertyName(String objectClassPropertyName);

    void setAttributePropertyName(String schemaBase);
}
