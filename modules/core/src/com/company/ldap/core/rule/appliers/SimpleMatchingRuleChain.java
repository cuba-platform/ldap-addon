package com.company.ldap.core.rule.appliers;

import com.company.ldap.core.dao.LdapUserDao;
import com.company.ldap.core.rule.ApplyMatchingRuleContext;
import com.company.ldap.entity.AbstractMatchingRule;
import com.company.ldap.entity.MatchingRule;
import com.company.ldap.entity.MatchingRuleType;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;

public class SimpleMatchingRuleChain extends MatchingRuleChain {

    private final LdapUserDao ldapUserDao;

    public SimpleMatchingRuleChain(MatchingRuleChain next, Metadata metadata, LdapUserDao ldapUserDao) {
        super(next, MatchingRuleType.FIXED, metadata);
        this.ldapUserDao = ldapUserDao;
    }

    @Override
    public boolean checkRule(MatchingRule matchingRule, ApplyMatchingRuleContext applyMatchingRuleContext) {
        return false;
    }
}
