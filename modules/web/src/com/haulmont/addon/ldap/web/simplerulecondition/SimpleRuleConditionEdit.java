/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.addon.ldap.web.simplerulecondition;

import com.haulmont.addon.ldap.entity.SimpleRuleCondition;
import com.haulmont.addon.ldap.service.LdapService;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.data.Datasource;

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