package com.haulmont.addon.ldap.web.screens.datasources;

import com.haulmont.addon.ldap.entity.AbstractCommonMatchingRule;
import com.haulmont.addon.ldap.service.MatchingRuleService;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.data.impl.CustomCollectionDatasource;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class MatchingRuleDatasource extends CustomCollectionDatasource<AbstractCommonMatchingRule, UUID> {

    private MatchingRuleService matchingRuleService = AppBeans.get(MatchingRuleService.class);

    /**
     * @param params
     * @return List of entities. List size is limited to maxResults, starting form firstResult position
     */
    @Override
    protected Collection<AbstractCommonMatchingRule> getEntities(Map<String, Object> params) {
        Collection<AbstractCommonMatchingRule> col =  matchingRuleService.getMatchingRulesGui();
        return col;
    }

    /**
     * @return Count of all entities, stored in database
     */
    @Override
    public int getCount() {
        return matchingRuleService.getMatchingRulesCount();
    }

}