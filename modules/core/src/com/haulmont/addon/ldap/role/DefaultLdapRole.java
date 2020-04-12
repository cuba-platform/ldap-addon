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

package com.haulmont.addon.ldap.role;

import com.haulmont.cuba.security.app.role.AnnotatedRoleDefinition;
import com.haulmont.cuba.security.app.role.annotation.Role;
import com.haulmont.cuba.security.app.role.annotation.SpecificAccess;
import com.haulmont.cuba.security.role.SpecificPermissionsContainer;

@Role(name = "Default LDAP role")
public class DefaultLdapRole extends AnnotatedRoleDefinition {

    @SpecificAccess(permissions = {"cuba.gui.loginToClient"})
    @Override
    public SpecificPermissionsContainer specificPermissions() {
        return super.specificPermissions();
    }
}
