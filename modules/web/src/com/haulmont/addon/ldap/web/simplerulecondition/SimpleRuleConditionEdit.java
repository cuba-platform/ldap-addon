package com.haulmont.addon.ldap.web.simplerulecondition;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.addon.ldap.entity.SimpleRuleCondition;
import com.haulmont.cuba.gui.data.Datasource;

import javax.inject.Named;

public class SimpleRuleConditionEdit extends AbstractEditor<SimpleRuleCondition> {


    @Named("simpleRuleConditionDs")
    private Datasource<SimpleRuleCondition> simpleRuleConditionDs;

    @Override
    public void setItem(Entity item) {
        super.setItem(item);
        SimpleRuleCondition inMemorySrc = (SimpleRuleCondition) item;
        SimpleRuleCondition src = simpleRuleConditionDs.getItem();
        src.setAttribute(inMemorySrc.getAttribute());
        src.setAttributeValue(inMemorySrc.getAttributeValue());

    }
}