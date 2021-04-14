package com.haulmont.addon.ldap.core.service;

import com.google.common.base.Strings;
import com.haulmont.addon.ldap.entity.AbstractDbStoredMatchingRule;
import com.haulmont.addon.ldap.service.MatchingRuleAccessGroupService;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.group.AccessGroupsService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(MatchingRuleAccessGroupService.NAME)
public class MatchingRuleAccessGroupServiceBean implements MatchingRuleAccessGroupService {

	@Inject
	private AccessGroupsService accessGroupsService;

	@Override
	public Group getAccessGroupMatchingRule(AbstractDbStoredMatchingRule rule) {
		if (rule.getAccessGroup() == null && !Strings.isNullOrEmpty(rule.getAccessGroupName())) {
			return accessGroupsService.findPredefinedGroupByName(rule.getAccessGroupName());
		}
		return rule.getAccessGroup();
	}
}