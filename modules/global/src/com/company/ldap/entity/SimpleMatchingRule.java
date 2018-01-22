package com.company.ldap.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.Lob;
@DiscriminatorValue("SIMPLE")
@Entity(name = "ldap$SimpleMatchingRule")
public class SimpleMatchingRule extends AbstractMatchingRule {
    private static final long serialVersionUID = -2383286286785487816L;

    @Lob
    @Column(name = "STRING_CONDITION")
    private String ldapCondition;

    public void setLdapCondition(String ldapCondition) {
        this.ldapCondition = ldapCondition;
    }

    public String getLdapCondition() {
        return ldapCondition;
    }


}