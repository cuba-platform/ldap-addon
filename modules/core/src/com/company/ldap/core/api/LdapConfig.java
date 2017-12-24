package com.company.ldap.core.api;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.DefaultString;

/**
 * Created by amarin on 12/4/2017.
 */
@Source(type = SourceType.DATABASE)
public interface LdapConfig extends Config {

    @Property("ldap.login")
    @DefaultString("sAMAccountName")
    String getLogin();

    @Property("ldap.email")
    @DefaultString("email")
    String getEmail();

    @Property("ldap.cn")
    @DefaultString("cn")
    String getCn();

    @Property("ldap.sn")
    @DefaultString("sn")
    String getSn();

    @Property("ldap.memberOf")
    @DefaultString("memberOf")
    String getMemberOf();

    @Property("ldap.userBase")
    @DefaultString("ou=people")
    String getUserBase();

    @Property("ldap.userObjectClass")
    @DefaultString("simulatedMicrosoftSecurityPrincipal")
    String getUserObjectClass();

    void setLogin(String login);
    void setEmail(String email);
    void setCn(String cn);
    void setSn(String sn);
    void setMemberOf(String memberOf);
    void setUserBase(String userBase);
    void setUserObjectClass(String userObjectClass);
}
