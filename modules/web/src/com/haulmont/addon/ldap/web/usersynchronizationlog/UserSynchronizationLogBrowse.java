package com.haulmont.addon.ldap.web.usersynchronizationlog;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.addon.ldap.entity.UserSynchronizationLog;
import com.haulmont.cuba.gui.components.Table;

public class UserSynchronizationLogBrowse extends AbstractLookup {

    public Component generateResultCell(UserSynchronizationLog entity) {
        return new Table.PlainTextCell(getMessage(entity.getResult().name()));
    }
}