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

import com.haulmont.addon.ldap.entity.*;
import com.haulmont.cuba.security.app.role.AnnotatedRoleDefinition;
import com.haulmont.cuba.security.app.role.annotation.EntityAccess;
import com.haulmont.cuba.security.app.role.annotation.EntityAttributeAccess;
import com.haulmont.cuba.security.app.role.annotation.Role;
import com.haulmont.cuba.security.app.role.annotation.ScreenAccess;
import com.haulmont.cuba.security.entity.EntityOp;
import com.haulmont.cuba.security.role.EntityAttributePermissionsContainer;
import com.haulmont.cuba.security.role.EntityPermissionsContainer;
import com.haulmont.cuba.security.role.ScreenPermissionsContainer;

@Role(name = "LDAP Admin role")
public class LdapAdminRole extends AnnotatedRoleDefinition {

    @EntityAccess(entityClass = UserSynchronizationLog.class,
            operations = {EntityOp.CREATE, EntityOp.READ, EntityOp.UPDATE, EntityOp.DELETE})
    @EntityAccess(entityClass = SimpleRuleCondition.class,
            operations = {EntityOp.CREATE, EntityOp.READ, EntityOp.UPDATE, EntityOp.DELETE})
    @EntityAccess(entityClass = ScriptingMatchingRule.class,
            operations = {EntityOp.CREATE, EntityOp.READ, EntityOp.UPDATE, EntityOp.DELETE})
    @EntityAccess(entityClass = MatchingRuleStatus.class,
            operations = {EntityOp.CREATE, EntityOp.READ, EntityOp.UPDATE, EntityOp.DELETE})
    @EntityAccess(entityClass = MatchingRuleOrder.class,
            operations = {EntityOp.CREATE, EntityOp.READ, EntityOp.UPDATE, EntityOp.DELETE})
    @EntityAccess(entityClass = LdapUserAttribute.class,
            operations = {EntityOp.CREATE, EntityOp.READ, EntityOp.UPDATE, EntityOp.DELETE})
    @EntityAccess(entityClass = LdapConfig.class,
            operations = {EntityOp.CREATE, EntityOp.READ, EntityOp.UPDATE, EntityOp.DELETE})
    @EntityAccess(entityClass = DefaultMatchingRule.class,
            operations = {EntityOp.CREATE, EntityOp.READ, EntityOp.UPDATE, EntityOp.DELETE})
    @Override
    public EntityPermissionsContainer entityPermissions() {
        return super.entityPermissions();
    }

    @EntityAttributeAccess(entityClass = UserSynchronizationLog.class, modify = "*")
    @EntityAttributeAccess(entityClass = SimpleRuleCondition.class, modify = "*")
    @EntityAttributeAccess(entityClass = ScriptingMatchingRule.class, modify = "*")
    @EntityAttributeAccess(entityClass = MatchingRuleStatus.class, modify = "*")
    @EntityAttributeAccess(entityClass = MatchingRuleOrder.class, modify = "*")
    @EntityAttributeAccess(entityClass = LdapUserAttribute.class, modify = "*")
    @EntityAttributeAccess(entityClass = LdapConfig.class, modify = "*")
    @EntityAttributeAccess(entityClass = DefaultMatchingRule.class, modify = "*")
    @Override
    public EntityAttributePermissionsContainer entityAttributePermissions() {
        return super.entityAttributePermissions();
    }

    @ScreenAccess(screenIds = {"application-ldap"})
    @ScreenAccess(screenIds = {"ldap$LdapPropertiesConfig.edit"})
    @ScreenAccess(screenIds = {"ldap$matchingRuleScreen"})
    @ScreenAccess(screenIds = {"ldap$UserSynchronizationLog.browse"})
    @ScreenAccess(screenIds = {"ldap$DefaultMatchingRule.edit"})
    @ScreenAccess(screenIds = {"ldap$SimpleMatchingRule.edit"})
    @ScreenAccess(screenIds = {"ldap$SimpleRuleCondition.edit"})
    @ScreenAccess(screenIds = {"ldap$LdapUserAttribute.edit"})
    @ScreenAccess(screenIds = {"ldap$UserSynchronizationLog.edit"})
    @ScreenAccess(screenIds = {"ldap$ScriptingMatchingRule.edit"})
    @Override
    public ScreenPermissionsContainer screenPermissions() {
        return super.screenPermissions();
    }
}
