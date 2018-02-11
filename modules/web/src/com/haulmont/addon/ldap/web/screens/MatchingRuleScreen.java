package com.haulmont.addon.ldap.web.screens;

import com.haulmont.addon.ldap.dto.ProgrammaticMatchingRuleDto;
import com.haulmont.addon.ldap.entity.*;
import com.haulmont.addon.ldap.service.MatchingRuleService;
import com.haulmont.addon.ldap.utils.MatchingRuleUtils;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.entity.Role;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class MatchingRuleScreen extends AbstractWindow {

    @Named("matchingRuleTable")
    private Table<AbstractMatchingRule> matchingRuleTable;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private Metadata metadata;

    @Inject
    private MatchingRuleService matchingRuleService;

    @Inject
    private MatchingRuleUtils matchingRuleUtils;


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        EditAction.BeforeActionPerformedHandler customEditBeforeActionPerformedHandler = new EditAction.BeforeActionPerformedHandler() {
            @Override
            public boolean beforeActionPerformed() {
                AbstractMatchingRule rule = matchingRuleTable.getSingleSelected();
                return !MatchingRuleType.PROGRAMMATIC.equals(rule.getRuleType());
            }
        };

        EditAction customEdit = new EditAction(matchingRuleTable) {
            @Override
            public String getWindowId() {
                AbstractMatchingRule rule = matchingRuleTable.getSingleSelected();
                if (MatchingRuleType.FIXED.equals(rule.getRuleType())) {
                    return ("ldap$FixedMatchingRule.edit");
                }
                if (MatchingRuleType.SIMPLE.equals(rule.getRuleType())) {
                    return ("ldap$SimpleMatchingRule.edit");
                } else {
                    return "";
                }
            }
        };

        customEdit.setBeforeActionPerformedHandler(customEditBeforeActionPerformedHandler);

        RemoveAction.BeforeActionPerformedHandler customRemoveBeforeActionPerformedHandler = new RemoveAction.BeforeActionPerformedHandler() {
            @Override
            public boolean beforeActionPerformed() {
                AbstractMatchingRule rule = matchingRuleTable.getSingleSelected();
                return !MatchingRuleType.PROGRAMMATIC.equals(rule.getRuleType());
            }
        };

        RemoveAction customRemove = new RemoveAction(matchingRuleTable);
        customRemove.setBeforeActionPerformedHandler(customRemoveBeforeActionPerformedHandler);

        matchingRuleTable.addAction(customEdit);
        matchingRuleTable.addAction(customRemove);
    }


    public Component generateMatchingRuleTableConditionColumnCell(AbstractMatchingRule entity) {
        return new Table.PlainTextCell(matchingRuleUtils.generateMatchingRuleTableConditionColumn(entity));
    }

    public Component generateMatchingRuleTableCubaColumnCell(AbstractMatchingRule entity) {
        return new Table.PlainTextCell(matchingRuleUtils.generateMatchingRuleRolesAccessGroupColumn(entity));
    }

    public Component generateMatchingRuleTableTypeColumnCell(AbstractMatchingRule entity) {
        return new Table.PlainTextCell(entity.getRuleType().getName());
    }

    public Component generateMatchingRuleTableOptionsColumnCell(AbstractMatchingRule entity) {
        return new Table.PlainTextCell(matchingRuleUtils.generateMatchingRuleOptionsColumn(entity));
    }

    public Component generateMatchingRuleTableStateColumnCell(AbstractMatchingRule entity) {
        CheckBox checkBox = componentsFactory.createComponent(CheckBox.class);
        if (entity.getIsDisabled()) {
            checkBox.setValue(false);
        } else {
            checkBox.setValue(true);
        }
        if ((entity instanceof ProgrammaticMatchingRuleDto)) {
            checkBox.setEditable(false);
            checkBox.setEnabled(false);
        } else {
            checkBox.addValueChangeListener(new ValueChangeListener() {
                @Override
                public void valueChanged(ValueChangeEvent e) {
                    AbstractMatchingRule mr = matchingRuleTable.getSingleSelected();
                    Boolean value = (Boolean) e.getValue();
                    matchingRuleService.updateDisabledStateForMatchingRule(mr.getId(), !value);
                    matchingRuleTable.getDatasource().refresh();
                }
            });
        }

        return checkBox;
    }

    public void onFixedRuleCreateButtonClick() {
        FixedMatchingRule fixedMatchingRule = matchingRuleService.getFixedMatchingRule();
        fixedMatchingRule = fixedMatchingRule == null ? metadata.create(FixedMatchingRule.class) : fixedMatchingRule;
        openCreateRuleWindow(fixedMatchingRule, "ldap$FixedMatchingRule.edit");
    }

    public void onSimpleRuleCreateButtonClick() {
        SimpleMatchingRule fixedMatchingRule = metadata.create(SimpleMatchingRule.class);
        openCreateRuleWindow(fixedMatchingRule, "ldap$SimpleMatchingRule.edit");
    }

    private void openCreateRuleWindow(AbstractMatchingRule abstractMatchingRule, String screenName) {
        Map<String, Object> params = new HashMap<>();
        Window window = openEditor(screenName, abstractMatchingRule, WindowManager.OpenType.NEW_TAB, params);
        window.addListener(new CloseListener() {
            public void windowClosed(String actionId) {
                matchingRuleTable.getDatasource().refresh();
            }
        });
    }
}