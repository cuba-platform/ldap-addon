package com.company.ldap.entity;

import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.Lob;

@Entity(name = "ldap$ScriptingMatchingRule")
public class ScriptingMatchingRule extends MatchingRule {
    private static final long serialVersionUID = -5385890969244419336L;

    @Lob
    @Column(name = "STRING_CONDITION")
    private String scriptingCondition;

    public void setScriptingCondition(String scriptingCondition) {
        this.scriptingCondition = scriptingCondition;
    }

    public String getScriptingCondition() {
        return scriptingCondition;
    }


}