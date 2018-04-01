package com.haulmont.addon.ldap.web.defaultmatchingrule;

import com.haulmont.addon.ldap.entity.DefaultMatchingRule;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.ValidationErrors;
import com.haulmont.cuba.security.entity.Role;

import javax.inject.Named;

public class DefaultMatchingRuleEdit extends AbstractEditor<DefaultMatchingRule> {

    @Named("rolesTable")
    private Table<Role> roleTable;

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        if (roleTable.getDatasource().getItems().isEmpty()) {
            errors.add(roleTable, getMessage("validationEmptyRoles"));
        }
    }
}