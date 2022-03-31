/*
 * Copyright (c) 2008-2022 Haulmont.
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

package com.haulmont.addon.ldap.web.screens.login;

import com.haulmont.addon.ldap.service.TenantProviderService;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;
import com.haulmont.cuba.security.auth.Credentials;
import com.haulmont.cuba.security.auth.LoginPasswordCredentials;
import com.haulmont.cuba.security.global.LoginException;
import com.haulmont.cuba.web.app.login.LoginScreen;

import javax.inject.Inject;
import java.util.Map;


@UiController("login-with-tenant")
@UiDescriptor("login-with-tenant-screen.xml")
public class LoginWithTenantScreen extends LoginScreen {

    @Inject
    private TenantProviderService tenantProviderService;
    @Inject
    private LookupField<String> tenantField;

    @Override
    protected void onInit(InitEvent event) {
        super.onInit(event);

        Map<String, String> urlParams = urlRouting.getState().getParams();
        if (urlParams.get("tenantId") != null) {
            tenantField.setVisible(false);
        } else {
            tenantField.setOptionsList(tenantProviderService.getTenantIds());
        }
    }

    @Override
    protected void doLogin(Credentials credentials) throws LoginException {
        String tenantId;
        if (urlRouting.getState().getParams().get("tenantId") != null) {
            tenantId = urlRouting.getState().getParams().get("tenantId");
        } else {
            tenantId = tenantField.getValue();
        }

        if (credentials instanceof LoginPasswordCredentials) {
            ((LoginPasswordCredentials) credentials).getParams().put("tenantId", tenantId);
        }

        super.doLogin(credentials);
    }
}