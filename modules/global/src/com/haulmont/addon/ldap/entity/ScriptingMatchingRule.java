package com.haulmont.addon.ldap.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.Lob;

@DiscriminatorValue("SCRIPTING")
@Entity(name = "ldap$ScriptingMatchingRule")
public class ScriptingMatchingRule extends AbstractMatchingRule {
    private static final long serialVersionUID = -5385890969244419336L;

    public ScriptingMatchingRule() {
        super();
        setRuleType(MatchingRuleType.SCRIPTING);
    }

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