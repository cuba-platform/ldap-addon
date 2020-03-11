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

package com.haulmont.addon.ldap.web.datasource;

import com.haulmont.addon.ldap.entity.AbstractDbStoredMatchingRule;
import com.haulmont.cuba.gui.data.impl.CustomCollectionDatasource;
import com.haulmont.cuba.security.entity.Role;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class RuleRolesDatasource extends CustomCollectionDatasource<Role, UUID> {

    private AbstractDbStoredMatchingRule rule;

    public void init(AbstractDbStoredMatchingRule rule) {
        this.rule = rule;
    }

    @Override
    protected Collection<Role> getEntities(Map<String, Object> params) {
        rule.postLoad();
        return rule.getRoles();
    }

    @Override
    public void addItem(Role item) {
        if (rule.getRoles().stream().map(Role::getName).noneMatch(r -> r.equals(item.getName()))) {
            super.addItem(item);
            rule.getRoles().add(item);
            rule.updateRolesList();
        }
    }

    @Override
    public void removeItem(Role item) {
        super.removeItem(item);
        rule.getRoles().remove(item);
        rule.updateRolesList();
    }
}
