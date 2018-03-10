package com.haulmont.addon.ldap.web.screens;

import com.haulmont.addon.ldap.dto.TestUserSynchronizationDto;
import com.haulmont.addon.ldap.entity.*;
import com.haulmont.addon.ldap.service.MatchingRuleService;
import com.haulmont.addon.ldap.service.UserSynchronizationService;
import com.haulmont.addon.ldap.utils.MatchingRuleUtils;
import com.haulmont.addon.ldap.web.screens.datasources.MatchingRuleDatasource;
import com.haulmont.chile.core.datatypes.Datatypes;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.chile.core.model.MetaPropertyPath;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.haulmont.addon.ldap.entity.MatchingRuleType.DEFAULT;
import static com.haulmont.addon.ldap.entity.MatchingRuleType.CUSTOM;
import static com.haulmont.addon.ldap.entity.MatchingRuleType.SIMPLE;
import static com.haulmont.cuba.gui.components.Frame.NotificationType.HUMANIZED;

public class MatchingRuleScreen extends AbstractWindow {

    @Named("matchingRuleTable")
    private Table<AbstractCommonMatchingRule> matchingRuleTable;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private Metadata metadata;

    @Inject
    private MatchingRuleUtils matchingRuleUtils;

    @Named("abstractMatchingRulesDs")
    private MatchingRuleDatasource matchingRuleDatasource;

    @Named("appliedMatchingRulesDs")
    private CollectionDatasource<AbstractCommonMatchingRule, UUID> appliedMatchingRulesDs;

    @Named("appliedRolesDs")
    private CollectionDatasource<Role, UUID> appliedRolesDs;

    @Named("testRuleScreenLogin")
    private TextField userLoginTextField;

    @Named("testRuleScreenAppliedGroup")
    private TextField appliedGroupTextField;

    @Inject
    private UserSynchronizationService userSynchronizationService;

    @Inject
    private MatchingRuleService matchingRuleService;

    private final static Integer DEFAULT_RULE_ORDER = 0;

    private final static String UP = "UP";

    private final static String DOWN = "DOWN";

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        CollectionDatasource.CollectionChangeListener sortListener = new CollectionDatasource.CollectionChangeListener() {
            @Override
            public void collectionChanged(CollectionDatasource.CollectionChangeEvent e) {
                if (CollectionDatasource.Operation.ADD.equals(e.getOperation())) {
                    Optional<Integer> maxOrder = matchingRuleDatasource.getItems().stream().filter(mr -> !DEFAULT.equals(mr.getRuleType()))
                            .max(Comparator.comparing(mr -> mr.getOrder().getOrder())).map(mr -> mr.getOrder().getOrder());
                    int order = maxOrder.isPresent() ? maxOrder.get() + 1 : 1;
                    Optional<AbstractCommonMatchingRule> ruleWithoutOrder = e.getItems().stream()
                            .filter(mr -> DEFAULT_RULE_ORDER.equals(((AbstractCommonMatchingRule) mr).getOrder().getOrder())).findAny();
                    if (ruleWithoutOrder.isPresent()) {
                        ruleWithoutOrder.get().getOrder().setOrder(order);
                    }
                    sortDsByOrder();
                }
            }
        };
        matchingRuleDatasource.addCollectionChangeListener(sortListener);

        appliedRolesDs.clear();

        EditAction.BeforeActionPerformedHandler customEditBeforeActionPerformedHandler = new EditAction.BeforeActionPerformedHandler() {
            @Override
            public boolean beforeActionPerformed() {
                AbstractCommonMatchingRule rule = matchingRuleTable.getSingleSelected();
                return !CUSTOM.equals(rule.getRuleType());
            }
        };

        EditAction.AfterCommitHandler customAfterCommitHandler = new EditAction.AfterCommitHandler() {
            @Override
            public void handle(Entity entity) {
                AbstractCommonMatchingRule amr = (AbstractCommonMatchingRule) entity;
                if (MatchingRuleType.SIMPLE.equals(amr.getRuleType())) {
                    matchingRuleDatasource.getItems().forEach(mr -> {
                        if (MatchingRuleType.SIMPLE.equals(mr.getRuleType()) && mr.getId().equals(amr.getId())) {
                            ((SimpleMatchingRule) amr).getConditions().forEach(con -> {
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
                AbstractCommonMatchingRule rule = matchingRuleTable.getSingleSelected();
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
                AbstractCommonMatchingRule rule = matchingRuleTable.getSingleSelected();
                return !(CUSTOM.equals(rule.getRuleType()) || MatchingRuleType.DEFAULT.equals(rule.getRuleType()));
            }
        };

        RemoveAction customRemove = new RemoveAction(matchingRuleTable, false);
        customRemove.setBeforeActionPerformedHandler(customRemoveBeforeActionPerformedHandler);

        matchingRuleTable.addAction(customEdit);
        matchingRuleTable.addAction(customRemove);
    }


    public Component generateMatchingRuleTableConditionColumnCell(AbstractCommonMatchingRule entity) {
        return new Table.PlainTextCell(matchingRuleUtils.generateMatchingRuleTableConditionColumn(entity));
    }

    public Component generateMatchingRuleTableCubaColumnCell(AbstractCommonMatchingRule entity) {
        return new Table.PlainTextCell(matchingRuleUtils.generateMatchingRuleRolesAccessGroupColumn(entity));
    }

    public Component generateMatchingRuleTableTypeColumnCell(AbstractCommonMatchingRule entity) {
        return new Table.PlainTextCell(entity.getRuleType().getName());
    }

    public Component generateMatchingRuleTableOptionsColumnCell(AbstractCommonMatchingRule entity) {
        return new Table.PlainTextCell(matchingRuleUtils.generateMatchingRuleOptionsColumn(entity));
    }

    public Component generateMatchingRuleTableStatusColumnCell(AbstractCommonMatchingRule entity) {
        CheckBox checkBox = componentsFactory.createComponent(CheckBox.class);
        checkBox.setValue(entity.getStatus().getIsActive());
        checkBox.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChanged(ValueChangeEvent e) {
                AbstractCommonMatchingRule mr = matchingRuleTable.getSingleSelected();
                Boolean value = (Boolean) e.getValue();
                mr.getStatus().setIsActive(value);
            }
        });
        if ((DEFAULT.equals(entity.getRuleType()))) {
            checkBox.setEditable(false);
            checkBox.setEnabled(false);
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

    private void openCreateRuleWindow(AbstractCommonMatchingRule abstractMatchingRule, String screenName) {
        Map<String, Object> params = new HashMap<>();
        openEditor(screenName, abstractMatchingRule, WindowManager.OpenType.NEW_TAB, params, matchingRuleDatasource);
    }

    public void onCommitButtonClick() {
        if (validateMatchingRulesOrder(new ArrayList<>(matchingRuleDatasource.getItems()))) {
            showOptionDialog(
                    getMessage("matchingRuleScreenCommitDialogTitle"),
                    getMessage("matchingRuleScreenCommitDialogMsg"),
                    MessageType.CONFIRMATION,
                    new Action[]{
                            new DialogAction(DialogAction.Type.YES) {
                                public void actionPerform(Component component) {
                                    List<AbstractCommonMatchingRule> itemsToSave = new ArrayList<>(matchingRuleDatasource.getItems());
                                    List<AbstractCommonMatchingRule> itemsToDelete = new ArrayList<>(matchingRuleDatasource.getItemsToDelete());
                                    matchingRuleService.saveMatchingRules(itemsToSave, itemsToDelete);
                                    matchingRuleDatasource.refresh();
                                }
                            },
                            new DialogAction(DialogAction.Type.NO)
                    }
            );
        }

    }

    public void onCancelButtonClick() {
        showOptionDialog(
                getMessage("matchingRuleScreenCancelDialogTitle"),
                getMessage("matchingRuleScreenCancelDialogMsg"),
                MessageType.CONFIRMATION,
                new Action[]{
                        new DialogAction(DialogAction.Type.YES) {
                            public void actionPerform(Component component) {
                                matchingRuleDatasource.refresh();
                            }
                        },
                        new DialogAction(DialogAction.Type.NO)
                }
        );
    }

    public Component generateTestMatchingRuleTableTypeColumnCell(AbstractCommonMatchingRule entity) {
        return new Table.PlainTextCell(entity.getRuleType().name());
    }

    public Component generateTestMatchingRuleTableOptionsColumnCell(AbstractCommonMatchingRule entity) {
        return new Table.PlainTextCell(matchingRuleUtils.generateMatchingRuleOptionsColumn(entity));
    }

    public Component generateTestMatchingRuleTableResultColumnCell(AbstractCommonMatchingRule entity) {
        return new Table.PlainTextCell(matchingRuleUtils.generateMatchingRuleRolesAccessGroupColumn(entity));
    }

    public Component generateTestMatchingRuleTableConditionColumnCell(AbstractCommonMatchingRule entity) {
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

    public Component generateMatchingRuleTableOrderColumnCell(AbstractCommonMatchingRule entity) {
        TextField textField = componentsFactory.createComponent(TextField.class);
        textField.setValue(matchingRuleUtils.generateMatchingRuleTableOrderColumn(entity));
        textField.setWidth("50");
        if (DEFAULT.equals(entity.getRuleType())) {
            textField.setEditable(false);
            textField.setEnabled(false);
            textField.setValue(getMessage("matchingRuleTableDefaultRuleOrder"));
        } else {
            textField.setDatatype(Datatypes.get(Integer.class));
        }
        textField.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChanged(ValueChangeEvent e) {
                if (!matchingRuleUtils.validateRuleOrder((Integer) e.getValue())) {
                    textField.setValue(e.getPrevValue());
                    return;
                }
                AbstractCommonMatchingRule mr = matchingRuleTable.getSingleSelected();
                Integer order = (Integer) e.getValue();
                MatchingRuleOrder matchingRuleOrder = mr.getOrder();
                matchingRuleOrder.setOrder(order);
                sortDsByOrder();
            }
        });

        return textField;
    }

    private boolean validateMatchingRulesOrder(List<AbstractCommonMatchingRule> matchingRules) {
        boolean result = true;
        Optional<AbstractCommonMatchingRule> defaultOrder = matchingRules.stream().filter(mr -> DEFAULT_RULE_ORDER.equals(mr.getOrder().getOrder())).findAny();
        if (defaultOrder.isPresent()) {
            result = false;
            showNotification(getMessage("matchingRuleScreenEmptyOrderCaption"), getMessage("matchingRuleScreenEmptyOrder"), HUMANIZED);
        }

        Map<Integer, Long> countMap = matchingRules.stream().collect(Collectors.groupingBy(mr -> mr.getOrder().getOrder(), Collectors.counting()));
        for (Map.Entry<Integer, Long> entry : countMap.entrySet()) {
            if (entry.getValue() > 1) {
                result = false;
                showNotification(getMessage("matchingRuleScreenDuplicationOrderCaption"), getMessage("matchingRuleScreenDuplicationOrder"), HUMANIZED);
                break;
            }
        }

        return result;

    }

    public Component generateMatchingRuleTableDescriptionColumnCell(AbstractCommonMatchingRule entity) {
        return new Table.PlainTextCell(matchingRuleUtils.generateMatchingRuleTableDescriptionColumn(entity));
    }

    public void onUpClick() {
        changeOrderClick(UP);
    }

    public void onDownClick() {
        changeOrderClick(DOWN);
    }

    private void sortDsByOrder() {
        List<CollectionDatasource.Sortable.SortInfo> sorts = new ArrayList<>(1);
        MetaPropertyPath orderPath = matchingRuleDatasource.getMetaClass().getPropertyPath("order.order");
        CollectionDatasource.Sortable.SortInfo<MetaPropertyPath> sortInfo = new CollectionDatasource.Sortable.SortInfo<>();
        sortInfo.setPropertyPath(orderPath);
        sortInfo.setOrder(CollectionDatasource.Sortable.Order.ASC);
        sorts.add(sortInfo);
        matchingRuleDatasource.sort(sorts.toArray(new CollectionDatasource.Sortable.SortInfo[1]));
    }

    private void changeOrderClick(String direction) {
        AbstractCommonMatchingRule selected = matchingRuleTable.getSingleSelected();
        if (selected == null) return;
        int selectedOrder = selected.getOrder().getOrder();
        int neighbourElementPosition = direction.equals(UP) ? -1 : 1;
        List<AbstractCommonMatchingRule> items = new ArrayList<>(matchingRuleDatasource.getItems());
        int i = 0;
        for (AbstractCommonMatchingRule acmr : items) {
            if (acmr == selected) {
                if (i == 0 && direction.equals(UP)) return;
                AbstractCommonMatchingRule neighbourElement = items.get(i + neighbourElementPosition);
                if (DEFAULT.equals(selected.getRuleType()) || DEFAULT.equals(neighbourElement.getRuleType())) return;
                selected.getOrder().setOrder(neighbourElement.getOrder().getOrder());
                neighbourElement.getOrder().setOrder(selectedOrder);
                sortDsByOrder();
            }
            i++;
        }
    }
}