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

@LdapMatchingRule
public class TestLdapProgrammaticRule implements LdapProgrammaticMatchingRule {

    @Inject
    private LdapUserDao ldapUserDao;

    @Inject
    private LdapConfig ldapConfig;

    @Inject
    private CubaUserDao cubaUserDao;

    @Override
    public boolean checkProgrammaticMatchingRule(ApplyMatchingRuleContext applyMatchingRuleContext) {
        return true;
    }

    @Override
    public Group getAccessGroup() {
        return null;
    }

    @Override
    public List<Role> getRoles() {
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

    @Override
    public String getDescription() {
        return "RULE THAT SET ADMIN ROLE";
    }
}
