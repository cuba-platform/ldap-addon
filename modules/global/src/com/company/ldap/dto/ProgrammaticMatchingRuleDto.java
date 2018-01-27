package com.company.ldap.dto;

import com.company.ldap.entity.AbstractMatchingRule;
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
