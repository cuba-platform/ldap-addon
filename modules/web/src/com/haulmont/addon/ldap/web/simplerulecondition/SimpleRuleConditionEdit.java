package com.haulmont.addon.ldap.web.simplerulecondition;

import com.haulmont.addon.ldap.entity.SimpleRuleCondition;
import com.haulmont.addon.ldap.service.LdapService;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;

import javax.inject.Inject;
import javax.inject.Named;

public class SimpleRuleConditionEdit extends AbstractEditor<SimpleRuleCondition> {


    @Named("simpleRuleConditionDs")
    private Datasource<SimpleRuleCondition> simpleRuleConditionDs;

    @Named("fieldGroup")
    private FieldGroup fieldGroup;

    @Inject
    private UiComponents componentsFactory;

    @Inject
    private LdapService ldapService;

    @Override
    public void setItem(Entity item) {
        super.setItem(item);
        SimpleRuleCondition inMemorySrc = (SimpleRuleCondition) item;
        SimpleRuleCondition src = simpleRuleConditionDs.getItem();
        src.setAttribute(inMemorySrc.getAttribute());
        src.setAttributeValue(inMemorySrc.getAttributeValue());

    }

    @Override
    protected void postInit() {
        super.postInit();
        FieldGroup.FieldConfig attributeField = fieldGroup.getField("attribute");
        LookupField lookupField = componentsFactory.create(LookupField.class);
        lookupField.setOptionsList(ldapService.getLdapUserAttributesNames());
        attributeField.setComponent(lookupField);
        String attribute = simpleRuleConditionDs.getItem().getAttribute();
        lookupField.setValue(attribute);
    }

    @Override
    protected boolean preCommit() {
        FieldGroup.FieldConfig attributeField = fieldGroup.getField("attribute");
        LookupField<String> lookupField = (LookupField) attributeField.getComponent();
        String val = lookupField.getValue();
        simpleRuleConditionDs.getItem().setAttribute(val);
        return true;
    }
}