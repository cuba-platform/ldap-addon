package com.company.ldap.entity;

public enum MatchingRuleType {

    FIXED("FIXED", Integer.MAX_VALUE),
    SIMPLE("SIMPLE", 1),
    SCRIPTING("SCRIPTING", 2);

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
