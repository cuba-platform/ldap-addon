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

package com.haulmont.addon.ldap.dto;

import com.haulmont.addon.ldap.entity.AbstractCommonMatchingRule;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.Role;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

public class TestUserSynchronizationDto implements Serializable {

    private final Set<AbstractCommonMatchingRule> appliedMatchingRules = new LinkedHashSet<>();
    private final Set<Role> appliedCubaRoles = new LinkedHashSet<>();
    private Group group;
    private boolean isUserExistsInLdap = false;

    public Set<AbstractCommonMatchingRule> getAppliedMatchingRules() {
        return appliedMatchingRules;
    }

    public Set<Role> getAppliedCubaRoles() {
        return appliedCubaRoles;
    }

    public boolean isUserExistsInLdap() {
        return isUserExistsInLdap;
    }

    public void setUserExistsInLdap(boolean userExistsInLdap) {
        isUserExistsInLdap = userExistsInLdap;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
