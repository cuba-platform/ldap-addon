package com.company.ldap.core.utils;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.company.ldap.core.utils.LdapUserValidator.NAME;

@Component(NAME)
public class LdapUserValidator {
    public static final String NAME = "ldap_LdapUserValidator";

    public <T> T validateLdapUserResult(String login, List<T> list) {
        if (list != null && list.size() > 1) {
            throw new RuntimeException("Invalid count in found by login ldap query. Login: " + login + ". Count " + list.size());
        }

        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        return list.get(0);
    }
}
