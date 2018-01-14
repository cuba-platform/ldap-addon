package com.company.ldap.service;

public interface UserService {
    String NAME = "ldap_LdapUserService";

    void find(String filter);

}
