package com.company.ldap.core.dao;

import com.company.ldap.core.rule.programmatic.LdapMatchingRule;
import com.company.ldap.core.rule.programmatic.ProgrammaticMatchingRule;
import com.company.ldap.entity.AbstractMatchingRule;
import com.company.ldap.entity.MatchingRule;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.TypedQuery;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.company.ldap.core.dao.MatchingRuleDao.NAME;

@Service(NAME)
public class MatchingRuleDao {

    private static List<ProgrammaticMatchingRule> programmaticMatchingRules;

    static {
        try {
            List<ProgrammaticMatchingRule> result = new ArrayList<>();
            ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
            provider.addIncludeFilter(new AnnotationTypeFilter(LdapMatchingRule.class));
            Set<BeanDefinition> beans = provider.findCandidateComponents("com.company.ldap.core.rule.programmatic");
            for (BeanDefinition bd : beans) {
                result.add((ProgrammaticMatchingRule) Class.forName(bd.getBeanClassName()).newInstance());
            }
            programmaticMatchingRules = result;
        } catch (Exception e) {
            throw new RuntimeException("Error creating programmatic rule", e);
        }
    }

    public final static String NAME = "ldap_MatchingRuleDao";

    @Inject
    private Persistence persistence;

    @Transactional(readOnly = true)
    public List<AbstractMatchingRule> getDbStoredMatchingRules() {
        TypedQuery<AbstractMatchingRule> query = persistence.getEntityManager().createQuery("select mr from ldap$AbstractMatchingRule mr " +
                "join fetch mr.roles roles " +
                "join fetch mr.accessGroup group", AbstractMatchingRule.class);
        return query.getResultList();
    }

    public List<ProgrammaticMatchingRule> getProgrammaticMatchingRules() {
        return programmaticMatchingRules;
    }

    @Transactional
    public List<MatchingRule> getMatchingRules(){
        List<MatchingRule> result = new ArrayList<>();
        TypedQuery<AbstractMatchingRule> query = persistence.getEntityManager().createQuery("select mr from ldap$AbstractMatchingRule mr " +
                "join fetch mr.roles roles " +
                "join fetch mr.accessGroup group", AbstractMatchingRule.class);
        List<? extends MatchingRule> dbMatchingRules =  query.getResultList();
        List<? extends MatchingRule> programmaticMatchingRules =  getProgrammaticMatchingRules();
        result.addAll(dbMatchingRules);
        result.addAll(programmaticMatchingRules);
        return result;
    }

}
