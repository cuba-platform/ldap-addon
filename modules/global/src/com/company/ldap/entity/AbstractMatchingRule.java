package com.company.ldap.entity;

import javax.persistence.*;

import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.Role;
import com.haulmont.cuba.core.entity.StandardEntity;

import java.util.List;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "RULE_TYPE", discriminatorType = DiscriminatorType.STRING)
@Table(name = "LDAP_MATCHING_RULE")
@Entity(name = "ldap$AbstractMatchingRule")
public abstract class AbstractMatchingRule extends StandardEntity implements MatchingRule {
    private static final long serialVersionUID = 1956446424046023194L;

    @Column(name = "RULE_TYPE")
    @Enumerated(EnumType.STRING)
    private MatchingRuleType ruleType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCESS_GROUP_ID")
    private Group accessGroup;

    @JoinTable(name = "LDAP_MATCHING_RULE_ROLE_LINK",
            joinColumns = @JoinColumn(name = "MATCHING_RULE_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
    @ManyToMany
    private List<Role> roles;

    @Column(name = "IS_TERMINAL_RULE")
    private Boolean isTerminalRule = false;

    @Column(name = "IS_OVERRIDE_EXISTING_ROLES")
    private Boolean isOverrideExistingRoles = false;

    @Column(name = "IS_OVERRIDE_EXIST_ACCESS_GRP")
    private Boolean isOverrideExistingAccessGroup = false;

    @Column(name = "IS_DISABLED")
    private Boolean isDisabled = false;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Override
    public MatchingRuleType getRuleType() {
        return ruleType;
    }

    public void setRuleType(MatchingRuleType ruleType) {
        this.ruleType = ruleType;
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

    public Boolean isTerminalRule() {
        return isTerminalRule;
    }

    public void setTerminalRule(Boolean terminalRule) {
        isTerminalRule = terminalRule;
    }

    public Boolean isOverrideExistingRoles() {
        return isOverrideExistingRoles;
    }

    public void setOverrideExistingRoles(Boolean overrideExistingRoles) {
        isOverrideExistingRoles = overrideExistingRoles;
    }

    public Boolean isOverrideExistingAccessGroup() {
        return isOverrideExistingAccessGroup;
    }

    public void setOverrideExistingAccessGroup(Boolean overrideExistingAccessGroup) {
        isOverrideExistingAccessGroup = overrideExistingAccessGroup;
    }

    public Boolean isDisabled() {
        return isDisabled;
    }

    public void setDisabled(Boolean disabled) {
        isDisabled = disabled;
    }
}