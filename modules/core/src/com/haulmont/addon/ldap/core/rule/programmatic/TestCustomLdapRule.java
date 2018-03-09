package com.haulmont.addon.ldap.core.rule.programmatic;

import com.haulmont.addon.ldap.config.LdapConfig;
import com.haulmont.addon.ldap.core.dao.CubaUserDao;
import com.haulmont.addon.ldap.core.dao.LdapUserDao;
import com.haulmont.addon.ldap.core.rule.ApplyMatchingRuleContext;
import com.haulmont.cuba.security.entity.User;

import javax.inject.Inject;

@LdapMatchingRule(description = "Test programmatic Rule")
public class TestCustomLdapRule implements CustomLdapMatchingRule {

    @Inject
    private LdapUserDao ldapUserDao;

    @Inject
    private LdapConfig ldapConfig;

    @Inject
    private CubaUserDao cubaUserDao;

    @Override
    //TODO: легко забыть добавить нужные значения в контекст
    public boolean applyCustomMatchingRule(ApplyMatchingRuleContext applyMatchingRuleContext) {
        if (applyMatchingRuleContext.getLdapUser().getLogin().equalsIgnoreCase("barts")) {
            User admin = cubaUserDao.getCubaUserByLogin("admin");
            applyMatchingRuleContext.getCurrentRoles().add(admin.getUserRoles().get(0).getRole());
            applyMatchingRuleContext.setCurrentGroup(admin.getGroup());
            applyMatchingRuleContext.getAppliedRules().add(new CustomLdapMatchingRuleWrapper(this));
        }
        return true;
    }
}
