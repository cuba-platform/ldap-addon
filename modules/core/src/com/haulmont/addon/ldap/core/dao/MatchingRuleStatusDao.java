package com.haulmont.addon.ldap.core.dao;

import com.haulmont.addon.ldap.entity.MatchingRuleStatus;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import static com.haulmont.addon.ldap.core.dao.MatchingRuleStatusDao.NAME;


@Service(NAME)
public class MatchingRuleStatusDao {

    public final static String NAME = "ldap_MatchingRuleStatusDao";

    @Inject
    private Persistence persistence;

    @Inject
    private Metadata metadata;


    @Transactional(readOnly = true)
    public MatchingRuleStatus getMatchingRuleStatus(String customMatchingRuleId) {
        TypedQuery<MatchingRuleStatus> query = persistence.getEntityManager()
                .createQuery("select mrs from ldap$MatchingRuleStatus mrs " +
                "where mrs.customMatchingRuleId = :customMatchingRuleId", MatchingRuleStatus.class);
        query.setParameter("customMatchingRuleId", customMatchingRuleId);
        MatchingRuleStatus matchingRuleStatus = query.getFirstResult();
        matchingRuleStatus = matchingRuleStatus == null ? metadata.create(MatchingRuleStatus.class) : matchingRuleStatus;
        if (StringUtils.isEmpty(matchingRuleStatus.getCustomMatchingRuleId())) {
            matchingRuleStatus.setCustomMatchingRuleId(customMatchingRuleId);
        }
        return matchingRuleStatus;

    }

    @Transactional
    public void saveMatchingRuleStatus(MatchingRuleStatus customMatchingRuleStatus) {
        EntityManager entityManager = persistence.getEntityManager();
        MatchingRuleStatus mergedMatchingRuleOrder = PersistenceHelper.isNew(customMatchingRuleStatus)
                ? customMatchingRuleStatus : entityManager.merge(customMatchingRuleStatus);
        entityManager.persist(mergedMatchingRuleOrder);
    }
}
