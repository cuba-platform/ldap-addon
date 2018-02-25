package com.haulmont.addon.ldap.core.spring;

import com.haulmont.addon.ldap.core.rule.programmatic.LdapMatchingRule;
import com.haulmont.addon.ldap.core.rule.programmatic.CustomLdapMatchingRule;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.haulmont.addon.ldap.core.spring.CustomRulesBeanDefinitionRegistryPostProcessor.NAME;
import static com.haulmont.addon.ldap.core.utils.LdapConstants.LDAP_COMPONENT_PREFIX;

@Component(NAME)
public class CustomRulesBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    public static final String NAME = "ldap_ProgrammaticRulesBeanDefinitionRegistryPostProcessor";

    //TODO: можно прикрутить локализованные сообщения сюда???
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        try {
            List<String> customRulesIds = new ArrayList<>();
            ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
            provider.addIncludeFilter(new AnnotationTypeFilter(LdapMatchingRule.class));
            Set<BeanDefinition> beans = provider.findCandidateComponents("com.haulmont.addon.ldap.core.rule.programmatic");
            for (BeanDefinition bd : beans) {
                Class customRuleClass = Class.forName(bd.getBeanClassName());
                if (!CustomLdapMatchingRule.class.isAssignableFrom(customRuleClass)) {
                    throw new RuntimeException("Programmatic rule must implement " + CustomLdapMatchingRule.class.getName());
                }
                Annotation annotation = customRuleClass.getAnnotation(LdapMatchingRule.class);
                LdapMatchingRule ldapMatchingRule = (LdapMatchingRule) annotation;
                try {
                    UUID uuid = UUID.fromString(ldapMatchingRule.uuid());
                } catch (IllegalArgumentException ex) {
                    throw new RuntimeException("Don't uuid" + ldapMatchingRule.uuid());
                }
                customRulesIds.add(ldapMatchingRule.uuid());

                bd.setScope(ConfigurableBeanFactory.SCOPE_SINGLETON);
                registry.registerBeanDefinition(LDAP_COMPONENT_PREFIX + bd.getBeanClassName(), bd);
            }
            Map<String, Long> countMap = customRulesIds.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            countMap.forEach((key, value) -> {
                if (value > 1) {
                    throw new RuntimeException("Duplicate custom rule id " + key);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Error creating programmatic rule", e);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
