package com.company.ldap.core.rule.appliers;

import com.company.ldap.core.dao.LdapUserDao;
import com.company.ldap.core.rule.ApplyMatchingRuleContext;
import com.company.ldap.entity.MatchingRule;
import com.company.ldap.entity.MatchingRuleType;
import com.haulmont.cuba.core.global.Metadata;

public class SimpleMatchingRuleChain extends MatchingRuleChain {

    private final LdapUserDao ldapUserDao;

    public SimpleMatchingRuleChain(MatchingRuleChain next, MatchingRuleType matchingRuleType, Metadata metadata, LdapUserDao ldapUserDao) {
        super(next, matchingRuleType, metadata);
        this.ldapUserDao = ldapUserDao;
    }

    @Override
    public boolean checkRule(MatchingRule matchingRule, ApplyMatchingRuleContext applyMatchingRuleContext) {
        return false;
    }
}
