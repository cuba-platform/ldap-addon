package com.haulmont.addon.ldap.core;


import com.haulmont.cuba.core.Persistence;
import org.junit.*;
import org.springframework.context.ApplicationContext;

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
        /*MatchingRuleApplier matchingRuleApplierInitializer = (MatchingRuleApplier) applicationContext.getBean(MatchingRuleApplier.NAME);
        MatchingRuleProcessor matchingRuleChain = matchingRuleApplierInitializer.getMatchingRuleProcessor();
        List<MatchingRuleType> typesByProcessOrderAsc = Arrays.stream(MatchingRuleType.values()).sorted(Comparator.comparing(MatchingRuleType::getProcessOrder))
                .collect(Collectors.toList());

        for (MatchingRuleType mrt : typesByProcessOrderAsc) {
            Assert.assertEquals(matchingRuleChain.getMatchingRuleType(), mrt);

            if (matchingRuleChain.getNext() == null) {
                Assert.assertEquals(MatchingRuleType.DEFAULT, mrt);
            }

            matchingRuleChain = matchingRuleChain.getNext();
        }*/

    }
}
