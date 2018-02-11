package com.haulmont.addon.ldap.core.dao;

import com.haulmont.addon.ldap.core.rule.programmatic.ProgrammaticMatchingRule;
import com.haulmont.addon.ldap.dto.ProgrammaticMatchingRuleDto;
import com.haulmont.addon.ldap.entity.AbstractMatchingRule;
import com.haulmont.addon.ldap.entity.FixedMatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRule;
import com.haulmont.addon.ldap.entity.SimpleMatchingRule;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.haulmont.addon.ldap.core.dao.MatchingRuleDao.NAME;

@Service(NAME)
public class MatchingRuleDao {

    public final static String NAME = "ldap_MatchingRuleDao";

    @Inject
    private Persistence persistence;

    @Inject
    private Metadata metadata;

    @Inject
    private Messages messages;

    public ProgrammaticMatchingRuleDto mapProgrammaticRule(ProgrammaticMatchingRule pmr){
        ProgrammaticMatchingRuleDto programmaticMatchingRuleDto = metadata.create(ProgrammaticMatchingRuleDto.class);
        programmaticMatchingRuleDto.setId(pmr.getId());
        programmaticMatchingRuleDto.setProgrammaticRuleName(pmr.getProgrammaticRuleName());
        programmaticMatchingRuleDto.setRuleType(pmr.getRuleType());
        programmaticMatchingRuleDto.setAccessGroup(pmr.getAccessGroup());
        programmaticMatchingRuleDto.setRoles(pmr.getRoles() == null ? new ArrayList<>() : pmr.getRoles());
        programmaticMatchingRuleDto.setIsDisabled(pmr.getIsDisabled() == null ? false : pmr.getIsDisabled());
        programmaticMatchingRuleDto.setIsOverrideExistingAccessGroup(pmr.getIsOverrideExistingAccessGroup() == null ? false : pmr.getIsOverrideExistingAccessGroup());
        programmaticMatchingRuleDto.setIsOverrideExistingRoles(pmr.getIsOverrideExistingRoles() == null ? false : pmr.getIsOverrideExistingRoles());
        programmaticMatchingRuleDto.setIsTerminalRule(pmr.getIsTerminalRule() == null ? false : pmr.getIsTerminalRule());
        return programmaticMatchingRuleDto;
    }

    @Transactional(readOnly = true)
    public List<AbstractMatchingRule> getDbStoredMatchingRules() {
        TypedQuery<AbstractMatchingRule> query = persistence.getEntityManager().createQuery("select distinct mr from ldap$AbstractMatchingRule mr " +
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
        TypedQuery<AbstractMatchingRule> query = persistence.getEntityManager().createQuery("select distinct mr from ldap$AbstractMatchingRule mr " +
                "left join fetch mr.roles roles " +
                "left join fetch mr.accessGroup group", AbstractMatchingRule.class);
        List<? extends MatchingRule> dbMatchingRules = query.getResultList();
        initializeDbMatchingRules(dbMatchingRules);
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
        TypedQuery<AbstractMatchingRule> query = persistence.getEntityManager().createQuery("select distinct mr from ldap$AbstractMatchingRule mr " +
                "left join fetch mr.roles roles " +
                "left join fetch mr.accessGroup group", AbstractMatchingRule.class);
        List<AbstractMatchingRule> dbMatchingRules = query.getResultList();
        initializeDbMatchingRules(dbMatchingRules);
        List<? extends MatchingRule> programmaticMatchingRules = getProgrammaticMatchingRules();
        List<ProgrammaticMatchingRuleDto> programmaticDto = new ArrayList<>(programmaticMatchingRules.size());

        for (MatchingRule programmaticMatchingRule : programmaticMatchingRules) {
            ProgrammaticMatchingRule pmr = (ProgrammaticMatchingRule) programmaticMatchingRule;
            programmaticDto.add(mapProgrammaticRule(pmr));
        }

        result.addAll(dbMatchingRules);
        result.addAll(programmaticDto);
        return result;
    }

    @Transactional(readOnly = true)
    public FixedMatchingRule getFixedMatchingRule() {
        TypedQuery<FixedMatchingRule> query = persistence.getEntityManager().createQuery("select distinct mr from ldap$FixedMatchingRule mr " +
                "left join fetch mr.roles roles " +
                "left join fetch mr.accessGroup group", FixedMatchingRule.class);
        List<FixedMatchingRule> list = query.getResultList();
        if (list.size() > 1) {
            throw new RuntimeException(messages.formatMessage(MatchingRuleDao.class, "onlySingleDefaultRule"));
        }
        FixedMatchingRule result = list.size() == 0 ? null : list.get(0);
        return result;
    }

    @Transactional
    public void updateDisabledStateForMatchingRule(UUID id, Boolean value) {
        Query query = persistence.getEntityManager().createQuery("update ldap$AbstractMatchingRule mr set mr.isDisabled = :disabled where mr.id = :id");
        query.setParameter("id", id);
        query.setParameter("disabled", value);
        query.executeUpdate();
    }

    private void initializeDbMatchingRules(List<? extends MatchingRule> rules) {
        for (MatchingRule rule : rules) {
            if (rule instanceof SimpleMatchingRule) {
                SimpleMatchingRule simpleMatchingRule = (SimpleMatchingRule) rule;
                simpleMatchingRule.getConditions().stream();//initialize conditions in session
            }
        }
    }


}
