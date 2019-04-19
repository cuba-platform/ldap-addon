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
