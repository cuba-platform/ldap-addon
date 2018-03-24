package com.haulmont.addon.ldap.core.spring;

import com.haulmont.addon.ldap.core.rule.custom.CustomLdapMatchingRule;
import com.haulmont.addon.ldap.core.rule.custom.LdapMatchingRule;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AfterContextInitializedCustomRulesChecker implements ApplicationListener<ContextRefreshedEvent> {
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
