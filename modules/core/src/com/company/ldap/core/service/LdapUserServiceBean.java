package com.company.ldap.core.service;

import com.company.ldap.core.dao.LdapUserDao;
import com.company.ldap.core.dto.LdapUserWrapper;
import com.company.ldap.core.rule.ApplyMatchingRuleContext;
import com.company.ldap.core.utils.LdapConstants;
import com.company.ldap.service.LdapUserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import java.util.List;

@Service(LdapUserService.NAME)
public class LdapUserServiceBean implements LdapUserService {

    @Inject
    @Qualifier(LdapUserDao.NAME)
    private LdapUserDao ldapUserDao;

    @Inject
    @Qualifier(LdapConstants.LDAP_TEMPLATE_BEAN_NAME)
    private LdapTemplate ldapTemplate;

    public ApplyMatchingRuleContext getApplyRuleContext(String login) {
        List<LdapUserWrapper> ldapUserWrappers = ldapUserDao.getLdapUserWrappers(login);

        if (CollectionUtils.isEmpty(ldapUserWrappers) || ldapUserWrappers.size() > 1) {
            throw new RuntimeException("invalid count in found by login");
        }

        LdapUserWrapper ldapUserWrapper = ldapUserWrappers.get(0);
        return new ApplyMatchingRuleContext(ldapUserWrapper.getLdapUser(), ldapUserWrapper.getLdapUserAttributes());
    }

    @Override
    public void find(String filter) {
        Object obj = ldapTemplate.lookup("uid=ben,ou=people");
        int t =4;
    }
}
