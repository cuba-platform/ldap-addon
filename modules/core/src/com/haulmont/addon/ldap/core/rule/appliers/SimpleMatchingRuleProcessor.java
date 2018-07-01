package com.haulmont.addon.ldap.core.rule.appliers;

import com.haulmont.addon.ldap.core.dao.LdapUserDao;
import com.haulmont.addon.ldap.dto.LdapUser;
import com.haulmont.addon.ldap.core.rule.LdapMatchingRuleContext;
import com.haulmont.addon.ldap.entity.AbstractDbStoredMatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRuleType;
import com.haulmont.addon.ldap.entity.SimpleMatchingRule;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static com.haulmont.addon.ldap.core.rule.appliers.SimpleMatchingRuleProcessor.NAME;

@Component(NAME)
public class SimpleMatchingRuleProcessor extends DbStoredMatchingRuleProcessor {

    public static final String NAME = "ldap_SimpleMatchingRuleProcessor";

    @Inject
    private LdapUserDao ldapUserDao;

    public SimpleMatchingRuleProcessor() {
        super(MatchingRuleType.SIMPLE);
    }

    @Override
    public boolean checkMatchingRule(AbstractDbStoredMatchingRule matchingRule, LdapMatchingRuleContext ldapMatchingRuleContext) {
        SimpleMatchingRule simpleMatchingRule = (SimpleMatchingRule) matchingRule;
        LdapUser ldapUser = ldapUserDao.findLdapUserByFilter(simpleMatchingRule.getConditions(), ldapMatchingRuleContext.getLdapUser().getLogin());
        return ldapUser != null;
    }
}
