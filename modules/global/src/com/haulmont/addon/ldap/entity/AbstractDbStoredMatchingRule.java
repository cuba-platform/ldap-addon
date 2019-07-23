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

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.Role;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Fields for matching rules stored in the DB.
 */
@NamePattern("%s|description")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "RULE_TYPE", discriminatorType = DiscriminatorType.STRING)
@Table(name = "LDAP_MATCHING_RULE")
@Entity(name = "ldap$AbstractDbStoredMatchingRule")
public abstract class AbstractDbStoredMatchingRule extends AbstractCommonMatchingRule implements MatchingRule {
    private static final long serialVersionUID = 1956446424046023195L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCESS_GROUP_ID")
    private Group accessGroup;

    @JoinTable(name = "LDAP_MATCHING_RULE_ROLE_LINK",
            joinColumns = @JoinColumn(name = "MATCHING_RULE_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
    @ManyToMany
    @Composition
    private List<Role> roles = new ArrayList<>();

    @Column(name = "IS_TERMINAL_RULE")
    private Boolean isTerminalRule = false;

    @Column(name = "IS_OVERRIDE_EXISTING_ROLES")
    private Boolean isOverrideExistingRoles = false;

    @Column(name = "IS_OVERRIDE_EXIST_ACCESS_GRP")
    private Boolean isOverrideExistingAccessGroup = false;

    @Override
    public Group getAccessGroup() {
        return accessGroup;
    }

    public void setAccessGroup(Group accessGroup) {
        this.accessGroup = accessGroup;
    }

    @Override
    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public Boolean getIsTerminalRule() {
        return isTerminalRule;
    }

    public void setIsTerminalRule(Boolean terminalRule) {
        isTerminalRule = terminalRule;
    }

    @Override
    public Boolean getIsOverrideExistingRoles() {
        return isOverrideExistingRoles;
    }

    public void setIsOverrideExistingRoles(Boolean overrideExistingRoles) {
        isOverrideExistingRoles = overrideExistingRoles;
    }

    @Override
    public Boolean getIsOverrideExistingAccessGroup() {
        return isOverrideExistingAccessGroup;
    }

    public void setIsOverrideExistingAccessGroup(Boolean overrideExistingAccessGroup) {
        isOverrideExistingAccessGroup = overrideExistingAccessGroup;
    }

}