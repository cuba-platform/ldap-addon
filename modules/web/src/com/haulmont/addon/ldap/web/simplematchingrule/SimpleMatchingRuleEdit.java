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
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionPropertyContainer;
import com.haulmont.cuba.gui.model.DataContext;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.entity.Role;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@UiDescriptor("simple-matching-rule-edit.xml")
@UiController("ldap$SimpleMatchingRule.edit")
@EditedEntityContainer("simpleMatchingRuleDs")
public class SimpleMatchingRuleEdit extends StandardEditor<SimpleMatchingRule> {

    @Inject
    private MessageBundle messageBundle;

    @Inject
    private CollectionPropertyContainer<SimpleRuleCondition> conditionsDs;
    @Inject
    private CollectionContainer<Role> rolesDs;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        getScreenData().loadAll();
        rolesDs.setItems(getEditedEntity().getRoles());
        setModifiedAfterOpen(false);
    }

    @Subscribe
    public void onValidation(ValidationEvent event) {
        if (conditionsDs.getItems().isEmpty()) {
            event.getErrors().add(messageBundle.getMessage("validationEmptyConditions"));
        }
        if (rolesDs.getItems().isEmpty()) {
            event.getErrors().add(messageBundle.getMessage("validationEmptyRoles"));
        }
    }

    @Subscribe
    public void onBeforeCommitChanges(BeforeCommitChangesEvent event) {
        String rolesList = rolesDs.getItems().stream()
                .map(Role::getName)
                .collect(Collectors.joining(";"));
        getEditedEntity().setRolesList(rolesList);

        // TODO: 30.03.2022 more smart way to except commiting roles
        DataContext dataContext = getScreenData().getDataContext();
        List<Entity> transientRoles = dataContext.getModified().stream().filter(entity -> entity instanceof Role).collect(Collectors.toList());
        for (Entity transientRole : transientRoles) {
            dataContext.setModified(transientRole, false);
        }
    }

}