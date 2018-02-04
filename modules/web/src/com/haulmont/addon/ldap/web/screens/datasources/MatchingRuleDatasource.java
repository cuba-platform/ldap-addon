package com.haulmont.addon.ldap.web.screens.datasources;

import com.haulmont.addon.ldap.entity.AbstractMatchingRule;
import com.haulmont.addon.ldap.service.MatchingRuleService;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.data.impl.CustomCollectionDatasource;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class MatchingRuleDatasource extends CustomCollectionDatasource<AbstractMatchingRule, UUID> {

    private MatchingRuleService matchingRuleService = AppBeans.get(MatchingRuleService.class);

    /**
     * @param params
     * @return List of entities. List size is limited to maxResults, starting form firstResult position
     */
    @Override
    protected Collection<AbstractMatchingRule> getEntities(Map<String, Object> params) {
        return matchingRuleService.getMatchingRulesGui();
    }

    /**
     * @return Count of all entities, stored in database
     */
    @Override
    public int getCount() {
        return matchingRuleService.getMatchingRulesCount();
    }
}