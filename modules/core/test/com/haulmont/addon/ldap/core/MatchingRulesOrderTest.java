package com.haulmont.addon.ldap.core;


import com.haulmont.addon.ldap.core.rule.MatchingRuleApplierInitializer;
import com.haulmont.addon.ldap.core.rule.appliers.MatchingRuleChain;
import com.haulmont.addon.ldap.entity.MatchingRuleType;
import com.haulmont.cuba.core.Persistence;
import org.junit.*;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class MatchingRulesOrderTest {
    @ClassRule
    public static LdapTestContainer cont = LdapTestContainer.Common.INSTANCE;

    private Persistence persistence;
    private ApplicationContext applicationContext;

    @Before
    public void setUp() throws Exception {
        persistence = cont.persistence();
        applicationContext = cont.getSpringAppContext();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testMatchingRulesOrder() {
        MatchingRuleApplierInitializer matchingRuleApplierInitializer = (MatchingRuleApplierInitializer) applicationContext.getBean(MatchingRuleApplierInitializer.NAME);
        MatchingRuleChain matchingRuleChain = matchingRuleApplierInitializer.getMatchingRuleChain();
        List<MatchingRuleType> typesByProcessOrderAsc = Arrays.stream(MatchingRuleType.values()).sorted(Comparator.comparing(MatchingRuleType::getProcessOrder))
                .collect(Collectors.toList());

        for (MatchingRuleType mrt : typesByProcessOrderAsc) {
            Assert.assertEquals(matchingRuleChain.getMatchingRuleType(), mrt);

            if (matchingRuleChain.getNext() == null) {
                Assert.assertEquals(MatchingRuleType.DEFAULT, mrt);
            }

            matchingRuleChain = matchingRuleChain.getNext();
        }

    }
}
