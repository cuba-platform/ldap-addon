package com.haulmont.addon.ldap.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;

public interface LdapContextConfig extends Config {

    @Source(type = SourceType.APP)
    @Property("ldap.contextSourceUrl")
    String getContextSourceUrl();

    @Source(type = SourceType.APP)
    @Property("ldap.contextSourceBase")
    String getContextSourceBase();

    @Source(type = SourceType.APP)
    @Property("ldap.contextSourceUserName")
    String getContextSourceUserName();

    @Source(type = SourceType.APP)
    @Property("ldap.contextSourcePassword")
    String getContextSourcePassword();

    void setContextSourceUrl(String contextSourceUrl);
    void setContextSourceBase(String contextSourceBase);
    void setContextSourceUserName(String contextSourceUserName);
    void setContextSourcePassword(String contextSourcePassword);

}
