package com.haulmont.addon.ldap.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

public enum MatchingRuleType implements EnumClass<String> {

    SIMPLE("SIMPLE", "Simple"),
    SCRIPTING("SCRIPTING", "Scripting"),
    CUSTOM("CUSTOM", "Custom"),
    DEFAULT("DEFAULT", "Default");

    private String id;
    private String name;

    MatchingRuleType(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public static MatchingRuleType fromId(String id) {
        for (MatchingRuleType at : MatchingRuleType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}
