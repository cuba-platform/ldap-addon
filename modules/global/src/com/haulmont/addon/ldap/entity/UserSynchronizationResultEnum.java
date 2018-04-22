package com.haulmont.addon.ldap.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum UserSynchronizationResultEnum implements EnumClass<String> {
    SUCCESS_SYNC("SUCCESS_SYNC", "SUCCESS"),
    ERROR_SYNC("ERROR_SYNC", "ERROR"),
    LDAP_LOGIN_ERROR("LOGIN_ERROR", "ERROR DURING LOGIN");

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