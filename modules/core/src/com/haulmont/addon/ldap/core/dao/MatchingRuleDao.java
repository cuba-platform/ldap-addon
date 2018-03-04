package com.haulmont.addon.ldap.core.dao;

import com.haulmont.addon.ldap.core.rule.programmatic.CustomLdapMatchingRule;
import com.haulmont.addon.ldap.core.rule.programmatic.CustomLdapMatchingRuleWrapper;
import com.haulmont.addon.ldap.dto.CustomLdapMatchingRuleDto;
import com.haulmont.addon.ldap.entity.*;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
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

    public CustomLdapMatchingRuleDto mapProgrammaticRuleToDto(CustomLdapMatchingRule customLdapMatchingRule) {
        CustomLdapMatchingRuleDto customLdapMatchingRuleDto = metadata.create(CustomLdapMatchingRuleDto.class);
        customLdapMatchingRuleDto.setMatchingRuleId(customLdapMatchingRule.getMatchingRuleId());
        customLdapMatchingRuleDto.setDescription(customLdapMatchingRule.getDescription());
        customLdapMatchingRuleDto.setOrder(customLdapMatchingRule.getOrder());
        customLdapMatchingRuleDto.setRuleType(customLdapMatchingRule.getRuleType());
        customLdapMatchingRuleDto.setIsDisabled(customLdapMatchingRule.getIsDisabled());
        return customLdapMatchingRuleDto;
    }

    public List<CustomLdapMatchingRule> getCustomMatchingRules() {
        List<CustomLdapMatchingRule> result = new ArrayList<>();
        Map<String, CustomLdapMatchingRule> map = AppBeans.getAll(CustomLdapMatchingRule.class);
        if (map != null) {
            for (Map.Entry<String, CustomLdapMatchingRule> me : map.entrySet()) {
                CustomLdapMatchingRule cmr = me.getValue();
                CustomLdapMatchingRuleWrapper wrapper = new CustomLdapMatchingRuleWrapper(cmr, cmr.getMatchingRuleId(), cmr.getOrder(), cmr.getIsDisabled(), cmr.getDescription());
                result.add(wrapper);
            }
        }
        return result;
    }

    @Transactional
    public List<CommonMatchingRule> getMatchingRules() {
        List<CommonMatchingRule> result = new ArrayList<>();
        TypedQuery<AbstractDbStoredMatchingRule> query = persistence.getEntityManager().createQuery("select distinct mr from ldap$AbstractDbStoredMatchingRule mr " +
                "left join fetch mr.roles roles " +
                "left join fetch mr.order mrOrder " +
                "left join fetch mr.accessGroup group", AbstractDbStoredMatchingRule.class);
        List<? extends CommonMatchingRule> dbMatchingRules = query.getResultList();
        initializeDbMatchingRules(dbMatchingRules);
        List<? extends CommonMatchingRule> programmaticMatchingRules = getCustomMatchingRules();
        result.addAll(dbMatchingRules);
        result.addAll(programmaticMatchingRules);
        result.sort(Comparator.comparing(mr -> mr.getOrder().getOrder()));
        return result;
    }

    @Transactional(readOnly = true)
    public int getMatchingRulesCount() {
        Query query = persistence.getEntityManager().createQuery("select count(mr.id) from ldap$AbstractDbStoredMatchingRule mr");
        int dbRulesCount = (int) query.getSingleResult();
        int programmaticRulesCount = getCustomMatchingRules().size();
        return dbRulesCount + programmaticRulesCount;
    }

    @Transactional(readOnly = true)
    public List<AbstractCommonMatchingRule> getMatchingRulesGui() {
        return getMatchingRules().stream()
                .map(mr -> MatchingRuleType.CUSTOM.equals(mr.getRuleType()) ? mapProgrammaticRuleToDto((CustomLdapMatchingRule) mr) : (AbstractCommonMatchingRule) mr)
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveMatchingRulesWithOrder(List<AbstractCommonMatchingRule> matchingRules) {

        List<CommonMatchingRule> defaultRules = matchingRules.stream().filter(mr -> DEFAULT.equals(mr.getRuleType())).collect(Collectors.toList());
        if (defaultRules.size() != 1) {
            throw new RuntimeException(messages.formatMessage(MatchingRuleDao.class, "onlySingleDefaultRule"));
        }
        EntityManager entityManager = persistence.getEntityManager();
        matchingRules.forEach(mr -> {
            if (MatchingRuleType.CUSTOM.equals(mr.getRuleType())) {
                MatchingRuleOrder mergedOrder = PersistenceHelper.isNew(mr.getOrder()) ? mr.getOrder() : entityManager.merge(mr.getOrder());
                entityManager.persist(mergedOrder);
            } else {
                AbstractDbStoredMatchingRule mergedRule = PersistenceHelper.isNew(mr) ? (AbstractDbStoredMatchingRule) mr : entityManager.merge((AbstractDbStoredMatchingRule) mr);
                entityManager.persist(mergedRule);
            }
        });
    }

    private void initializeDbMatchingRules(List<? extends CommonMatchingRule> rules) {
        for (CommonMatchingRule rule : rules) {
            if (MatchingRuleType.SIMPLE.equals(rule.getRuleType())) {
                SimpleMatchingRule simpleMatchingRule = (SimpleMatchingRule) rule;
                simpleMatchingRule.getConditions().forEach(con -> con.getSimpleMatchingRule());
            }
        }
    }


}
