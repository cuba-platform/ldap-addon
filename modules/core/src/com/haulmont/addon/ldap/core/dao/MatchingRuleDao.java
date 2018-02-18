package com.haulmont.addon.ldap.core.dao;

import com.haulmont.addon.ldap.core.rule.programmatic.CustomLdapMatchingRule;
import com.haulmont.addon.ldap.dto.CustomLdapMatchingRuleDto;
import com.haulmont.addon.ldap.entity.AbstractMatchingRule;
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

import java.util.*;
import java.util.stream.Collectors;

import static com.haulmont.addon.ldap.core.dao.MatchingRuleDao.NAME;
import static com.haulmont.addon.ldap.entity.MatchingRuleType.DEFAULT;

@Service(NAME)
public class MatchingRuleDao {

    public final static String NAME = "ldap_MatchingRuleDao";

    @Inject
    private Persistence persistence;

    @Inject
    private Metadata metadata;

    @Inject
    private Messages messages;

    public CustomLdapMatchingRuleDto mapProgrammaticRule(CustomLdapMatchingRule pmr) {
        CustomLdapMatchingRuleDto customLdapMatchingRuleDto = metadata.create(CustomLdapMatchingRuleDto.class);
        customLdapMatchingRuleDto.setId(pmr.getId());
        customLdapMatchingRuleDto.setDescription(pmr.getDescription());
        customLdapMatchingRuleDto.setOrder(pmr.getOrder());
        customLdapMatchingRuleDto.setRuleType(pmr.getRuleType());
        customLdapMatchingRuleDto.setAccessGroup(pmr.getAccessGroup());
        customLdapMatchingRuleDto.setRoles(pmr.getRoles() == null ? new ArrayList<>() : pmr.getRoles());
        customLdapMatchingRuleDto.setIsDisabled(pmr.getIsDisabled() == null ? false : pmr.getIsDisabled());
        customLdapMatchingRuleDto.setIsOverrideExistingAccessGroup(pmr.getIsOverrideExistingAccessGroup() == null ? false : pmr.getIsOverrideExistingAccessGroup());
        customLdapMatchingRuleDto.setIsOverrideExistingRoles(pmr.getIsOverrideExistingRoles() == null ? false : pmr.getIsOverrideExistingRoles());
        customLdapMatchingRuleDto.setIsTerminalRule(pmr.getIsTerminalRule() == null ? false : pmr.getIsTerminalRule());
        return customLdapMatchingRuleDto;
    }

    @Transactional(readOnly = true)
    public List<AbstractMatchingRule> getDbStoredMatchingRules() {
        TypedQuery<AbstractMatchingRule> query = persistence.getEntityManager().createQuery("select distinct mr from ldap$AbstractMatchingRule mr " +
                "left join fetch mr.roles roles " +
                "left join fetch mr.accessGroup group order by mr.order", AbstractMatchingRule.class);
        return query.getResultList();
    }

    public List<CustomLdapMatchingRule> getProgrammaticMatchingRules() {
        List<CustomLdapMatchingRule> result = new ArrayList<>();
        Map<String, CustomLdapMatchingRule> map = AppBeans.getAll(CustomLdapMatchingRule.class);
        if (map != null) {
            for (Map.Entry<String, CustomLdapMatchingRule> me : map.entrySet()) {
                result.add(me.getValue());
            }
        }
        result.sort(Comparator.comparing(CustomLdapMatchingRule::getOrder));
        return result;
    }

    @Transactional
    public List<MatchingRule> getMatchingRules() {
        List<MatchingRule> result = new ArrayList<>();
        TypedQuery<AbstractMatchingRule> query = persistence.getEntityManager().createQuery("select distinct mr from ldap$AbstractMatchingRule mr " +
                "left join fetch mr.roles roles " +
                "left join fetch mr.accessGroup group order by mr.order", AbstractMatchingRule.class);
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
                "left join fetch mr.accessGroup group order by mr.order", AbstractMatchingRule.class);
        List<AbstractMatchingRule> dbMatchingRules = query.getResultList();
        List<AbstractMatchingRule> defaultRules = dbMatchingRules.stream().filter(mr -> DEFAULT.equals(mr.getRuleType())).collect(Collectors.toList());
        if (defaultRules.size() != 1) {
            throw new RuntimeException(messages.formatMessage(MatchingRuleDao.class, "onlySingleDefaultRule"));
        }
        initializeDbMatchingRules(dbMatchingRules);
        List<? extends MatchingRule> programmaticMatchingRules = getProgrammaticMatchingRules();
        List<CustomLdapMatchingRuleDto> programmaticDto = new ArrayList<>(programmaticMatchingRules.size());

        for (MatchingRule programmaticMatchingRule : programmaticMatchingRules) {
            CustomLdapMatchingRule pmr = (CustomLdapMatchingRule) programmaticMatchingRule;
            programmaticDto.add(mapProgrammaticRule(pmr));
        }

        result.addAll(dbMatchingRules);
        result.addAll(programmaticDto);
        return result;
    }

    private void initializeDbMatchingRules(List<? extends MatchingRule> rules) {
        for (MatchingRule rule : rules) {
            if (rule instanceof SimpleMatchingRule) {
                SimpleMatchingRule simpleMatchingRule = (SimpleMatchingRule) rule;
                simpleMatchingRule.getConditions().forEach(con -> con.getSimpleMatchingRule());//initialize conditions in session
            }
        }
    }


}
