package com.haulmont.addon.ldap.core;

import com.haulmont.addon.ldap.core.dao.CubaUserDao;
import com.haulmont.addon.ldap.core.dao.LdapUserDao;
import com.haulmont.addon.ldap.core.rule.LdapMatchingRuleContext;
import com.haulmont.addon.ldap.core.rule.custom.CustomLdapMatchingRule;
import com.haulmont.addon.ldap.core.rule.custom.LdapMatchingRule;
import com.haulmont.cuba.security.entity.User;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.stream.Collectors;

@Component
@LdapMatchingRule(name = "Tesla rule", condition = "Assigns all roles from Admin to Tesla")
public class TeslaCustomLdapRule implements CustomLdapMatchingRule {

    @Inject
    private LdapUserDao ldapUserDao;

    @Inject
    private CubaUserDao cubaUserDao;

    @Override
    public boolean applyCustomMatchingRule(LdapMatchingRuleContext ldapMatchingRuleContext) {
        if (ldapMatchingRuleContext.getLdapUser() != null && ldapMatchingRuleContext.getLdapUser().getLogin().equalsIgnoreCase("tesla")) {
            User admin = cubaUserDao.getCubaUserByLogin("admin");
            ldapMatchingRuleContext.getRoles().addAll(admin.getUserRoles().stream().map(ur -> ur.getRole()).collect(Collectors.toList()));
            ldapMatchingRuleContext.setGroup(admin.getGroup());
            return true;
        }
        return false;
    }
}
