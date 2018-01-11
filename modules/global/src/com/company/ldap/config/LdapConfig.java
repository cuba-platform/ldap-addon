package com.company.ldap.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.DefaultBoolean;
import com.haulmont.cuba.core.config.defaults.DefaultString;

/**
 * Created by amarin on 12/4/2017.
 */
@Source(type = SourceType.APP)
public interface LdapConfig extends Config {

    @Property("ldap.loginAttribute")
    @DefaultString("sAMAccountName")
    String getLoginAttribute();

    @Property("ldap.emailAttribute")
    @DefaultString("email")
    String getEmailAttribute();

    @Property("ldap.cnAttribute")
    @DefaultString("cn")
    String getCnAttribute();

    @Property("ldap.snAttribute")
    @DefaultString("sn")
    String getSnAttribute();

    @Property("ldap.memberOfAttribute")
    @DefaultString("memberOf")
    String getMemberOfAttribute();

    @Property("ldap.accessGroupAttribute")
    @DefaultString("")
    String getAccessGroupAttribute();

    @Property("ldap.inactiveUserAttribute")
    @DefaultString("")
    String getInactiveUserAttribute();

    @Property("ldap.userBase")
    @DefaultString("ou=people")
    String getUserBase();

    @Property("ldap.useContextSourcePooling")
    @DefaultBoolean(false)
    boolean getUseContextSourcePooling();

    @Property("ldap.contextSourceUrl")
    @DefaultString("ldap://localhost:10389")
    String getContextSourceUrl();

    @Property("ldap.contextSourceBase")
    @DefaultString("dc=springframework,dc=org")
    String getContextSourceBase();

    @Property("ldap.contextSourceUserName")
    @DefaultString("uid=admin,ou=system")
    String getContextSourceUserName();

    @Property("ldap.contextSourcePassword")
    @DefaultString("secret")
    String getContextSourcePassword();

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
    void setInactiveUserAttribute(String inactiveUserAttribute);
    void setUserBase(String userBase);

    void setUseContextSourcePooling(boolean useContextSourcePooling);
}
