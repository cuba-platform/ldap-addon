package com.haulmont.addon.ldap.core.dao;

import com.haulmont.addon.ldap.entity.MatchingRuleOrder;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.global.PersistenceHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.UUID;

import static com.haulmont.addon.ldap.core.dao.MatchingRuleOrderDao.NAME;

@Service(NAME)
public class MatchingRuleOrderDao {

    public final static String NAME = "ldap_MatchingRuleOrderDao";

    @Inject
    private Persistence persistence;


    @Transactional(readOnly = true)
    public MatchingRuleOrder getOrderById(UUID id) {
        TypedQuery<MatchingRuleOrder> query = persistence.getEntityManager().createQuery("select mro from ldap$MatchingRuleOrder mro " +
                "where mro.id = :id", MatchingRuleOrder.class);
        query.setParameter("id", id);
        return query.getFirstResult();
    }

    @Transactional
    public void saveMatchingRuleOrder(MatchingRuleOrder matchingRuleOrder) {
        EntityManager entityManager = persistence.getEntityManager();
        MatchingRuleOrder mergedMatchingRuleOrder = PersistenceHelper.isNew(matchingRuleOrder) ? matchingRuleOrder : entityManager.merge(matchingRuleOrder);
        entityManager.persist(mergedMatchingRuleOrder);
    }
}
