package com.company.ldap.core.dao;

import com.company.ldap.core.rule.programmatic.ProgrammaticMatchingRule;
import com.company.ldap.entity.AbstractMatchingRule;
import com.company.ldap.entity.MatchingRule;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.global.AppBeans;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.company.ldap.core.dao.MatchingRuleDao.NAME;

@Service(NAME)
public class MatchingRuleDao {

    private List<ProgrammaticMatchingRule> programmaticMatchingRules;

    public final static String NAME = "ldap_MatchingRuleDao";

    @Inject
    private Persistence persistence;

    @Transactional(readOnly = true)
    public List<AbstractMatchingRule> getDbStoredMatchingRules() {
        TypedQuery<AbstractMatchingRule> query = persistence.getEntityManager().createQuery("select mr from ldap$AbstractMatchingRule mr " +
                "left join fetch mr.roles roles " +
                "left join fetch mr.accessGroup group", AbstractMatchingRule.class);
        return query.getResultList();
    }

    public List<ProgrammaticMatchingRule> getProgrammaticMatchingRules() {
        List<ProgrammaticMatchingRule> result = new ArrayList<>();
        Map<String, ProgrammaticMatchingRule> map = AppBeans.getAll(ProgrammaticMatchingRule.class);
        if (map != null) {
            for (Map.Entry<String, ProgrammaticMatchingRule> me : map.entrySet()) {
                result.add(me.getValue());
            }
        }
        return result;
    }

    @Transactional
    public List<MatchingRule> getMatchingRules() {
        List<MatchingRule> result = new ArrayList<>();
        TypedQuery<AbstractMatchingRule> query = persistence.getEntityManager().createQuery("select mr from ldap$AbstractMatchingRule mr " +
                "left join fetch mr.roles roles " +
                "left join fetch mr.accessGroup group", AbstractMatchingRule.class);
        List<? extends MatchingRule> dbMatchingRules = query.getResultList();
        List<? extends MatchingRule> programmaticMatchingRules = getProgrammaticMatchingRules();
        result.addAll(dbMatchingRules);
        result.addAll(programmaticMatchingRules);
        return result;
    }


}
