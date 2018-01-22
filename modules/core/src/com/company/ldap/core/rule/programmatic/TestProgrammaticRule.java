package com.company.ldap.core.rule.programmatic;

import com.company.ldap.config.LdapConfig;
import com.company.ldap.core.dao.LdapUserDao;
import com.company.ldap.core.rule.ApplyMatchingRuleContext;
import com.company.ldap.entity.MatchingRuleType;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.Role;

import javax.inject.Inject;
import java.util.List;

@LdapMatchingRule
public class TestProgrammaticRule implements ProgrammaticMatchingRule {

    @Inject
    private LdapUserDao ldapUserDao;

    @Inject
    private LdapConfig ldapConfig;

    @Override
    public boolean checkProgrammaticMatchingRule(ApplyMatchingRuleContext applyMatchingRuleContext) {
        return false;
    }

    @Override
    public Group getAccessGroup() {
        return null;
    }

    @Override
    public List<Role> getRoles() {
        return null;
    }

    @Override
    public Boolean getIsTerminalRule() {
        return null;
    }

    @Override
    public Boolean getIsOverrideExistingRoles() {
        return null;
    }

    @Override
    public Boolean getIsOverrideExistingAccessGroup() {
        return null;
    }

    @Override
    public Boolean getIsDisabled() {
        return null;
    }
}
