package com.haulmont.addon.ldap.web.screens;

import com.haulmont.addon.ldap.dto.TestUserSynchronizationDto;
import com.haulmont.addon.ldap.entity.AbstractMatchingRule;
import com.haulmont.addon.ldap.entity.MatchingRuleType;
import com.haulmont.addon.ldap.entity.ScriptingMatchingRule;
import com.haulmont.addon.ldap.entity.SimpleMatchingRule;
import com.haulmont.addon.ldap.service.UserSynchronizationService;
import com.haulmont.addon.ldap.utils.MatchingRuleUtils;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.security.entity.Role;

import javax.inject.Inject;
import javax.inject.Named;

import com.haulmont.cuba.security.entity.User;
import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.UUID;

public class TestMatchingRuleScreen extends AbstractWindow {

    @Named("testRuleScreenUser")
    private PickerField userPickerField;

    @Inject
    private CollectionDatasource<AbstractMatchingRule, UUID> abstractMatchingRulesDs;

    @Inject
    private CollectionDatasource<Role, UUID> rolesDs;

    @Inject
    private MatchingRuleUtils matchingRuleUtils;

    @Inject
    private UserSynchronizationService userSynchronizationService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        //TODO: как сделать чтобы датасорс не загружал ентити
        abstractMatchingRulesDs.clear();
        rolesDs.clear();
    }


    public void onTestRuleScreenTestButtonClick() {
        User cubaUser = userPickerField.getValue();
        if (cubaUser != null) {
            abstractMatchingRulesDs.clear();
            rolesDs.clear();

            TestUserSynchronizationDto dto = userSynchronizationService.testUserSynchronization(cubaUser.getLogin());
            dto.getAppliedMatchingRules().forEach(matchingRule -> abstractMatchingRulesDs.addItem(matchingRule));
            dto.getAppliedCubaRoles().forEach(role -> rolesDs.addItem(role));
        }

    }

    public Component generateTestMatchingRuleTableTypeColumnCell(AbstractMatchingRule entity) {
        return new Table.PlainTextCell(entity.getRuleType().name());
    }

    public Component generateTestMatchingRuleTableOptionsColumnCell(AbstractMatchingRule entity) {
        return new Table.PlainTextCell(matchingRuleUtils.generateMatchingRuleOptionsColumn(entity));
    }

    public Component generateTestMatchingRuleTableResultColumnCell(AbstractMatchingRule entity) {
        return new Table.PlainTextCell(matchingRuleUtils.generateMatchingRuleRolesAccessGroupColumn(entity));
    }

    public Component generateTestMatchingRuleTableConditionColumnCell(AbstractMatchingRule entity) {
        return new Table.PlainTextCell(matchingRuleUtils.generateMatchingRuleTableConditionColumn(entity));
    }
}