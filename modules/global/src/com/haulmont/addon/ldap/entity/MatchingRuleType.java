package com.haulmont.addon.ldap.entity;

public enum MatchingRuleType {

    SIMPLE("SIMPLE", 1, "Simple"),
    SCRIPTING("SCRIPTING", 2, "Scripting"),
    PROGRAMMATIC("PROGRAMMATIC", 3, "Programmatic"),
    FIXED("FIXED", Integer.MAX_VALUE, "Fixed");

    private String code;
    private Integer processOrder;
    private String name;

    MatchingRuleType(String code, Integer processOrder, String name) {
        this.code = code;
        this.processOrder = processOrder;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public Integer getProcessOrder() {
        return processOrder;
    }

    public String getName() {
        return name;
    }
}
