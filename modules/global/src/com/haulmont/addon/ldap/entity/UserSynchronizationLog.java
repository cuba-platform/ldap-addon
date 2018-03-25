package com.haulmont.addon.ldap.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.SoftDelete;
import com.haulmont.cuba.core.entity.Creatable;
import javax.persistence.Lob;
import com.haulmont.cuba.core.entity.Updatable;

@Table(name = "LDAP_USER_SYNCHRONIZATION_LOG")
@Entity(name = "ldap$UserSynchronizationLog")
public class UserSynchronizationLog extends BaseUuidEntity implements SoftDelete, Creatable {
    private static final long serialVersionUID = 1008125711770026646L;

    @Column(name = "CREATE_TS")
    private Date createTs;

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    @Column(name = "DELETE_TS")
    private Date deleteTs;

    @Column(name = "DELETED_BY", length = 50)
    private String deletedBy;

    @Column(name = "LOGIN", nullable = false)
    private String login;

    @Lob
    @Column(name = "LDAP_ATTRIBUTES")
    private String ldapAttributes;

    @Column(name = "RESULT_", nullable = false)
    private String result;

    @Lob
    @Column(name = "APPLIED_RULES")
    private String appliedRules;

    @Lob
    @Column(name = "ROLES_BEFORE")
    private String rolesBefore;

    @Lob
    @Column(name = "ROLES_AFTER")
    private String rolesAfter;

    @Column(name = "ACCESS_GROUP_BEFORE")
    private String accessGroupBefore;

    @Column(name = "ACCESS_GROUP_AFTER")
    private String accessGroupAfter;

    @Lob
    @Column(name = "USER_INFO_BEFORE")
    private String userInfoBefore;

    @Lob
    @Column(name = "USER_INFO_AFTER")
    private String userInfoAfter;

    @Lob
    @Column(name = "ERROR_TEXT")
    private String errorText;

    @Column(name = "IS_NEW_USER")
    private Boolean isNewUser = false;

    @Column(name = "IS_DEACTIVATED")
    private Boolean isDeactivated = false;


    public void setIsNewUser(Boolean isNewUser) {
        this.isNewUser = isNewUser;
    }

    public Boolean getIsNewUser() {
        return isNewUser;
    }

    public void setIsDeactivated(Boolean isDeactivated) {
        this.isDeactivated = isDeactivated;
    }

    public Boolean getIsDeactivated() {
        return isDeactivated;
    }


    public void setLdapAttributes(String ldapAttributes) {
        this.ldapAttributes = ldapAttributes;
    }

    public String getLdapAttributes() {
        return ldapAttributes;
    }


    public void setResult(UserSynchronizationResultEnum result) {
        this.result = result == null ? null : result.getId();
    }

    public UserSynchronizationResultEnum getResult() {
        return result == null ? null : UserSynchronizationResultEnum.fromId(result);
    }

    public void setAppliedRules(String appliedRules) {
        this.appliedRules = appliedRules;
    }

    public String getAppliedRules() {
        return appliedRules;
    }

    public void setRolesBefore(String rolesBefore) {
        this.rolesBefore = rolesBefore;
    }

    public String getRolesBefore() {
        return rolesBefore;
    }

    public void setRolesAfter(String rolesAfter) {
        this.rolesAfter = rolesAfter;
    }

    public String getRolesAfter() {
        return rolesAfter;
    }

    public void setAccessGroupBefore(String accessGroupBefore) {
        this.accessGroupBefore = accessGroupBefore;
    }

    public String getAccessGroupBefore() {
        return accessGroupBefore;
    }

    public void setAccessGroupAfter(String accessGroupAfter) {
        this.accessGroupAfter = accessGroupAfter;
    }

    public String getAccessGroupAfter() {
        return accessGroupAfter;
    }

    public void setUserInfoBefore(String userInfoBefore) {
        this.userInfoBefore = userInfoBefore;
    }

    public String getUserInfoBefore() {
        return userInfoBefore;
    }

    public void setUserInfoAfter(String userInfoAfter) {
        this.userInfoAfter = userInfoAfter;
    }

    public String getUserInfoAfter() {
        return userInfoAfter;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public String getErrorText() {
        return errorText;
    }


    @Override
    public Boolean isDeleted() {
        return deleteTs != null;
    }


    public void setCreateTs(Date createTs) {
        this.createTs = createTs;
    }

    public Date getCreateTs() {
        return createTs;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setDeleteTs(Date deleteTs) {
        this.deleteTs = deleteTs;
    }

    @Override
    public Date getDeleteTs() {
        return deleteTs;
    }

    @Override
    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    @Override
    public String getDeletedBy() {
        return deletedBy;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }


}