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

package com.haulmont.addon.ldap.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

/**
 * Defines the result of a user login attempt.
 */
public enum UserSynchronizationResultEnum implements EnumClass<String> {
    SUCCESS_SYNC("SUCCESS_SYNC", "SUCCESS"),
    ERROR_SYNC("ERROR_SYNC", "ERROR"),
    LDAP_LOGIN_ERROR("LOGIN_ERROR", "ERROR DURING LOGIN"),
    DISABLED_USER_TRY_LOGIN("DISABLED_USER_TRY_LOGIN", "DISABLED USER TRY TO LOGIN"),
    USER_DISABLED_DURING_LDAP_SYNC("USER_DISABLED_DURING_LDAP_SYNC", "USER DISABLED DURING LDAP SYNCHRONIZATION"),
    USER_ENABLED_DURING_LDAP_SYNC("USER_ENABLED_DURING_LDAP_SYNC", "USER ENABLED DURING LDAP SYNCHRONIZATION");

    private String id;
    private String name;

    UserSynchronizationResultEnum(String value, String name) {
        this.id = value;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public static UserSynchronizationResultEnum fromId(String id) {
        for (UserSynchronizationResultEnum at : UserSynchronizationResultEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }


    @Override
    public String toString() {
        return getName();
    }
}