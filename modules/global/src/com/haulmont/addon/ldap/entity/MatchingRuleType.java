package com.haulmont.addon.ldap.entity;

public enum MatchingRuleType {

    SIMPLE("SIMPLE", "Simple"),
    SCRIPTING("SCRIPTING", "Scripting"),
    CUSTOM("CUSTOM", "Custom"),
    DEFAULT("DEFAULT", "Default");

    private String code;
    private String name;

    MatchingRuleType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
