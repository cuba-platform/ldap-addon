package com.company.ldap.core.service;

import com.company.ldap.core.dao.LdapUserDao;
import com.company.ldap.core.dto.LdapUserWrapper;
import com.company.ldap.core.rule.ApplyMatchingRuleContext;
import com.company.ldap.service.LdapUserService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import java.util.List;

@Service(LdapUserService.NAME)
public class LdapUserServiceBean implements LdapUserService {

    @Inject
    private LdapUserDao ldapUserDao;

    public ApplyMatchingRuleContext getApplyRuleContext(String login) {
        List<LdapUserWrapper> ldapUserWrappers = ldapUserDao.getLdapUserWrappers(login);

        if (CollectionUtils.isEmpty(ldapUserWrappers) || ldapUserWrappers.size() > 1) {
            throw new RuntimeException("invalid count in found by login");
        }

        LdapUserWrapper ldapUserWrapper = ldapUserWrappers.get(0);
        return new ApplyMatchingRuleContext(ldapUserWrapper.getLdapUser(), ldapUserWrapper.getLdapUserAttributes());
    }
}
