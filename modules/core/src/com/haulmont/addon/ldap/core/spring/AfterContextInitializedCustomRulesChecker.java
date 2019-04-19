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

package com.haulmont.addon.ldap.core.spring;

import com.haulmont.addon.ldap.core.rule.custom.CustomLdapMatchingRule;
import com.haulmont.addon.ldap.core.rule.custom.LdapMatchingRule;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.haulmont.addon.ldap.core.spring.AfterContextInitializedCustomRulesChecker.NAME;

@Component(NAME)
public class AfterContextInitializedCustomRulesChecker implements ApplicationListener<ContextRefreshedEvent> {

    public final static String NAME = "ldap_AfterContextInitializedCustomRulesChecker";

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            Map<String, Object> customRulesMap = event.getApplicationContext().getBeansWithAnnotation(LdapMatchingRule.class);
            for (Map.Entry<String, Object> entry : customRulesMap.entrySet()) {
                Object customRule = entry.getValue();
                if (!CustomLdapMatchingRule.class.isAssignableFrom(customRule.getClass())) {
                    throw new RuntimeException("Custom rule must implement " + CustomLdapMatchingRule.class.getName());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error creating custom rule", e);
        }
    }

}
