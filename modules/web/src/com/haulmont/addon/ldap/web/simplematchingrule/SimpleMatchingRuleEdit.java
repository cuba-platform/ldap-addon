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

package com.haulmont.addon.ldap.web.simplematchingrule;

import com.haulmont.addon.ldap.entity.SimpleMatchingRule;
import com.haulmont.addon.ldap.entity.SimpleRuleCondition;
import com.haulmont.addon.ldap.service.MatchingRuleService;
import com.haulmont.addon.ldap.web.datasource.RuleRolesDatasource;
import com.haulmont.cuba.core.global.EntityStates;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.ValidationErrors;
import com.haulmont.cuba.gui.screen.Subscribe;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.Role;

import javax.inject.Inject;
import javax.inject.Named;

public class SimpleMatchingRuleEdit extends AbstractEditor<SimpleMatchingRule> {

    @Named("conditionsTable")
    private Table<SimpleRuleCondition> simpleRuleConditionTable;

    @Named("rolesTable")
    private Table<Role> roleTable;

    @Inject
    private RuleRolesDatasource rolesDs;

    @Inject
    private MatchingRuleService matchingRuleService;

    @Named("accessGroupFieldGroup.accessGroupField")
    private PickerField<Group> accessGroupField;

    @Inject
    private EntityStates entityStates;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        rolesDs.init(getItem());
        rolesDs.refresh();
        if (!entityStates.isNew(getEditedEntity())) {
            accessGroupField.setValue(matchingRuleService.getAccessGroupForMatchingRule(getEditedEntity()));
        }
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        if (simpleRuleConditionTable.getDatasource().getItems().isEmpty()) {
            errors.add(simpleRuleConditionTable, getMessage("validationEmptyConditions"));
        }
        if (roleTable.getDatasource().getItems().isEmpty()) {
            errors.add(roleTable, getMessage("validationEmptyRoles"));
        }
    }
}