package com.haulmont.addon.ldap.core.rule.appliers;

import com.haulmont.addon.ldap.core.dao.LdapUserDao;
import com.haulmont.addon.ldap.core.dto.LdapUser;
import com.haulmont.addon.ldap.core.rule.ApplyMatchingRuleContext;
import com.haulmont.addon.ldap.entity.MatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRuleType;
import com.haulmont.addon.ldap.entity.SimpleMatchingRule;
import com.haulmont.cuba.core.global.Metadata;

public class SimpleMatchingRuleChain extends MatchingRuleChain {

    private final LdapUserDao ldapUserDao;

    public SimpleMatchingRuleChain(MatchingRuleChain next, Metadata metadata, LdapUserDao ldapUserDao) {
        super(next, MatchingRuleType.SIMPLE, metadata);
        this.ldapUserDao = ldapUserDao;
    }

    @Override
    public boolean checkRule(MatchingRule matchingRule, ApplyMatchingRuleContext applyMatchingRuleContext) {
        SimpleMatchingRule simpleMatchingRule = (SimpleMatchingRule) matchingRule;
        LdapUser ldapUser = ldapUserDao.findLdapUserByFilter(simpleMatchingRule.getLdapCondition(), applyMatchingRuleContext.getLdapUser().getLogin());
        return ldapUser != null;
    }
}
