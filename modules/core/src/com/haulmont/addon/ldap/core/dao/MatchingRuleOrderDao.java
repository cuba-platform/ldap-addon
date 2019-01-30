package com.haulmont.addon.ldap.core.dao;

import com.haulmont.addon.ldap.entity.MatchingRuleOrder;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.global.Metadata;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import static com.haulmont.addon.ldap.core.dao.MatchingRuleOrderDao.NAME;

@Component(NAME)
public class MatchingRuleOrderDao {

    public final static String NAME = "ldap_MatchingRuleOrderDao";

    @Inject
    private Persistence persistence;

    @Inject
    private Metadata metadata;

    @Inject
    private DaoHelper daoHelper;


    @Transactional(readOnly = true)
    public MatchingRuleOrder getCustomRuleOrder(String customMatchingRuleId) {
        TypedQuery<MatchingRuleOrder> query = persistence.getEntityManager()
                .createQuery("select mro from ldap$MatchingRuleOrder mro " +
                        "where mro.customMatchingRuleId = :customMatchingRuleId", MatchingRuleOrder.class);
        query.setParameter("customMatchingRuleId", customMatchingRuleId);
        MatchingRuleOrder matchingRuleOrder = query.getFirstResult();
        matchingRuleOrder = matchingRuleOrder == null ? metadata.create(MatchingRuleOrder.class) : matchingRuleOrder;
        if (StringUtils.isEmpty(matchingRuleOrder.getCustomMatchingRuleId())) {
            matchingRuleOrder.setCustomMatchingRuleId(customMatchingRuleId);
        }
        return matchingRuleOrder;

    }

    @Transactional
    public void saveMatchingRuleOrder(MatchingRuleOrder matchingRuleOrder) {
        daoHelper.persistOrMerge(matchingRuleOrder);
    }
}
