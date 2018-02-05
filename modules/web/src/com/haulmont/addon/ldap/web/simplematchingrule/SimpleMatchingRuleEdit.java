package com.haulmont.addon.ldap.web.simplematchingrule;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.addon.ldap.entity.SimpleMatchingRule;
import com.haulmont.cuba.gui.components.actions.AddAction;
import com.haulmont.cuba.gui.components.actions.CreateAction;

import javax.inject.Named;

public class SimpleMatchingRuleEdit extends AbstractEditor<SimpleMatchingRule> {

    @Named("conditionsTable.create")
    private CreateAction conditionsTableCreate;

    @Override
    protected void postInit() {
        super.postInit();
        conditionsTableCreate.setInitialValues(ParamsMap.of("simpleMatchingRule", getItem()));
    }
}