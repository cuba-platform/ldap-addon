package com.haulmont.addon.ldap.core.service;

import com.haulmont.addon.ldap.core.dao.MatchingRuleDao;
import com.haulmont.addon.ldap.entity.AbstractMatchingRule;
import com.haulmont.addon.ldap.entity.FixedMatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRule;
import com.haulmont.addon.ldap.service.MatchingRuleService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@Service(MatchingRuleService.NAME)
public class MatchingRuleServiceBean implements MatchingRuleService{

    @Inject
    @Qualifier(MatchingRuleDao.NAME)
    private MatchingRuleDao matchingRuleDao;

    @Override
    public List<MatchingRule> getMatchingRules() {
        return matchingRuleDao.getMatchingRules();
    }

    @Override
    public int getMatchingRulesCount() {
        return matchingRuleDao.getMatchingRulesCount();
    }

    @Override
    public List<AbstractMatchingRule> getMatchingRulesGui() {
        return matchingRuleDao.getMatchingRulesGui();
    }

}
