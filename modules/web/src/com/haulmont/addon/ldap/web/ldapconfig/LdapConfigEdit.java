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

package com.haulmont.addon.ldap.web.ldapconfig;

import com.haulmont.addon.ldap.entity.LdapConfig;
import com.haulmont.addon.ldap.entity.LdapUserAttribute;
import com.haulmont.addon.ldap.service.LdapService;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.DialogAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;

import javax.inject.Inject;
import java.util.UUID;

import static com.haulmont.cuba.gui.components.Frame.NotificationType.HUMANIZED;
import static com.haulmont.cuba.gui.components.Frame.NotificationType.WARNING;

public class LdapConfigEdit extends AbstractEditor<LdapConfig> {

    @Inject
    private LdapService ldapService;

    @Inject
    private CollectionDatasource<LdapUserAttribute, UUID> ldapUserAttributesDs;

    @Inject
    private Datasource<LdapConfig> ldapConfigDs;

    public void onTestConnectionClick() {
        String result = ldapService.testConnection();

        if ("SUCCESS".equals(result)) {
            showNotification(getMessage("settingsScreenConnectionSuccessCaption"), getMessage("settingsScreenConnectionSuccessMsg"), HUMANIZED);
        } else {
            showNotification(getMessage("settingsScreenConnectionErrorCaption"), result, WARNING);
        }
    }

    public void onUpdateLdapSchemaUserAttributesButtonClick() {

        showOptionDialog(
                getMessage("refreshAttributesFromLdapTitle"),
                getMessage("refreshAttributesFromLdap"),
                MessageType.CONFIRMATION,
                new Action[]{
                        new DialogAction(DialogAction.Type.YES) {
                            public void actionPerform(Component component) {
                                LdapConfig lc = getItem();
                                ldapService.fillLdapUserAttributes(lc.getSchemaBase(), lc.getLdapUserObjectClasses(),
                                        lc.getObjectClassPropertyName(), lc.getAttributePropertyNames());
                                ldapUserAttributesDs.refresh();
                            }
                        },
                        new DialogAction(DialogAction.Type.NO)
                }
        );
    }


    @Override
    protected void postInit() {
        super.postInit();
        LdapConfig lc = ldapService.getLdapConfig();
        ldapConfigDs.setItem(lc);
    }
}