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

package com.haulmont.addon.ldap.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Lob;

/**
 * Scripting matching rule.<br>
 * Rules of this type are applied if a provided Groovy script returns 'true'.
 */
@DiscriminatorValue("SCRIPTING")
@Entity(name = "ldap$ScriptingMatchingRule")
public class ScriptingMatchingRule extends AbstractDbStoredMatchingRule {
    private static final long serialVersionUID = -5385890969244419336L;

    public ScriptingMatchingRule() {
        super();
        setRuleType(MatchingRuleType.SCRIPTING);
    }

    @Lob
    @Column(name = "STRING_CONDITION")
    private String scriptingCondition;

    public void setScriptingCondition(String scriptingCondition) {
        this.scriptingCondition = scriptingCondition;
    }

    public String getScriptingCondition() {
        return scriptingCondition;
    }
}