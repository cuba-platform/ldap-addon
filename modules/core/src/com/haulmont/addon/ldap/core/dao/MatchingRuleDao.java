package com.haulmont.addon.ldap.core.dao;

import com.haulmont.addon.ldap.core.rule.programmatic.CustomLdapMatchingRule;
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
import com.haulmont.cuba.security.entity.User;
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

    @Inject
    private MatchingRuleOrderDao matchingRuleOrderDao;

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

    public List<CustomLdapMatchingRule> getCustomMatchingRules() {
        List<CustomLdapMatchingRule> result = new ArrayList<>();
        Map<String, CustomLdapMatchingRule> map = AppBeans.getAll(CustomLdapMatchingRule.class);
        if (map != null) {
            for (Map.Entry<String, CustomLdapMatchingRule> me : map.entrySet()) {
                CustomLdapMatchingRule customRule = me.getValue();
                MatchingRuleOrder customRuleOrder = matchingRuleOrderDao.getOrderById(customRule.getId());
                customRule.setOrder(customRuleOrder);
                result.add(customRule);
            }
        }
        return result;
    }

    @Transactional
    public List<MatchingRule> getMatchingRules() {
        List<MatchingRule> result = new ArrayList<>();
        TypedQuery<AbstractMatchingRule> query = persistence.getEntityManager().createQuery("select distinct mr from ldap$AbstractMatchingRule mr " +
                "left join fetch mr.roles roles " +
                "left join fetch mr.order mrOrder " +
                "left join fetch mr.accessGroup group", AbstractMatchingRule.class);
        List<? extends MatchingRule> dbMatchingRules = query.getResultList();

        List<MatchingRule> defaultRules = dbMatchingRules.stream().filter(mr -> DEFAULT.equals(mr.getRuleType())).collect(Collectors.toList());
        if (defaultRules.size() != 1) {
            throw new RuntimeException(messages.formatMessage(MatchingRuleDao.class, "onlySingleDefaultRule"));
        }

        initializeDbMatchingRules(dbMatchingRules);
        List<? extends MatchingRule> programmaticMatchingRules = getCustomMatchingRules();
        result.addAll(dbMatchingRules);
        result.addAll(programmaticMatchingRules);
        result.sort((r1, r2) -> {
            int o1 = r1.getOrder() == null || r1.getOrder().getOrder() == null ? 0 : r1.getOrder().getOrder();
            int o2 = r2.getOrder() == null || r2.getOrder().getOrder() == null ? 0 : r2.getOrder().getOrder();
            return o1 - o2;
        });
        return result;
    }

    @Transactional(readOnly = true)
    public int getMatchingRulesCount() {
        Query query = persistence.getEntityManager().createQuery("select count(mr.id) from ldap$AbstractMatchingRule mr");
        int dbRulesCount = (int) query.getSingleResult();
        int programmaticRulesCount = getCustomMatchingRules().size();
        return dbRulesCount + programmaticRulesCount;
    }

    @Transactional(readOnly = true)
    public List<AbstractMatchingRule> getMatchingRulesGui() {
        List<MatchingRule> mrList = getMatchingRules();
        List<AbstractMatchingRule> result = new ArrayList<>();

        mrList.forEach(mr -> {
            if (MatchingRuleType.CUSTOM.equals(mr.getRuleType())) {
                result.add(mapProgrammaticRule((CustomLdapMatchingRule) mr));
            } else {
                result.add((AbstractMatchingRule) mr);
            }
        });

        return result;
    }

    @Transactional
    public void saveMatchingRulesWithOrder(List<AbstractMatchingRule> matchingRules) {
        EntityManager entityManager = persistence.getEntityManager();
        matchingRules.forEach(mr -> {
            if (MatchingRuleType.CUSTOM.equals(mr.getRuleType())) {
                if (mr.getOrder() != null) {
                    MatchingRuleOrder mergedOrder = PersistenceHelper.isNew(mr.getOrder()) ? mr.getOrder() : entityManager.merge(mr.getOrder());
                    entityManager.persist(mergedOrder);
                }
            } else {
                AbstractMatchingRule mergedRule = PersistenceHelper.isNew(mr) ? mr : entityManager.merge(mr);
                entityManager.persist(mergedRule);
            }
        });
    }

    private void initializeDbMatchingRules(List<? extends MatchingRule> rules) {
        for (MatchingRule rule : rules) {
            if (MatchingRuleType.SIMPLE.equals(rule.getRuleType())) {
                SimpleMatchingRule simpleMatchingRule = (SimpleMatchingRule) rule;
                simpleMatchingRule.getConditions().forEach(con -> con.getSimpleMatchingRule());
            }
        }
    }


}
