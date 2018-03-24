package com.haulmont.addon.ldap.core.rule.appliers;

import com.haulmont.addon.ldap.core.dto.LdapUser;
import com.haulmont.addon.ldap.core.rule.ApplyMatchingRuleContext;
import com.haulmont.addon.ldap.core.rule.custom.CustomLdapMatchingRule;
import com.haulmont.addon.ldap.core.service.LdapServiceBean;
import com.haulmont.addon.ldap.entity.AbstractDbStoredMatchingRule;
import com.haulmont.addon.ldap.entity.CommonMatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRuleType;
import com.haulmont.addon.ldap.entity.ScriptingMatchingRule;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.MetadataTools;
import com.haulmont.cuba.core.global.Scripting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.*;

import static com.haulmont.addon.ldap.core.rule.appliers.ScriptingMatchingRuleProcessor.NAME;
import static com.haulmont.addon.ldap.entity.MatchingRuleType.CUSTOM;
import static java.util.stream.Collectors.toList;

@Component(NAME)
public class ScriptingMatchingRuleProcessor extends DbStoredMatchingRuleProcessor {

    public static final String NAME = "ldap_ScriptingMatchingRuleProcessor";

    private final Logger logger = LoggerFactory.getLogger(ScriptingMatchingRuleProcessor.class);

    @Inject
    private Scripting scripting;

    @Inject
    private Messages messages;

    @Inject
    private MetadataTools metadataTools;

    public ScriptingMatchingRuleProcessor() {
        super(MatchingRuleType.SCRIPTING);
    }

    @Override
    public boolean checkMatchingRule(AbstractDbStoredMatchingRule matchingRule, ApplyMatchingRuleContext applyMatchingRuleContext) {
        Object scriptExecutionResult = null;
        String groovyScript = ((ScriptingMatchingRule) matchingRule).getScriptingCondition();

        Map<String, Object> context = new HashMap<>();
        context.put("__context__", getContextCopy(applyMatchingRuleContext));
        try {
            scriptExecutionResult = scripting.evaluateGroovy(groovyScript.replace("{E}", "__context__"), context);
        } catch (Exception e) {
            throw new RuntimeException(messages.formatMessage(LdapServiceBean.class, "errorDuringGroovyScriptEvaluation", applyMatchingRuleContext.getLdapUser().getLogin()), e);
        }
        if (scriptExecutionResult instanceof Boolean) {
            return (Boolean) scriptExecutionResult;
        } else {
            throw new RuntimeException(messages.formatMessage(LdapServiceBean.class, "testGroovyScriptResultNonBoolean", scriptExecutionResult == null ? "null" : scriptExecutionResult.toString()));
        }
    }

    private ApplyMatchingRuleContext getContextCopy(ApplyMatchingRuleContext source) {
        ApplyMatchingRuleContext tempContext = new ApplyMatchingRuleContext(new LdapUser(source.getLdapUser()), source.getLdapUserAttributes(),
                metadataTools.deepCopy(source.getCubaUser()));
        tempContext.getCurrentRoles().addAll(source.getCurrentRoles().stream().map(cmr -> metadataTools.deepCopy(cmr)).collect(toList()));
        tempContext.setCurrentGroup(metadataTools.deepCopy(source.getCurrentGroup()));

        List<CommonMatchingRule> customRules = source.getAppliedRules().stream().filter(mr -> CUSTOM.equals(mr.getRuleType())).collect(toList());
        List<AbstractDbStoredMatchingRule> dbRules = new ArrayList<>();
        source.getAppliedRules().stream().filter(mr -> !CUSTOM.equals(mr.getRuleType())).forEach(dbRule -> {
            AbstractDbStoredMatchingRule abstractDbStoredMatchingRule = (AbstractDbStoredMatchingRule) dbRule;
            dbRules.add(metadataTools.deepCopy(abstractDbStoredMatchingRule));
        });

        tempContext.getAppliedRules().addAll(customRules);
        tempContext.getAppliedRules().addAll(dbRules);

        return tempContext;
    }
}
