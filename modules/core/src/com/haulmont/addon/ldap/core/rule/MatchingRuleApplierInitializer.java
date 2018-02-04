package com.haulmont.addon.ldap.core.rule;

import com.haulmont.addon.ldap.core.dao.LdapUserDao;
import com.haulmont.addon.ldap.core.rule.appliers.*;
import com.haulmont.addon.ldap.entity.MatchingRuleType;
import com.haulmont.cuba.core.global.Metadata;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.haulmont.addon.ldap.core.rule.MatchingRuleApplierInitializer.NAME;

@Component(NAME)
public class MatchingRuleApplierInitializer {

    public static final String NAME = "ldap_MatchingRuleApplierInitializer";

    private MatchingRuleChain matchingRuleChain;

    @Inject
    private Metadata metadata;

    @Inject
    @Qualifier(LdapUserDao.NAME)
    private LdapUserDao ldapUserDao;

    @PostConstruct
    void initChain() {
        List<MatchingRuleType> typesByProcessOrderDesc = Arrays.stream(MatchingRuleType.values()).sorted(Comparator.comparing(MatchingRuleType::getProcessOrder).reversed())
                .collect(Collectors.toList());
        MatchingRuleChain next = null;
        for (MatchingRuleType mrt : typesByProcessOrderDesc) {
            MatchingRuleChain mrc = getChainElement(mrt, next);
            next = mrc;
        }
        matchingRuleChain = next;
    }

    public MatchingRuleChain getMatchingRuleChain() {
        return matchingRuleChain;
    }

    private MatchingRuleChain getChainElement(MatchingRuleType matchingRuleType, MatchingRuleChain next) {
        switch (matchingRuleType) {
            case SIMPLE:
                return new SimpleMatchingRuleChain(next, metadata, ldapUserDao);
            case SCRIPTING:
                return new ScriptingMatchingRuleChain(next, metadata);
            case PROGRAMMATIC:
                return new ProgrammaticMatchingRuleChain(next, metadata);
            case FIXED:
                return new FixedMatchingRuleChain(next, metadata);
            default:
                throw new RuntimeException("Invalid matching rule type");
        }
    }

}
