package com.company.ldap.web.screens;

import com.company.ldap.dto.ProgrammaticMatchingRuleDto;
import com.company.ldap.entity.*;
import com.company.ldap.service.MatchingRuleService;
import com.haulmont.cuba.core.entity.Entity;
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
    private DataSource matchingRuleDataSource;

    @Inject
    private Metadata metadata;

    @Inject
    private MatchingRuleService matchingRuleService;


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

        if (entity instanceof SimpleMatchingRule) {
            SimpleMatchingRule simpleMatchingRule = (SimpleMatchingRule) entity;
            return new Table.PlainTextCell(simpleMatchingRule.getLdapCondition());
        } else if ((entity instanceof ScriptingMatchingRule)) {
            ScriptingMatchingRule scriptingMatchingRule = (ScriptingMatchingRule) entity;
            return new Table.PlainTextCell(scriptingMatchingRule.getScriptingCondition());
        } else {
            return new Table.PlainTextCell(StringUtils.EMPTY);
        }
    }

    public Component generateMatchingRuleTableCubaColumnCell(AbstractMatchingRule entity) {
        StringBuilder sb = new StringBuilder("Roles: ");
        for (Role role : entity.getRoles()) {
            sb.append(role.getName());
            sb.append(";");
        }
        sb.append("\n");
        sb.append("Access group: ");
        sb.append(entity.getAccessGroup() == null ? StringUtils.EMPTY : entity.getAccessGroup().getName());
        return new Table.PlainTextCell(sb.toString());
    }

    public Component generateMatchingRuleTableTypeColumnCell(AbstractMatchingRule entity) {
        if ((entity instanceof ProgrammaticMatchingRuleDto)) {
            ProgrammaticMatchingRuleDto programmaticMatchingRuleDto = (ProgrammaticMatchingRuleDto) entity;
            String programmaticType = programmaticMatchingRuleDto.getRuleType().getCode();
            return new Table.PlainTextCell(programmaticType + " - " + programmaticMatchingRuleDto.getProgrammaticRuleName());
        } else {
            return new Table.PlainTextCell(entity.getRuleType().getCode());
        }
    }

    public Component generateMatchingRuleTableOptionsColumnCell(AbstractMatchingRule entity) {
        StringBuilder sb = new StringBuilder();
        if (entity.getIsTerminalRule()) {
            sb.append("Terminal; ");
        } else {
            sb.append("Pass-through; ");
        }

        if (entity.getIsOverrideExistingAccessGroup()) {
            sb.append("Override access group; ");
        } else {
            sb.append("Don't override access group; ");
        }

        if (entity.getIsOverrideExistingRoles()) {
            sb.append("Override existing roles; ");
        } else {
            sb.append("Don't Override existing roles; ");
        }

        return new Table.PlainTextCell(sb.toString());
    }

    public Component generateMatchingRuleTableStateColumnCell(AbstractMatchingRule entity) {
        CheckBox checkBox = componentsFactory.createComponent(CheckBox.class);
        if (entity.getIsDisabled()) {
            checkBox.setValue(true);
        } else {
            checkBox.setValue(false);
        }
        if ((entity instanceof ProgrammaticMatchingRuleDto)) {
            checkBox.setEditable(false);
            checkBox.setEnabled(false);
        } else {
            checkBox.addValueChangeListener(new ValueChangeListener() {
                @Override
                public void valueChanged(ValueChangeEvent e) {
                    AbstractMatchingRule mr = matchingRuleTable.getSingleSelected();
                    matchingRuleService.updateDisabledStateForMatchingRule(mr.getId(), (Boolean) e.getValue());
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