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

package com.haulmont.addon.ldap.core.spring.events;

import com.haulmont.addon.ldap.core.rule.LdapMatchingRuleContext;
import com.haulmont.cuba.security.entity.User;
import org.springframework.context.ApplicationEvent;

public class UserDeactivatedFromLdapEvent extends ApplicationEvent {

    private final LdapMatchingRuleContext ldapMatchingRuleContext;
    private final User cubaUser;
    private final SynchronizationMode synchronizationMode;

    public UserDeactivatedFromLdapEvent(Object source, LdapMatchingRuleContext ldapMatchingRuleContext, User cubaUser,
                                        SynchronizationMode synchronizationMode) {
        super(source);
        this.ldapMatchingRuleContext = ldapMatchingRuleContext;
        this.cubaUser = cubaUser;
        this.synchronizationMode = synchronizationMode;
    }

    public LdapMatchingRuleContext getLdapMatchingRuleContext() {
        return ldapMatchingRuleContext;
    }

    public User getCubaUser() {
        return cubaUser;
    }

    public SynchronizationMode getSynchronizationMode() {
        return synchronizationMode;
    }
}
