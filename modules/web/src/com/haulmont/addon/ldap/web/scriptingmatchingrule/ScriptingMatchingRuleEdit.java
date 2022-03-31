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

package com.haulmont.addon.ldap.web.scriptingmatchingrule;

import com.google.common.base.Strings;
import com.haulmont.addon.ldap.dto.GroovyScriptTestResultDto;
import com.haulmont.addon.ldap.entity.ScriptingMatchingRule;
import com.haulmont.addon.ldap.service.LdapService;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.DataContext;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.entity.Role;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static com.haulmont.addon.ldap.dto.GroovyScriptTestResult.*;

@UiDescriptor("scripting-matching-rule-edit.xml")
@UiController("ldap$ScriptingMatchingRule.edit")
@EditedEntityContainer("scriptingMatchingRuleDs")
public class ScriptingMatchingRuleEdit extends StandardEditor<ScriptingMatchingRule> {
    @Inject
    private Notifications notifications;
    @Inject
    private MessageBundle messageBundle;
    @Inject
    private LdapService ldapService;

    @Inject
    private CollectionContainer<Role> rolesDs;
    @Inject
    private Table<Role> rolesTable;
    @Inject
    private TextField<String> userLoginTextField;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        getScreenData().loadAll();
        rolesDs.setItems(getEditedEntity().getRoles());
        setModifiedAfterOpen(false);
    }

    @Subscribe
    public void onValidation(ValidationEvent event) {
        if (rolesTable.getItems() != null && rolesTable.getItems().getItems().isEmpty()) {
            event.getErrors().add(rolesTable, messageBundle.getMessage("validationEmptyRoles"));
        }
    }

    public void onTestConstraintButtonClick() {
        String login = userLoginTextField.getValue();
        String groovyScript = getEditedEntity().getScriptingCondition();
        if (!Strings.isNullOrEmpty(login) && !Strings.isNullOrEmpty(groovyScript)) {
            String tenantId = getEditedEntity().getLdapConfig().getSysTenantId();
            GroovyScriptTestResultDto result = ldapService.testGroovyScript(groovyScript, login, tenantId);
            if (NO_USER.equals(result.getResult())) {
                notifications.create(Notifications.NotificationType.WARNING)
                        .withCaption(messageBundle.getMessage("notificationError"))
                        .withDescription(messageBundle.formatMessage("testGroovyScriptResultNoUser", login))
                        .show();
            } else if (COMPILATION_ERROR.equals(result.getResult())) {
                notifications.create(Notifications.NotificationType.WARNING)
                        .withCaption(messageBundle.getMessage("notificationError"))
                        .withDescription(messageBundle.formatMessage("testGroovyScriptResultCompilationError", result.getErrorText()))
                        .show();
            } else if (OTHER_ERROR.equals(result.getResult())) {
                notifications.create(Notifications.NotificationType.WARNING)
                        .withCaption(messageBundle.getMessage("notificationError"))
                        .withDescription(messageBundle.formatMessage("testGroovyScriptResultOtherError", result.getErrorText()))
                        .show();
            } else if (NON_BOOLEAN_RESULT.equals(result.getResult())) {
                notifications.create(Notifications.NotificationType.WARNING)
                        .withCaption(messageBundle.getMessage("notificationError"))
                        .withDescription(messageBundle.formatMessage("testGroovyScriptResultNonBoolean"))
                        .show();
            } else if (FALSE.equals(result.getResult())) {
                notifications.create(Notifications.NotificationType.WARNING)
                        .withCaption(messageBundle.getMessage("notificationSuccess"))
                        .withDescription(messageBundle.formatMessage("testGroovyScriptResultFalse"))
                        .show();
            } else if (TRUE.equals(result.getResult())) {
                notifications.create(Notifications.NotificationType.WARNING)
                        .withCaption(messageBundle.getMessage("notificationSuccess"))
                        .withDescription(messageBundle.formatMessage("testGroovyScriptResultTrue"))
                        .show();
            }
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