package com.haulmont.addon.ldap.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.Column;

@Table(name = "LDAP_USER_ATTRIBUTE")
@Entity(name = "ldap$LdapUserAttribute")
public class LdapUserAttribute extends StandardEntity {
    private static final long serialVersionUID = 6513479251227654463L;

    @Column(name = "ATTRIBUTE_NAME", nullable = false, unique = true)
    protected String attributeName;

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeName() {
        return attributeName;
    }


}