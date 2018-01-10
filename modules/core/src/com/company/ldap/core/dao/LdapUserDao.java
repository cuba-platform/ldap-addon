package com.company.ldap.core.dao;

import com.company.ldap.config.LdapConfig;
import com.company.ldap.core.dto.LdapUser;
import com.company.ldap.core.dto.LdapUserWrapper;
import com.company.ldap.core.utils.LdapUserWrapperMapper;
import com.company.ldap.core.utils.LdapUserMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.query.SearchScope;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service("ldap_LdapUserDao")
public class LdapUserDao {

    @Inject
    @Qualifier("ldap_ldapTemplate")
    private LdapTemplate ldapTemplate;

    @Inject
    private LdapConfig ldapConfig;

    public List<LdapUserWrapper> getLdapUserWrappers(String login) {
        EqualsFilter ef = new EqualsFilter(ldapConfig.getLoginAttribute(), login);
        return ldapTemplate.search(ldapConfig.getUserBase(), ef.encode(), new LdapUserWrapperMapper(ldapConfig));
    }

    public LdapUser authenticate(String login, String password) {

        try {
            LdapQuery query = LdapQueryBuilder.query()
                    .searchScope(SearchScope.SUBTREE)
                    .countLimit(1)
                    .base(ldapConfig.getUserBase())
                    .where("objectClass").is(ldapConfig.getUserBase())
                    .and(ldapConfig.getLoginAttribute()).is(login);


            return ldapTemplate.authenticate(query, password, new LdapUserMapper(ldapConfig));
        } catch (Exception e) {
            return null;
        }
    }
}

