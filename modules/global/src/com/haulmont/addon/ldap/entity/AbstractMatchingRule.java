package com.haulmont.addon.ldap.entity;

import javax.persistence.*;

import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.Role;
import com.haulmont.cuba.core.entity.StandardEntity;

import java.util.ArrayList;
import java.util.List;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "RULE_TYPE", discriminatorType = DiscriminatorType.STRING)
@Table(name = "LDAP_MATCHING_RULE")
@Entity(name = "ldap$AbstractMatchingRule")
public abstract class AbstractMatchingRule extends StandardEntity implements MatchingRule {
    private static final long serialVersionUID = 1956446424046023194L;

    @Column(name = "RULE_TYPE")
    @Enumerated(EnumType.STRING)
    private MatchingRuleType ruleType;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "MATCHING_RULE_ORDER_ID")
    protected MatchingRuleOrder order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCESS_GROUP_ID")
    private Group accessGroup;

    @JoinTable(name = "LDAP_MATCHING_RULE_ROLE_LINK",
            joinColumns = @JoinColumn(name = "MATCHING_RULE_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
    @ManyToMany
    private List<Role> roles = new ArrayList<>();

    @Column(name = "IS_TERMINAL_RULE")
    private Boolean isTerminalRule = false;

    @Column(name = "IS_OVERRIDE_EXISTING_ROLES")
    private Boolean isOverrideExistingRoles = false;

    @Column(name = "IS_OVERRIDE_EXIST_ACCESS_GRP")
    private Boolean isOverrideExistingAccessGroup = false;

    @Column(name = "IS_DISABLED")
    private Boolean isDisabled = false;

    @Column(name = "DESCRIPTION", length = 1500)
    private String description;


    @Override
    public MatchingRuleType getRuleType() {
        return ruleType;
    }

    public void setRuleType(MatchingRuleType ruleType) {
        this.ruleType = ruleType;
    }

    @Override
    public MatchingRuleOrder getOrder() {
        return order;
    }

    public void setOrder(MatchingRuleOrder order) {
        this.order = order;
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

    @Override
    public Boolean getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(Boolean disabled) {
        isDisabled = disabled;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}