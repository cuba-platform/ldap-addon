package com.haulmont.addon.ldap.dto;


import java.io.Serializable;

public class LdapContextDto implements Serializable {

    private String contextSourceUrl;
    private String contextSourceUserName;
    private String contextSourceBase;

    public LdapContextDto(String contextSourceUrl, String contextSourceUserName, String contextSourceBase) {
        this.contextSourceUrl = contextSourceUrl;
        this.contextSourceUserName = contextSourceUserName;
        this.contextSourceBase = contextSourceBase;
    }

    public String getContextSourceBase() {
        return contextSourceBase;
    }

    public String getContextSourceUserName() {
        return contextSourceUserName;
    }

    public String getContextSourceUrl() {
        return contextSourceUrl;
    }
}
