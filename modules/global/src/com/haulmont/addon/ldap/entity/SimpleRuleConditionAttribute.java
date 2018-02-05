package com.haulmont.addon.ldap.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum SimpleRuleConditionAttribute implements EnumClass<String> {

    EMAIL("EMAIL"),
    CN("CN"),
    SN("SN"),
    MEMBER_OF("MEMBER OF"),
    ACCESS_GROUP("ACCESS GROUP"),
    POSITION("POSITION"),
    LANGUAGE("LANGUAGE"),
    OU("OU");

    private String id;

    SimpleRuleConditionAttribute(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static SimpleRuleConditionAttribute fromId(String id) {
        for (SimpleRuleConditionAttribute at : SimpleRuleConditionAttribute.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}