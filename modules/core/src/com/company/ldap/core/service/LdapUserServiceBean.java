package com.company.ldap.core.service;

import com.company.ldap.service.LdapUserService;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(LdapUserService.NAME)
public class LdapUserServiceBean implements LdapUserService {

    @Inject
    private LdapTemplate ldapTemplate;

    @Override
    public void find(String filter) {
        Object obj = ldapTemplate.lookup("uid=ben,ou=people");
        int t =4;
    }
}
