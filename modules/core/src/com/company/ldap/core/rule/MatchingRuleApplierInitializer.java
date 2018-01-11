package com.company.ldap.core.rule;

import com.company.ldap.core.dao.LdapUserDao;
import com.company.ldap.core.rule.appliers.FixedMatchingRuleChain;
import com.company.ldap.core.rule.appliers.MatchingRuleChain;
import com.company.ldap.core.rule.appliers.ScriptingMatchingRuleChain;
import com.company.ldap.core.rule.appliers.SimpleMatchingRuleChain;
import com.company.ldap.entity.MatchingRuleType;
import com.haulmont.cuba.core.global.Metadata;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import static com.company.ldap.core.rule.MatchingRuleApplierInitializer.NAME;

@Component(NAME)
public class MatchingRuleApplierInitializer {

    public static final String NAME = "ldap_MatchingRuleApplierInitializer";

    private MatchingRuleChain matchingRuleChain;

    @Inject
    @Qualifier(LdapUserDao.NAME)
    private LdapUserDao ldapUserDao;

    @Inject
    private Metadata metadata;

    @PostConstruct
    void initChain() {
        FixedMatchingRuleChain fixedMatchingRuleChain = new FixedMatchingRuleChain(null, MatchingRuleType.FIXED, metadata);
        ScriptingMatchingRuleChain scriptingMatchingRule = new ScriptingMatchingRuleChain(fixedMatchingRuleChain, MatchingRuleType.SCRIPTING, metadata);
        SimpleMatchingRuleChain simpleMatchingRuleChain = new SimpleMatchingRuleChain(scriptingMatchingRule, MatchingRuleType.SIMPLE, metadata, ldapUserDao);
        matchingRuleChain = simpleMatchingRuleChain;
    }

    public MatchingRuleChain getMatchingRuleChain() {
        return matchingRuleChain;
    }

}
