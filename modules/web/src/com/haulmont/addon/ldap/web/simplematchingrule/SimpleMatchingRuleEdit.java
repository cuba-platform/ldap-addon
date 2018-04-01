package com.haulmont.addon.ldap.web.simplematchingrule;

import com.haulmont.addon.ldap.entity.SimpleMatchingRule;
import com.haulmont.addon.ldap.entity.SimpleRuleCondition;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.security.entity.Role;

import javax.inject.Named;

public class SimpleMatchingRuleEdit extends AbstractEditor<SimpleMatchingRule> {

    @Named("conditionsTable")
    private Table<SimpleRuleCondition> simpleRuleConditionTable;

    @Named("rolesTable")
    private Table<Role> roleTable;

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        if (simpleRuleConditionTable.getDatasource().getItems().isEmpty()) {
            errors.add(simpleRuleConditionTable, getMessage("validationEmptyConditions"));
        }
        if (roleTable.getDatasource().getItems().isEmpty()) {
            errors.add(roleTable, getMessage("validationEmptyRoles"));
        }
    }
}