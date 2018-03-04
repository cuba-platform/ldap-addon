package com.haulmont.addon.ldap.core.rule.appliers;

import com.haulmont.addon.ldap.core.rule.ApplyMatchingRuleContext;
import com.haulmont.addon.ldap.entity.CommonMatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRuleType;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.security.entity.Role;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserRole;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.haulmont.addon.ldap.core.rule.appliers.MatchingRuleApplier.NAME;


@Component(NAME)
public class MatchingRuleApplier {

    public static final String NAME = "ldap_MatchingRuleApplier";

    private final Map<MatchingRuleType, MatchingRuleProcessor> matchingRuleProcessors = new HashMap<>();

    @Inject
    private Metadata metadata;

    public void applyMatchingRules(List<CommonMatchingRule> matchingRules, ApplyMatchingRuleContext applyMatchingRuleContext) {
        List<CommonMatchingRule> activeMatchingRules = matchingRules.stream().filter(cmr -> !cmr.getIsDisabled()).collect(Collectors.toList());

        for (CommonMatchingRule commonMatchingRule : activeMatchingRules) {
            matchingRuleProcessors.get(commonMatchingRule.getRuleType()).applyMatchingRule(commonMatchingRule, applyMatchingRuleContext);
            if (applyMatchingRuleContext.isTerminalRuleApply()) {
                break;
            }
        }
        applyContextToUser(applyMatchingRuleContext);
    }


    private void applyContextToUser(ApplyMatchingRuleContext applyMatchingRuleContext) {
        User cubaUser = applyMatchingRuleContext.getCubaUser();
        cubaUser.setGroup(applyMatchingRuleContext.getCurrentGroup());

        for (Role role : applyMatchingRuleContext.getCurrentRoles()) {
            UserRole userRole = metadata.create(UserRole.class);
            userRole.setUser(cubaUser);
            userRole.setRole(role);
            cubaUser.getUserRoles().add(userRole);
        }
    }

    @EventListener
    public void initializeMatchingRuleProcessors(ContextRefreshedEvent event) {
        Map<String, MatchingRuleProcessor> processorMap = event.getApplicationContext().getBeansOfType(MatchingRuleProcessor.class);
        for (Map.Entry<String, MatchingRuleProcessor> mrp : processorMap.entrySet()) {
            matchingRuleProcessors.put(mrp.getValue().getMatchingRuleType(), mrp.getValue());
        }

    }

}
