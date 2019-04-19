/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
