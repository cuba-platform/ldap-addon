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

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.PublishEntityChangedEvents;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.Role;
import com.haulmont.cuba.security.role.RolesService;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Fields for matching rules stored in the DB.
 */
@PublishEntityChangedEvents
@NamePattern("%s|description")
@Listeners({"ldap_RuleDetachListener", "ldap_RuleEntityListener"})
@DiscriminatorValue("ABSTRACT")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "RULE_TYPE", discriminatorType = DiscriminatorType.STRING)
@Table(name = "LDAP_MATCHING_RULE")
@Entity(name = "ldap$AbstractDbStoredMatchingRule")
public abstract class AbstractDbStoredMatchingRule extends AbstractCommonMatchingRule implements MatchingRule {
    private static final long serialVersionUID = 1956446424046023195L;
    @Lob
    @Column(name = "ROLES_LIST")
    protected String rolesList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCESS_GROUP_ID")
    private Group accessGroup;

    @Column(name = "IS_TERMINAL_RULE")
    private Boolean isTerminalRule = false;

    @Column(name = "IS_OVERRIDE_EXISTING_ROLES")
    private Boolean isOverrideExistingRoles = false;

    @Column(name = "IS_OVERRIDE_EXIST_ACCESS_GRP")
    private Boolean isOverrideExistingAccessGroup = false;

    @Column(name = "ACCESS_GROUP_NAME")
    private String accessGroupName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LDAP_CONFIG_ID")
    protected LdapConfig ldapConfig;

    @Transient
    private List<Role> roles = new ArrayList<>();

    public String getAccessGroupName() {
        return accessGroupName;
    }

    public void setAccessGroupName(String accessGroupName) {
        this.accessGroupName = accessGroupName;
    }

    public String getRolesList() {
        return rolesList;
    }

    public void setRolesList(String rolesList) {
        this.rolesList = rolesList;
    }

    @Override
    public Group getAccessGroup() {
        return accessGroup;
    }

    public void setAccessGroup(Group accessGroup) {
        this.accessGroup = accessGroup;
    }

    @Override
    public List<Role> getRoles() {
        List<Role> roles = new ArrayList<>();
        if (getRolesList() != null) {
            roles = Arrays.stream(getRolesList().split(";"))
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .distinct()
                    .filter(s -> !s.isEmpty())
                    .map(s -> {
                                Role role = AppBeans.get(RolesService.class).getRoleDefinitionAndTransformToRole(s);
                                if (role == null) {
                                    LoadContext<Role> roleLoadContext = new LoadContext<>(Role.class);
                                    roleLoadContext
                                            .setView(View.LOCAL)
                                            .setQueryString("select r from sec$Role r where r.name=:name")
                                            .setParameter("name", s)
                                            .setMaxResults(1);
                                    role = AppBeans.get(DataManager.class).load(roleLoadContext);
                                }
                                return role;
                            }
                    )
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

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

    public void updateRolesList(List<Role> roles) {
        String rolesList = roles.stream()
                .map(Role::getName)
                .collect(Collectors.joining(";"));
        setRolesList(rolesList);
    }


    public void postLoad() {
        List<Role> roles = new ArrayList<>();
        if (getRolesList() != null) {
            roles = Arrays.stream(getRolesList().split(";"))
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .distinct()
                    .filter(s -> !s.isEmpty())
                    .map(s -> {
                                Role role = AppBeans.get(RolesService.class).getRoleDefinitionAndTransformToRole(s);
                                if (role == null) {
                                    LoadContext<Role> roleLoadContext = new LoadContext<>(Role.class);
                                    roleLoadContext
                                            .setView(View.LOCAL)
                                            .setQueryString("select r from sec$Role r where r.name=:name")
                                            .setParameter("name", s)
                                            .setMaxResults(1);
                                    role = AppBeans.get(DataManager.class).load(roleLoadContext);
                                }
                                return role;
                            }
                    )
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        setRoles(roles);
    }

    public LdapConfig getLdapConfig() {
        return ldapConfig;
    }

    public void setLdapConfig(LdapConfig ldapConfig) {
        this.ldapConfig = ldapConfig;
    }
}