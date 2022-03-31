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

package com.haulmont.addon.ldap.service;

import com.haulmont.addon.sdbmt.entity.Tenant;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.security.app.Authentication;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Service(TenantProviderService.NAME)
public class TenantProviderServiceBean implements TenantProviderService {

    @Inject
    private Authentication authentication;
    @Inject
    private DataManager dataManager;

    @Override
    public List<String> getTenantIds() {
        return authentication.withSystemUser(() -> dataManager.load(Tenant.class)
                .query("select e from cubasdbmt$Tenant e")
                .view("_local")
                .list().stream()
                .map(Tenant::getTenantId)
                .collect(Collectors.toList()));
    }
}