package com.haulmont.addon.ldap.web.screens;

import com.haulmont.addon.ldap.dto.TestUserSynchronizationDto;
import com.haulmont.addon.ldap.entity.*;
import com.haulmont.addon.ldap.service.MatchingRuleService;
import com.haulmont.addon.ldap.service.UserSynchronizationService;
import com.haulmont.addon.ldap.utils.MatchingRuleUtils;
import com.haulmont.addon.ldap.web.screens.datasources.MatchingRuleDatasource;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.entity.Role;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import java.util.*;

import static com.haulmont.addon.ldap.entity.MatchingRuleType.DEFAULT;
import static com.haulmont.addon.ldap.entity.MatchingRuleType.CUSTOM;
import static com.haulmont.addon.ldap.entity.MatchingRuleType.SIMPLE;
import static com.haulmont.cuba.gui.components.Frame.NotificationType.HUMANIZED;

public class MatchingRuleScreen extends AbstractWindow {

    @Named("matchingRuleTable")
    private Table<AbstractMatchingRule> matchingRuleTable;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private Metadata metadata;

    @Inject
    private MatchingRuleUtils matchingRuleUtils;

    @Named("abstractMatchingRulesDs")
    private MatchingRuleDatasource matchingRuleDatasource;

    @Named("appliedMatchingRulesDs")
    private CollectionDatasource<AbstractMatchingRule, UUID> appliedMatchingRulesDs;

    @Named("appliedRolesDs")
    private CollectionDatasource<Role, UUID> appliedRolesDs;

    @Named("testRuleScreenLogin")
    private TextField userLoginTextField;

    @Named("testRuleScreenAppliedGroup")
    private TextField appliedGroupTextField;

    @Inject
    private UserSynchronizationService userSynchronizationService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        appliedRolesDs.clear();

        EditAction.BeforeActionPerformedHandler customEditBeforeActionPerformedHandler = new EditAction.BeforeActionPerformedHandler() {
            @Override
            public boolean beforeActionPerformed() {
                AbstractMatchingRule rule = matchingRuleTable.getSingleSelected();
                return !CUSTOM.equals(rule.getRuleType());
            }
        };

        EditAction.AfterCommitHandler customAfterCommitHandler = new EditAction.AfterCommitHandler() {
            @Override
            public void handle(Entity entity) {
                AbstractMatchingRule amr = (AbstractMatchingRule) entity;
                if (MatchingRuleType.SIMPLE.equals(amr.getRuleType())) {
                    matchingRuleDatasource.getItems().forEach(mr -> {
                        if (MatchingRuleType.SIMPLE.equals(mr.getRuleType()) && mr.getId().equals(amr.getId())) {
                            ((SimpleMatchingRule)amr).getConditions().forEach(con -> {
                                Optional<SimpleRuleCondition> src = ((SimpleMatchingRule) mr).getConditions().stream().filter(c -> c.getId().equals(con.getId())).findFirst();
                                if (src.isPresent()) {
                                    src.get().setAttribute(con.getAttribute());
                                    src.get().setAttributeValue(con.getAttributeValue());
                                }
                            });
                        }
                    });
                }
                matchingRuleTable.repaint();
            }
        };

        EditAction customEdit = new EditAction(matchingRuleTable) {
            @Override
            protected void internalOpenEditor(CollectionDatasource datasource, Entity existingItem, Datasource parentDs, Map<String, Object> params) {
                super.internalOpenEditor(datasource, existingItem, datasource, params);
            }

            @Override
            public String getWindowId() {
                AbstractMatchingRule rule = matchingRuleTable.getSingleSelected();
                if (DEFAULT.equals(rule.getRuleType())) {
                    return ("ldap$DefaultMatchingRule.edit");
                }
                if (SIMPLE.equals(rule.getRuleType())) {
                    return ("ldap$SimpleMatchingRule.edit");
                } else {
                    return "";
                }


            }
        };

        customEdit.setBeforeActionPerformedHandler(customEditBeforeActionPerformedHandler);
        customEdit.setAfterCommitHandler(customAfterCommitHandler);


        RemoveAction.BeforeActionPerformedHandler customRemoveBeforeActionPerformedHandler = new RemoveAction.BeforeActionPerformedHandler() {
            @Override
            public boolean beforeActionPerformed() {
                AbstractMatchingRule rule = matchingRuleTable.getSingleSelected();
                return !(CUSTOM.equals(rule.getRuleType()) || MatchingRuleType.DEFAULT.equals(rule.getRuleType()));
            }
        };

        RemoveAction customRemove = new RemoveAction(matchingRuleTable, false);
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
        if (((CUSTOM.equals(entity.getRuleType()) || DEFAULT.equals(entity.getRuleType())))) {
            checkBox.setEditable(false);
            checkBox.setEnabled(false);
        } else {
            checkBox.addValueChangeListener(new ValueChangeListener() {
                @Override
                public void valueChanged(ValueChangeEvent e) {
                    AbstractMatchingRule mr = matchingRuleTable.getSingleSelected();
                    Boolean value = (Boolean) e.getValue();
                    mr.setIsDisabled(!value);
                }
            });
        }

        return checkBox;
    }

    public void onDefaultRuleCreateButtonClick() {
        DefaultMatchingRule defaultMatchingRule = (DefaultMatchingRule) matchingRuleDatasource.getItems().stream().filter(mr -> DEFAULT.equals(mr.getRuleType())).findFirst().get();
        openCreateRuleWindow(defaultMatchingRule, "ldap$DefaultMatchingRule.edit");
    }

    public void onSimpleRuleCreateButtonClick() {
        SimpleMatchingRule simpleMatchingRule = metadata.create(SimpleMatchingRule.class);
        openCreateRuleWindow(simpleMatchingRule, "ldap$SimpleMatchingRule.edit");
    }

    private void openCreateRuleWindow(AbstractMatchingRule abstractMatchingRule, String screenName) {
        Map<String, Object> params = new HashMap<>();
        openEditor(screenName, abstractMatchingRule, WindowManager.OpenType.NEW_TAB, params, matchingRuleDatasource);
    }

    public void onCommitButtonClick() {

        showOptionDialog(
                getMessage("matchingRuleScreenCommitDialogTitle"),
                getMessage("matchingRuleScreenCommitDialogMsg"),
                MessageType.CONFIRMATION,
                new Action[]{
                        new DialogAction(DialogAction.Type.YES) {
                            public void actionPerform(Component component) {
                                matchingRuleDatasource.commit();
                            }
                        },
                        new DialogAction(DialogAction.Type.NO)
                }
        );

    }

    public void onCancelButtonClick() {
        showOptionDialog(
                getMessage("matchingRuleScreenCancelDialogTitle"),
                getMessage("matchingRuleScreenCancelDialogMsg"),
                MessageType.CONFIRMATION,
                new Action[]{
                        new DialogAction(DialogAction.Type.YES) {
                            public void actionPerform(Component component) {
                                close(StringUtils.EMPTY);
                            }
                        },
                        new DialogAction(DialogAction.Type.NO)
                }
        );
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

    public void onTestRuleScreenTestButtonClick() {
        String login = userLoginTextField.getValue();
        if (StringUtils.isNotEmpty(login)) {
            appliedMatchingRulesDs.clear();
            appliedRolesDs.clear();
            appliedGroupTextField.setValue(StringUtils.EMPTY);

            TestUserSynchronizationDto dto = userSynchronizationService.testUserSynchronization(login, new ArrayList<>(matchingRuleDatasource.getItems()));
            if (!dto.isUserExistsInLdap()) {
                showNotification(getMessage("testRuleScreenUserNotInLdapCaption"), getMessage("testRuleScreenUserNotInLdap"), HUMANIZED);
            } else {
                dto.getAppliedMatchingRules().forEach(matchingRule -> appliedMatchingRulesDs.addItem(matchingRule));
                dto.getAppliedCubaRoles().forEach(role -> appliedRolesDs.addItem(role));
                appliedGroupTextField.setValue(dto.getGroup() == null ? StringUtils.EMPTY : dto.getGroup().getName());
            }
        }

    }
}