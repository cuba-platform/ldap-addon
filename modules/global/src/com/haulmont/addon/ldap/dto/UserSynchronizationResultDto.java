package com.haulmont.addon.ldap.dto;

import java.io.Serializable;

public class UserSynchronizationResultDto implements Serializable {
    private boolean userPrivilegesChanged;
    private boolean inactiveUser;

    public boolean isUserPrivilegesChanged() {
        return userPrivilegesChanged;
    }

    public void setUserPrivilegesChanged(boolean userPrivilegesChanged) {
        this.userPrivilegesChanged = userPrivilegesChanged;
    }

    public boolean isInactiveUser() {
        return inactiveUser;
    }

    public void setInactiveUser(boolean inactiveUser) {
        this.inactiveUser = inactiveUser;
    }
}
