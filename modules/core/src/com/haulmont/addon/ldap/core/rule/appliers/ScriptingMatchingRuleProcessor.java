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

package com.haulmont.addon.ldap.core.rule.appliers;

import com.haulmont.addon.ldap.core.rule.LdapMatchingRuleContext;
import com.haulmont.addon.ldap.core.service.LdapServiceBean;
import com.haulmont.addon.ldap.dto.LdapUser;
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
    public boolean checkMatchingRule(AbstractDbStoredMatchingRule matchingRule, LdapMatchingRuleContext ldapMatchingRuleContext) {
        Object scriptExecutionResult = null;
        String groovyScript = ((ScriptingMatchingRule) matchingRule).getScriptingCondition();

        Map<String, Object> context = new HashMap<>();
        context.put("__context__", getContextCopy(ldapMatchingRuleContext));
        try {
            scriptExecutionResult = scripting.evaluateGroovy(groovyScript.replace("{ldapContext}", "__context__"), context);
        } catch (Exception e) {
            throw new RuntimeException(messages.formatMessage(LdapServiceBean.class, "errorDuringGroovyScriptEvaluation",
                    ldapMatchingRuleContext.getLdapUser().getLogin()), e);
        }
        if (scriptExecutionResult instanceof Boolean) {
            return (Boolean) scriptExecutionResult;
        } else {
            throw new RuntimeException(messages.formatMessage(LdapServiceBean.class, "testGroovyScriptResultNonBoolean",
                    scriptExecutionResult.toString()));
        }
    }

    private LdapMatchingRuleContext getContextCopy(LdapMatchingRuleContext source) {
        LdapMatchingRuleContext tempContext = new LdapMatchingRuleContext(
                new LdapUser(source.getLdapUser()),
                metadataTools.deepCopy(source.getCubaUser()),
                source.getRoles().stream().filter(Objects::nonNull)
                        .map(cmr -> metadataTools.deepCopy(cmr))
                        .collect(toList()),
                source.getGroup() == null ? null : metadataTools.deepCopy(source.getGroup()));

        List<CommonMatchingRule> customRules = source.getAppliedRules().stream()
                .filter(mr -> CUSTOM == mr.getRuleType())
                .collect(toList());
        List<AbstractDbStoredMatchingRule> dbRules = new ArrayList<>();
        source.getAppliedRules().stream()
                .filter(mr -> !(CUSTOM == mr.getRuleType()))
                .forEach(dbRule -> {
                    AbstractDbStoredMatchingRule abstractDbStoredMatchingRule = (AbstractDbStoredMatchingRule) dbRule;
                    dbRules.add(metadataTools.deepCopy(abstractDbStoredMatchingRule));
                });

        tempContext.getAppliedRules().addAll(customRules);
        tempContext.getAppliedRules().addAll(dbRules);

        return tempContext;
    }
}
