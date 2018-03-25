package com.haulmont.addon.ldap.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Version;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Versioned;
import com.haulmont.cuba.core.entity.Updatable;
import com.haulmont.chile.core.annotations.MetaProperty;
import javax.persistence.Transient;

@Table(name = "LDAP_LDAP_CONFIG")
@Entity(name = "ldap$LdapContextConfig")
public class LdapConfig extends BaseUuidEntity implements Versioned, Updatable {
    private static final long serialVersionUID = 7194701707147252828L;

    @Column(name = "SCHEMA_BASE")
    private String schemaBase;

    @Transient
    @MetaProperty
    protected String contextSourceBase;

    @Transient
    @MetaProperty
    protected String contextSourceUserName;

    @Transient
    @MetaProperty
    protected String contextSourceUrl;

    @Column(name = "LDAP_USER_OBJECT_CLASSES", length = 2000)
    private String ldapUserObjectClasses;

    @Column(name = "OBJECT_CLASS_PROPERTY_NAME")
    private String objectClassPropertyName;

    @Column(name = "ATTRIBUTE_PROPERTY_NAMES", length = 2000)
    private String attributePropertyNames;

    @Column(name = "LOGIN_ATTRIBUTE")
    private String loginAttribute;

    @Column(name = "EMAIL_ATTRIBUTE")
    private String emailAttribute;

    @Column(name = "CN_ATTRIBUTE")
    private String cnAttribute;

    @Column(name = "SN_ATTRIBUTE")
    private String snAttribute;

    @Column(name = "MEMBER_OF_ATTRIBUTE")
    private String memberOfAttribute;

    @Column(name = "ACCESS_GROUP_ATTRIBUTE")
    private String accessGroupAttribute;

    @Column(name = "POSITION_ATTRIBUTE")
    private String positionAttribute;

    @Column(name = "OU_ATTRIBUTE")
    private String ouAttribute;

    @Column(name = "LANGUAGE_ATTRIBUTE")
    private String languageAttribute;

    @Column(name = "INACTIVE_USER_ATTRIBUTE")
    private String inactiveUserAttribute;

    @Column(name = "USER_BASE")
    private String userBase;

    @Column(name = "UPDATE_TS")
    private Date updateTs;

    @Column(name = "UPDATED_BY", length = 50)
    private String updatedBy;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Integer version;

    public void setContextSourceBase(String contextSourceBase) {
        this.contextSourceBase = contextSourceBase;
    }

    public String getContextSourceBase() {
        return contextSourceBase;
    }

    public void setContextSourceUserName(String contextSourceUserName) {
        this.contextSourceUserName = contextSourceUserName;
    }

    public String getContextSourceUserName() {
        return contextSourceUserName;
    }


    public void setContextSourceUrl(String contextSourceUrl) {
        this.contextSourceUrl = contextSourceUrl;
    }

    public String getContextSourceUrl() {
        return contextSourceUrl;
    }


    public String getInactiveUserAttribute() {
        return inactiveUserAttribute;
    }

    public void setInactiveUserAttribute(String inactiveUserAttribute) {
        this.inactiveUserAttribute = inactiveUserAttribute;
    }


    public void setSchemaBase(String schemaBase) {
        this.schemaBase = schemaBase;
    }

    public String getSchemaBase() {
        return schemaBase;
    }

    public void setLdapUserObjectClasses(String ldapUserObjectClasses) {
        this.ldapUserObjectClasses = ldapUserObjectClasses;
    }

    public String getLdapUserObjectClasses() {
        return ldapUserObjectClasses;
    }

    public void setObjectClassPropertyName(String objectClassPropertyName) {
        this.objectClassPropertyName = objectClassPropertyName;
    }

    public String getObjectClassPropertyName() {
        return objectClassPropertyName;
    }

    public void setAttributePropertyNames(String attributePropertyNames) {
        this.attributePropertyNames = attributePropertyNames;
    }

    public String getAttributePropertyNames() {
        return attributePropertyNames;
    }


    public void setLoginAttribute(String loginAttribute) {
        this.loginAttribute = loginAttribute;
    }

    public String getLoginAttribute() {
        return loginAttribute;
    }

    public void setEmailAttribute(String emailAttribute) {
        this.emailAttribute = emailAttribute;
    }

    public String getEmailAttribute() {
        return emailAttribute;
    }

    public void setCnAttribute(String cnAttribute) {
        this.cnAttribute = cnAttribute;
    }

    public String getCnAttribute() {
        return cnAttribute;
    }

    public void setSnAttribute(String snAttribute) {
        this.snAttribute = snAttribute;
    }

    public String getSnAttribute() {
        return snAttribute;
    }

    public void setMemberOfAttribute(String memberOfAttribute) {
        this.memberOfAttribute = memberOfAttribute;
    }

    public String getMemberOfAttribute() {
        return memberOfAttribute;
    }

    public void setAccessGroupAttribute(String accessGroupAttribute) {
        this.accessGroupAttribute = accessGroupAttribute;
    }

    public String getAccessGroupAttribute() {
        return accessGroupAttribute;
    }

    public void setPositionAttribute(String positionAttribute) {
        this.positionAttribute = positionAttribute;
    }

    public String getPositionAttribute() {
        return positionAttribute;
    }

    public void setOuAttribute(String ouAttribute) {
        this.ouAttribute = ouAttribute;
    }

    public String getOuAttribute() {
        return ouAttribute;
    }

    public void setLanguageAttribute(String languageAttribute) {
        this.languageAttribute = languageAttribute;
    }

    public String getLanguageAttribute() {
        return languageAttribute;
    }

    public void setUserBase(String userBase) {
        this.userBase = userBase;
    }

    public String getUserBase() {
        return userBase;
    }


    @Override
    public void setUpdateTs(Date updateTs) {
        this.updateTs = updateTs;
    }

    @Override
    public Date getUpdateTs() {
        return updateTs;
    }

    @Override
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public Integer getVersion() {
        return version;
    }


}