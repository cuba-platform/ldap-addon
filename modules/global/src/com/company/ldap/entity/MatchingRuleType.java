package com.company.ldap.entity;

public enum MatchingRuleType {

    SIMPLE("SIMPLE", 1),
    SCRIPTING("SCRIPTING", 2),
    PROGRAMMATIC("PROGRAMMATIC", 3),
    FIXED("FIXED", Integer.MAX_VALUE);

    private String code;
    private Integer processOrder;

    MatchingRuleType(String code, Integer processOrder) {
        this.code = code;
        this.processOrder = processOrder;
    }

    public String getCode() {
        return code;
    }

    public Integer getProcessOrder() {
        return processOrder;
    }
}
