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

import com.haulmont.addon.ldap.entity.LdapConfig;
import com.haulmont.addon.ldap.entity.LdapUserAttribute;
import com.haulmont.addon.ldap.entity.SimpleMatchingRule;
import com.haulmont.addon.ldap.entity.SimpleRuleCondition;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@UiDescriptor("simple-rule-condition-edit.xml")
@UiController("ldap$SimpleRuleCondition.edit")
@EditedEntityContainer("simpleRuleConditionDs")
public class SimpleRuleConditionEdit extends StandardEditor<SimpleRuleCondition> {

    @Inject
    private LookupField<String> attributeNameField;


    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        SimpleMatchingRule simpleMatchingRule = getEditedEntity().getSimpleMatchingRule();
        LdapConfig ldapConfig = simpleMatchingRule.getLdapConfig();
        List<String> attributeNames = ldapConfig.getLdapUserAttributes().stream()
                .map(LdapUserAttribute::getAttributeName)
                .collect(Collectors.toList());
        attributeNameField.setOptionsList(attributeNames);
    }

}