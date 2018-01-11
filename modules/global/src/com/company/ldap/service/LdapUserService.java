package com.company.ldap.service;

public interface LdapUserService {
    String NAME = "ldap_LdapUserService";

    void find(String filter);

}
