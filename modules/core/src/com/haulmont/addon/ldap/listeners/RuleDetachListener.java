/*
 * Copyright (c) 2008-2020 Haulmont.
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

package com.haulmont.addon.ldap.listeners;

import com.haulmont.addon.ldap.entity.AbstractDbStoredMatchingRule;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.listener.BeforeDetachEntityListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component("ldap_RuleDetachListener")
public class RuleDetachListener implements BeforeDetachEntityListener<AbstractDbStoredMatchingRule> {

    @Inject
    protected Persistence persistence;

    @Override
    public void onBeforeDetach(AbstractDbStoredMatchingRule entity, EntityManager entityManager) {
//        entity.postLoad();
    }
}