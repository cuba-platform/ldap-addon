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

import java.util.Set;

import static com.haulmont.addon.ldap.core.spring.ProgrammaticRulesBeanDefinitionRegistryPostProcessor.NAME;
import static com.haulmont.addon.ldap.core.utils.LdapConstants.LDAP_COMPONENT_PREFIX;

@Component(NAME)
public class ProgrammaticRulesBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    public static final String NAME = "ldap_ProgrammaticRulesBeanDefinitionRegistryPostProcessor";

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        try {
            ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
            provider.addIncludeFilter(new AnnotationTypeFilter(LdapMatchingRule.class));
            Set<BeanDefinition> beans = provider.findCandidateComponents("com.haulmont.addon.ldap.core.rule.programmatic");
            for (BeanDefinition bd : beans) {
                if (!CustomLdapMatchingRule.class.isAssignableFrom(Class.forName(bd.getBeanClassName()))) {
                    throw new RuntimeException("Programmatic rule must implement " + CustomLdapMatchingRule.class.getName());
                }
                bd.setScope(ConfigurableBeanFactory.SCOPE_SINGLETON);
                registry.registerBeanDefinition(LDAP_COMPONENT_PREFIX + bd.getBeanClassName(), bd);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error creating programmatic rule", e);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
