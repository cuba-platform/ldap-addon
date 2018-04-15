package com.haulmont.addon.ldap.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum UserSynchronizationResultEnum implements EnumClass<String> {
    SUCCESS_SYNC("SUCCESS_SYNC"),
    ERROR_SYNC("ERROR_SYNC"),
    LDAP_LOGIN_ERROR("LOGIN_ERROR");

    private String id;

    UserSynchronizationResultEnum(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
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
}