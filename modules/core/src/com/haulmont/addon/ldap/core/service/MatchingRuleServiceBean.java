package com.haulmont.addon.ldap.core.service;

import com.haulmont.addon.ldap.core.dao.MatchingRuleDao;
import com.haulmont.addon.ldap.entity.AbstractCommonMatchingRule;
import com.haulmont.addon.ldap.entity.CommonMatchingRule;
import com.haulmont.addon.ldap.service.MatchingRuleService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service(MatchingRuleService.NAME)
public class MatchingRuleServiceBean implements MatchingRuleService {

    @Inject
    private MatchingRuleDao matchingRuleDao;

    @Override
    public List<CommonMatchingRule> getMatchingRules() {
        return matchingRuleDao.getMatchingRules();
    }

    @Override
    public int getMatchingRulesCount() {
        return matchingRuleDao.getMatchingRulesCount();
    }

    @Override
    public List<AbstractCommonMatchingRule> getMatchingRulesGui() {
        return matchingRuleDao.getMatchingRulesGui();
    }

    @Override
    public void saveMatchingRules(List<AbstractCommonMatchingRule> matchingRules, List<AbstractCommonMatchingRule> matchingRulesToDelete) {
        matchingRuleDao.saveMatchingRules(matchingRules, matchingRulesToDelete);
    }
}
