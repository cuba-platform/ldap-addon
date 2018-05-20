package com.haulmont.addon.ldap.core.dao;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.EntityStates;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import static com.haulmont.addon.ldap.core.dao.DaoHelper.NAME;

@Component(NAME)
public class DaoHelper {
    public final static String NAME = "ldap_DaoHelper";

    @Inject
    private Persistence persistence;

    @Inject
    private EntityStates entityStates;

    @Transactional
    public <T extends Entity> T persistOrMerge(T entity) {
        T mergedEntity;
        EntityManager entityManager = persistence.getEntityManager();
        if (entityStates.isNew(entity)) {
            entityManager.persist(entity);
            mergedEntity = entity;
        } else {
            mergedEntity = entityManager.merge(entity);
        }
        return mergedEntity;
    }
}
