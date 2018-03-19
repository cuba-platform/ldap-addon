package com.haulmont.addon.ldap.core.rule.appliers;

import com.haulmont.addon.ldap.core.rule.ApplyMatchingRuleContext;
import com.haulmont.addon.ldap.core.service.LdapServiceBean;
import com.haulmont.addon.ldap.entity.AbstractDbStoredMatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRuleType;
import com.haulmont.addon.ldap.entity.ScriptingMatchingRule;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Scripting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static com.haulmont.addon.ldap.core.rule.appliers.ScriptingMatchingRuleProcessor.NAME;

@Component(NAME)
public class ScriptingMatchingRuleProcessor extends DbStoredMatchingRuleProcessor {

    public static final String NAME = "ldap_ScriptingMatchingRuleProcessor";

    private final Logger logger = LoggerFactory.getLogger(ScriptingMatchingRuleProcessor.class);

    @Inject
    private Scripting scripting;

    @Inject
    private Messages messages;

    public ScriptingMatchingRuleProcessor() {
        super(MatchingRuleType.SCRIPTING);
    }

    @Override
    public boolean checkMatchingRule(AbstractDbStoredMatchingRule matchingRule, ApplyMatchingRuleContext applyMatchingRuleContext) {
        Object scriptExecutionResult = null;
        String groovyScript = ((ScriptingMatchingRule) matchingRule).getScriptingCondition();
        //TODO: make copy of context
        //ApplyMatchingRuleContext tempContext = new ApplyMatchingRuleContext(applyMatchingRuleContext.getLdapUser(), ldapUserWrapper.getLdapUserAttributes(), cubaUser);
        Map<String, Object> context = new HashMap<>();
        context.put("__context__", applyMatchingRuleContext);
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
}
