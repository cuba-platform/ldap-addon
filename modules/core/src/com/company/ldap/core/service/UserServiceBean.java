package com.company.ldap.core.service;

import com.company.ldap.core.dao.CubaUserDao;
import com.company.ldap.core.dao.LdapUserDao;
import com.company.ldap.core.dao.MatchingRuleDao;
import com.company.ldap.core.dto.LdapUser;
import com.company.ldap.core.dto.LdapUserWrapper;
import com.company.ldap.core.rule.ApplyMatchingRuleContext;
import com.company.ldap.core.utils.LdapConstants;
import com.company.ldap.core.utils.LdapUserValidator;
import com.company.ldap.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service(UserService.NAME)
public class UserServiceBean implements UserService {

    @Inject
    @Qualifier(LdapUserDao.NAME)
    private LdapUserDao ldapUserDao;

    @Inject
    @Qualifier(LdapConstants.LDAP_TEMPLATE_BEAN_NAME)
    private LdapTemplate ldapTemplate;

    @Inject
    @Qualifier(CubaUserDao.NAME)
    private CubaUserDao cubaUserDao;

    @Inject
    @Qualifier(MatchingRuleDao.NAME)
    private MatchingRuleDao matchingRuleDao;


    public LdapUser findLdapUserByFilter(String login, String filter) {
        return ldapUserDao.findLdapUserByFilter(login, filter);
    }

    @Override
    public void find(String filter) {
        Object obj = ldapTemplate.lookup("uid=ben,ou=people");
        int t = 4;
    }
}
