package com.company.ldap.core.dao;

import com.company.ldap.core.rule.programmatic.ProgrammaticMatchingRule;
import com.company.ldap.dto.ProgrammaticMatchingRuleDto;
import com.company.ldap.entity.AbstractMatchingRule;
import com.company.ldap.entity.MatchingRule;
import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
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

    public final static String NAME = "ldap_MatchingRuleDao";

    @Inject
    private Persistence persistence;

    @Inject
    Metadata metadata;

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

    @Transactional(readOnly = true)
    public int getMatchingRulesCount() {
        Query query = persistence.getEntityManager().createQuery("select count(mr.id) from ldap$AbstractMatchingRule mr");
        int dbRulesCount = (int) query.getSingleResult();
        int programmaticRulesCount = getProgrammaticMatchingRules().size();
        return dbRulesCount + programmaticRulesCount;
    }

    @Transactional
    public List<AbstractMatchingRule> getMatchingRulesGui() {
        List<AbstractMatchingRule> result = new ArrayList<>();
        TypedQuery<AbstractMatchingRule> query = persistence.getEntityManager().createQuery("select mr from ldap$AbstractMatchingRule mr " +
                "left join fetch mr.roles roles " +
                "left join fetch mr.accessGroup group", AbstractMatchingRule.class);
        List<AbstractMatchingRule> dbMatchingRules = query.getResultList();
        List<? extends MatchingRule> programmaticMatchingRules = getProgrammaticMatchingRules();
        List<ProgrammaticMatchingRuleDto> programmaticDto = new ArrayList<>(programmaticMatchingRules.size());

        for (MatchingRule programmaticMatchingRule : programmaticMatchingRules) {
            ProgrammaticMatchingRule pmr = (ProgrammaticMatchingRule) programmaticMatchingRule;
            ProgrammaticMatchingRuleDto programmaticMatchingRuleDto = metadata.create(ProgrammaticMatchingRuleDto.class);
            programmaticMatchingRuleDto.setProgrammaticRuleName(pmr.getProgrammaticRuleName());
            programmaticMatchingRuleDto.setRuleType(pmr.getRuleType());
            programmaticMatchingRuleDto.setAccessGroup(programmaticMatchingRule.getAccessGroup());
            programmaticMatchingRuleDto.setRoles(programmaticMatchingRule.getRoles());
            programmaticMatchingRuleDto.setIsDisabled(programmaticMatchingRule.getIsDisabled());
            programmaticMatchingRuleDto.setIsOverrideExistingAccessGroup(programmaticMatchingRule.getIsOverrideExistingAccessGroup());
            programmaticMatchingRuleDto.setIsOverrideExistingRoles(programmaticMatchingRule.getIsOverrideExistingRoles());
            programmaticMatchingRuleDto.setIsTerminalRule(programmaticMatchingRule.getIsTerminalRule());
            programmaticDto.add(programmaticMatchingRuleDto);
        }

        result.addAll(dbMatchingRules);
        result.addAll(programmaticDto);
        return result;
    }


}
