package com.company.ldap.entity;

import javax.persistence.*;

import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.Role;
import com.haulmont.cuba.core.entity.StandardEntity;

import java.util.List;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "RULE_TYPE", discriminatorType = DiscriminatorType.STRING)
@Table(name = "LDAP_MATCHING_RULE")
@Entity(name = "ldap$MatchingRule")
public abstract class MatchingRule extends StandardEntity {
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
    private Boolean isTerminalRule;

    @Column(name = "IS_OVERRIDE_EXISTING_ROLES")
    private Boolean isOverrideExistingRoles;

    @Column(name = "IS_OVERRIDE_EXIST_ACCESS_GRP")
    private Boolean isOverrideExistingAccessGroup;


    public MatchingRuleType getRuleType() {
        return ruleType;
    }

    public void setRuleType(MatchingRuleType ruleType) {
        this.ruleType = ruleType;
    }

    public Boolean getIsOverrideExistingAccessGroup() {
        return isOverrideExistingAccessGroup;
    }

    public void setIsOverrideExistingAccessGroup(Boolean isOverrideExistingAccessGroup) {
        this.isOverrideExistingAccessGroup = isOverrideExistingAccessGroup;
    }


    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }


    public void setAccessGroup(Group accessGroup) {
        this.accessGroup = accessGroup;
    }

    public Group getAccessGroup() {
        return accessGroup;
    }

    public void setIsTerminalRule(Boolean isTerminalRule) {
        this.isTerminalRule = isTerminalRule;
    }

    public Boolean getIsTerminalRule() {
        return isTerminalRule;
    }

    public void setIsOverrideExistingRoles(Boolean isOverrideExistingRoles) {
        this.isOverrideExistingRoles = isOverrideExistingRoles;
    }

    public Boolean getIsOverrideExistingRoles() {
        return isOverrideExistingRoles;
    }


}