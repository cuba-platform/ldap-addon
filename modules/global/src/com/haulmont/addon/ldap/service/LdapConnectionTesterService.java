package com.haulmont.addon.ldap.service;

public interface LdapConnectionTesterService {

    String NAME = "ldap_LdapConnectionTesterService";

    String testConnection(String url, String base, String userDn, String password);
}
