package com.company.ldap.core.rule.programmatic;

import com.company.ldap.core.rule.ApplyMatchingRuleContext;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.Role;

import java.util.List;

@LdapMatchingRule
public class TestProgrammaticRule implements ProgrammaticMatchingRule {

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
    public Boolean isTerminalRule() {
        return false;
    }

    @Override
    public Boolean isOverrideExistingRoles() {
        return false;
    }

    @Override
    public Boolean isOverrideExistingAccessGroup() {
        return false;
    }

    @Override
    public Boolean isDisabled() {
        return false;
    }
}
