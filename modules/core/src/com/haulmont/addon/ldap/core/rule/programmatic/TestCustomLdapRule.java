package com.haulmont.addon.ldap.core.rule.programmatic;

import com.haulmont.addon.ldap.config.LdapConfig;
import com.haulmont.addon.ldap.core.dao.CubaUserDao;
import com.haulmont.addon.ldap.core.dao.LdapUserDao;
import com.haulmont.addon.ldap.core.rule.ApplyMatchingRuleContext;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.Role;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserRole;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@LdapMatchingRule(description = "Test programmatic Rule")
//TODO: програматик на кастом
public class TestCustomLdapRule implements CustomLdapMatchingRule {

    @Inject
    private LdapUserDao ldapUserDao;

    @Inject
    private LdapConfig ldapConfig;

    @Inject
    private CubaUserDao cubaUserDao;

    @Override
    public boolean checkCustomMatchingRule(ApplyMatchingRuleContext applyMatchingRuleContext) {
        return applyMatchingRuleContext.getLdapUser().getLogin().equalsIgnoreCase("barts");
    }

    @Override
    public Group getAccessGroup() {//TODO: добавить контекст
        User admin =  cubaUserDao.getCubaUserByLogin("admin");
        return admin.getGroup();
    }

    @Override
    public List<Role> getRoles() {//TODO: добавить контекст
        User admin =  cubaUserDao.getCubaUserByLogin("admin");
        return admin.getUserRoles().stream().map(UserRole::getRole).collect(Collectors.toList());
    }

    @Override
    public Boolean getIsTerminalRule() {
        return false;
    }

    @Override
    public Boolean getIsOverrideExistingRoles() {
        return false;
    }

    @Override
    public Boolean getIsOverrideExistingAccessGroup() {
        return false;
    }

    @Override
    public Boolean getIsDisabled() {
        return false;
    }

}
