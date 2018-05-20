package com.haulmont.addon.ldap.core.custom;

import com.haulmont.addon.ldap.core.dao.CubaUserDao;
import com.haulmont.addon.ldap.core.dao.LdapUserDao;
import com.haulmont.addon.ldap.core.rule.LdapMatchingRuleContext;
import com.haulmont.addon.ldap.core.rule.custom.CustomLdapMatchingRule;
import com.haulmont.addon.ldap.core.rule.custom.LdapMatchingRule;
import com.haulmont.cuba.security.entity.User;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@LdapMatchingRule(name = "barts rule", condition = "Test custom Rule")
public class TestCustomLdapRule implements CustomLdapMatchingRule {

    @Inject
    private LdapUserDao ldapUserDao;

    @Inject
    private CubaUserDao cubaUserDao;

    @Override
    public boolean applyCustomMatchingRule(LdapMatchingRuleContext ldapMatchingRuleContext) {
        if (ldapMatchingRuleContext.getLdapUser() != null && ldapMatchingRuleContext.getLdapUser().getLogin().equalsIgnoreCase("barts")) {
            User admin = cubaUserDao.getCubaUserByLogin("admin");
            ldapMatchingRuleContext.getRoles().add(admin.getUserRoles().get(0).getRole());
            ldapMatchingRuleContext.setGroup(admin.getGroup());
            return true;
        }
        return false;
    }
}
