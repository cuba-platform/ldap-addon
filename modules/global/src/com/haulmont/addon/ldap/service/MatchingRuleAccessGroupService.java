package com.haulmont.addon.ldap.service;

import com.haulmont.addon.ldap.entity.AbstractDbStoredMatchingRule;
import com.haulmont.cuba.security.entity.Group;

public interface MatchingRuleAccessGroupService {
	String NAME = "ldap_MatchingRuleAccessGroupService";

	Group getAccessGroupMatchingRule(AbstractDbStoredMatchingRule rule);
}