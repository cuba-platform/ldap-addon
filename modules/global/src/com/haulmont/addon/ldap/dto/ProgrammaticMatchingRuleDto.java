package com.haulmont.addon.ldap.dto;

import com.haulmont.addon.ldap.entity.AbstractMatchingRule;
import com.haulmont.chile.core.annotations.MetaClass;

@MetaClass(name = "ldap$ProgrammaticMatchingRuleDto")
public class ProgrammaticMatchingRuleDto extends AbstractMatchingRule {

    private String programmaticRuleName;

    public String getProgrammaticRuleName() {
        return programmaticRuleName;
    }

    public void setProgrammaticRuleName(String programmaticRuleName) {
        this.programmaticRuleName = programmaticRuleName;
    }
}
