package com.company.ldap.core.dao;

import com.company.ldap.core.api.LdapConfig;
import com.company.ldap.core.dto.LdapUser;
import com.company.ldap.core.utils.BaseLdapUserContextMapper;
import com.company.ldap.core.utils.LdapAuthentificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.query.SearchScope;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LdapUserDao {

    @Autowired
    private LdapTemplate ldapTemplate;

    @Autowired
    private LdapConfig ldapConfig;

    public LdapUser getLdapUserByLogin(String login) {
        EqualsFilter ef = new EqualsFilter(ldapConfig.getLogin(), login);
        List<LdapUser> ldapUsers = ldapTemplate.search(ldapConfig.getUserBase(), ef.encode(), new BaseLdapUserContextMapper(ldapConfig));
        return ldapUsers.get(0);
    }

    public LdapUser authenticate(String login, String password) {

        try {
            LdapQuery query = LdapQueryBuilder.query()
                    .searchScope(SearchScope.SUBTREE)
                    .countLimit(1)
                    .base(ldapConfig.getUserBase())
                    .where("objectClass").is(ldapConfig.getUserObjectClass())
                    .and(ldapConfig.getLogin()).is(login);


            return ldapTemplate.authenticate(query, password, new LdapAuthentificationMapper(ldapConfig));
        } catch (Exception e) {
            return null;
        }
    }
}

