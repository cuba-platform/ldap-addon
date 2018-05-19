package com.haulmont.addon.ldap.web.usersynchronizationlog;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.addon.ldap.entity.UserSynchronizationLog;
import com.haulmont.cuba.gui.components.TextField;

import javax.inject.Named;

public class UserSynchronizationLogEdit extends AbstractEditor<UserSynchronizationLog> {

    @Named("resultTextField")
    TextField resultTextField;

    @Override
    protected void postInit() {
        super.postInit();
        resultTextField.setValue(getMessage(getItem().getResult().name()));
    }
}