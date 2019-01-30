package com.haulmont.addon.ldap.core.dao;

import com.haulmont.addon.ldap.entity.MatchingRuleStatus;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.global.Metadata;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import static com.haulmont.addon.ldap.core.dao.MatchingRuleStatusDao.NAME;


@Component(NAME)
public class MatchingRuleStatusDao {

    public final static String NAME = "ldap_MatchingRuleStatusDao";

    @Inject
    private Persistence persistence;

    @Inject
    private Metadata metadata;

    @Inject
    private DaoHelper daoHelper;


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
        daoHelper.persistOrMerge(customMatchingRuleStatus);
    }
}
