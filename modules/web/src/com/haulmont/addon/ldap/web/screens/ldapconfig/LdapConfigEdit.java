/*
 * Copyright (c) 2008-2022 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.addon.ldap.web.screens.ldapconfig;

import com.haulmont.addon.ldap.config.LdapPropertiesConfig;
import com.haulmont.addon.ldap.dto.TestUserSynchronizationDto;
import com.haulmont.addon.ldap.entity.*;
import com.haulmont.addon.ldap.service.LdapService;
import com.haulmont.addon.ldap.service.MatchingRuleService;
import com.haulmont.addon.ldap.service.TenantProviderService;
import com.haulmont.addon.ldap.service.UserSynchronizationService;
import com.haulmont.addon.ldap.utils.MatchingRuleUtils;
import com.haulmont.addon.ldap.web.scriptingmatchingrule.ScriptingMatchingRuleEdit;
import com.haulmont.addon.ldap.web.simplematchingrule.SimpleMatchingRuleEdit;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.EntityStates;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.Dialogs;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.CollectionChangeType;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionPropertyContainer;
import com.haulmont.cuba.gui.model.DataContext;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.Role;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.haulmont.addon.ldap.entity.MatchingRuleType.CUSTOM;
import static com.haulmont.addon.ldap.entity.MatchingRuleType.DEFAULT;

@UiController("ldap$LdapPropertiesConfig.edit")
@UiDescriptor("ldap-config-edit.xml")
@EditedEntityContainer("ldapConfigDs")
public class LdapConfigEdit extends StandardEditor<LdapConfig> {

    private final static Integer DEFAULT_RULE_ORDER = 0;

    @Inject
    private LdapService ldapService;
    @Inject
    private MessageBundle messageBundle;
    @Inject
    private Notifications notifications;
    @Inject
    private EntityStates entityStates;
    @Inject
    private LookupField<String> tenantField;
    @Inject
    private TextField<String> loginAttributeField;
    @Inject
    private TextField<String> cnAttributeField;
    @Inject
    private TextField<String> givenNameAttributeField;
    @Inject
    private TextField<String> middleNameAttributeField;
    @Inject
    private TextField<String> snAttributeField;
    @Inject
    private TextField<String> emailAttributeField;
    @Inject
    private TextField<String> memberOfAttributeField;
    @Inject
    private TextField<String> accessGroupAttributeField;
    @Inject
    private TextField<String> positionAttributeField;
    @Inject
    private TextField<String> languageAttributeField;
    @Inject
    private TextField<String> ouAttributeField;
    @Inject
    private TextField<String> inactiveUserAttributeField;
    @Inject
    private TextField<String> userBaseField;
    @Inject
    private TextField<String> defaultAccessGroupNameField;
    @Inject
    private TextField<String> schemaBaseField;
    @Inject
    private TextField<String> ldapUserObjectClassesField;
    @Inject
    private TextField<String> objectClassPropertyNameField;
    @Inject
    private TextField<String> attributePropertyNamesField;
    @Inject
    private CollectionContainer<AbstractCommonMatchingRule> abstractMatchingRulesDs;
    @Inject
    private CollectionContainer<AbstractCommonMatchingRule> appliedMatchingRulesDs;
    @Inject
    private CollectionContainer<Role> appliedRolesDs;
    @Inject
    private MatchingRuleService matchingRuleService;
    @Inject
    private TenantProviderService tenantProviderService;
    @Inject
    private UiComponents uiComponents;
    @Inject
    private Table<AbstractCommonMatchingRule> matchingRuleTable;
    @Inject
    private MatchingRuleUtils matchingRuleUtils;
    @Inject
    private TextField<String> testRuleScreenLogin;
    @Inject
    private TextField<String> testRuleScreenAppliedGroup;
    @Inject
    private UserSynchronizationService userSynchronizationService;
    @Inject
    private Dialogs dialogs;
    @Inject
    private LdapPropertiesConfig ldapPropertiesConfig;
    @Inject
    private ScreenBuilders screenBuilders;
    @Inject
    private CollectionPropertyContainer<LdapUserAttribute> ldapUserAttributesDs;
    @Inject
    private Metadata metadata;
    @Inject
    private DataManager dataManager;

    private List<AbstractCommonMatchingRule> matchingRulesToDelete = new ArrayList<>();

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        getScreenData().loadAll();
        tenantField.setOptionsList(tenantProviderService.getTenantIds());
        abstractMatchingRulesDs.setItems(matchingRuleService.getMatchingRules(getEditedEntity().getId()));
        if (entityStates.isNew(getEditedEntity())) {
            setUpDefaultLdapConfigSettings();
            createDefaultMatchingRule();
            setModifiedAfterOpen(false);
        }
    }

    private void setUpDefaultLdapConfigSettings() {
        schemaBaseField.setValue(ldapPropertiesConfig.getSchemaBase());
        defaultAccessGroupNameField.setValue(ldapPropertiesConfig.getDefaultAccessGroupName());
        ldapUserObjectClassesField.setValue(ldapPropertiesConfig.getLdapUserObjectClasses());
        objectClassPropertyNameField.setValue(ldapPropertiesConfig.getObjectClassPropertyName());
        attributePropertyNamesField.setValue(ldapPropertiesConfig.getAttributePropertyNames());
        loginAttributeField.setValue(ldapPropertiesConfig.getLoginAttribute());
        emailAttributeField.setValue(ldapPropertiesConfig.getEmailAttribute());
        cnAttributeField.setValue(ldapPropertiesConfig.getCnAttribute());
        snAttributeField.setValue(ldapPropertiesConfig.getSnAttribute());
        givenNameAttributeField.setValue(ldapPropertiesConfig.getGivenNameAttribute());
        middleNameAttributeField.setValue(ldapPropertiesConfig.getMiddleNameAttribute());
        memberOfAttributeField.setValue(ldapPropertiesConfig.getMemberOfAttribute());
        accessGroupAttributeField.setValue(ldapPropertiesConfig.getAccessGroupAttribute());
        positionAttributeField.setValue(ldapPropertiesConfig.getPositionAttribute());
        ouAttributeField.setValue(ldapPropertiesConfig.getOuAttribute());
        languageAttributeField.setValue(ldapPropertiesConfig.getLanguageAttribute());
        inactiveUserAttributeField.setValue(ldapPropertiesConfig.getInactiveUserAttribute());
        userBaseField.setValue(ldapPropertiesConfig.getUserBase());
    }

    private void createDefaultMatchingRule() {
        DefaultMatchingRule defaultMatchingRule = metadata.create(DefaultMatchingRule.class);
        MatchingRuleOrder matchingRuleOrder = metadata.create(MatchingRuleOrder.class);
        matchingRuleOrder.setOrder(2147483647);
        defaultMatchingRule.setOrder(matchingRuleOrder);
        defaultMatchingRule.setStatus(metadata.create(MatchingRuleStatus.class));
        defaultMatchingRule.setRuleType(DEFAULT);
        defaultMatchingRule.setDescription("Default rule");
        defaultMatchingRule.setRolesList("Default LDAP role");
        defaultMatchingRule.setLdapConfig(getEditedEntity());

        defaultMatchingRule.setAccessGroup(getDefaultAccessGroup());
        defaultMatchingRule.setAccessGroupName("Company");

        abstractMatchingRulesDs.setItems(Collections.singletonList(defaultMatchingRule));
    }

    private Group getDefaultAccessGroup() {
        return dataManager.load(Group.class)
                .query("select g from sec$Group g where g.name = :name")
                .parameter("name", "Company")
                .one();
    }

    public void onTestConnectionClick() {
        LdapConfig ldapConfig = getEditedEntity();
        String result = ldapService.testConnection(
                ldapConfig.getContextSourceUrl(),
                ldapConfig.getContextSourceBase(),
                ldapConfig.getContextSourceUserName(),
                ldapConfig.getContextSourcePassword()
        );

        if ("SUCCESS".equals(result)) {
            notifications.create(Notifications.NotificationType.HUMANIZED)
                    .withCaption(messageBundle.getMessage("settingsScreenConnectionSuccessCaption"))
                    .withDescription(messageBundle.getMessage("settingsScreenConnectionSuccessMsg"))
                    .show();
        } else {
            notifications.create(Notifications.NotificationType.WARNING)
                    .withCaption(messageBundle.getMessage("settingsScreenConnectionErrorCaption"))
                    .withDescription(result)
                    .show();
        }
    }

    @Subscribe("popupBtn.createSimpleRule")
    public void onPopupBtnCreateSimpleRule(Action.ActionPerformedEvent event) {
        SimpleMatchingRule simpleMatchingRule = metadata.create(SimpleMatchingRule.class);
        simpleMatchingRule.setOrder(metadata.create(MatchingRuleOrder.class));
        simpleMatchingRule.setStatus(metadata.create(MatchingRuleStatus.class));
        simpleMatchingRule.setLdapConfig(getEditedEntity());
        screenBuilders.editor(SimpleMatchingRule.class, this)
                .withScreenClass(SimpleMatchingRuleEdit.class)
                .withOpenMode(OpenMode.NEW_TAB)
                .editEntity(simpleMatchingRule)
                .withParentDataContext(getScreenData().getDataContext())
                .withAfterCloseListener(e -> {
                    if (e.getCloseAction() == WINDOW_COMMIT_AND_CLOSE_ACTION) {
                        Set<AbstractCommonMatchingRule> matchingRules = new LinkedHashSet<>(abstractMatchingRulesDs.getItems());
                        matchingRules.add(e.getScreen().getEditedEntity());
                        abstractMatchingRulesDs.setItems(matchingRules);
                    }
                })
                .build()
                .show();
    }

    @Subscribe("popupBtn.createScriptingRule")
    public void onPopupBtnCreateScriptingRule(Action.ActionPerformedEvent event) {
        ScriptingMatchingRule scriptingMatchingRule = metadata.create(ScriptingMatchingRule.class);
        scriptingMatchingRule.setOrder(metadata.create(MatchingRuleOrder.class));
        scriptingMatchingRule.setStatus(metadata.create(MatchingRuleStatus.class));
        scriptingMatchingRule.setLdapConfig(getEditedEntity());
        screenBuilders.editor(ScriptingMatchingRule.class, this)
                .withScreenClass(ScriptingMatchingRuleEdit.class)
                .withOpenMode(OpenMode.NEW_TAB)
                .editEntity(scriptingMatchingRule)
                .withParentDataContext(getScreenData().getDataContext())
                .withAfterCloseListener(e -> {
                    if (e.getCloseAction() == WINDOW_COMMIT_AND_CLOSE_ACTION) {
                        Set<AbstractCommonMatchingRule> matchingRules = new LinkedHashSet<>(abstractMatchingRulesDs.getItems());
                        matchingRules.add(e.getScreen().getEditedEntity());
                        abstractMatchingRulesDs.setItems(matchingRules);
                    }
                })
                .build()
                .show();
    }

    public void onUpdateLdapSchemaUserAttributesButtonClick() {
        dialogs.createOptionDialog(Dialogs.MessageType.CONFIRMATION)
                .withCaption(messageBundle.getMessage("refreshAttributesFromLdapTitle"))
                .withMessage(messageBundle.getMessage("refreshAttributesFromLdap"))
                .withActions(new DialogAction(DialogAction.Type.YES) {
                                 public void actionPerform(Component component) {
                                     List<LdapUserAttribute> luas = new ArrayList<>();
                                     for (String attribute : ldapService.getLdapUserAttributes(getEditedEntity())) {
                                         LdapUserAttribute ldapUserAttribute = metadata.create(LdapUserAttribute.class);
                                         ldapUserAttribute.setAttributeName(attribute);
                                         ldapUserAttribute.setLdapConfig(getEditedEntity());
                                         luas.add(ldapUserAttribute);
                                     }
                                     ldapUserAttributesDs.setItems(luas);
                                 }
                             },
                        new DialogAction(DialogAction.Type.NO))
                .show();
    }

    public Component generateMatchingRuleTableStatusColumnCell(AbstractCommonMatchingRule entity) {
        CheckBox checkBox = uiComponents.create(CheckBox.class);
        checkBox.setValue(entity.getStatus().getIsActive());
        checkBox.addValueChangeListener(e -> {
            AbstractCommonMatchingRule mr = matchingRuleTable.getSingleSelected();
            Boolean value = e.getValue();
            mr.getStatus().setIsActive(value);
        });
        if ((DEFAULT == entity.getRuleType())) {
            checkBox.setEditable(false);
            checkBox.setEnabled(false);
        }

        return checkBox;
    }

    public Component generateMatchingRuleTableOrderColumnCell(AbstractCommonMatchingRule entity) {
        TextField<String> textField = uiComponents.create(TextField.TYPE_DEFAULT);
        textField.setValue(matchingRuleUtils.generateMatchingRuleTableOrderColumn(entity));
        textField.setWidth("50");
        if (DEFAULT == entity.getRuleType()) {
            textField.setEditable(false);
            textField.setEnabled(false);
            textField.setValue(messageBundle.getMessage("matchingRuleTableDefaultRuleOrder"));
        }
        textField.addValueChangeListener(e -> {
            String order = e.getValue();
            if (!matchingRuleUtils.validateRuleOrder(Integer.valueOf(order))) {
                textField.setValue(e.getPrevValue());
                return;
            }
            AbstractCommonMatchingRule mr = matchingRuleTable.getSingleSelected();
            MatchingRuleOrder matchingRuleOrder = mr.getOrder();
            matchingRuleOrder.setOrder(Integer.valueOf(order));
            // TODO: 28.03.2022
            sortDsByOrder();
        });

        return textField;
    }

    public Component generateMatchingRuleTableTerminalColumnCell(AbstractCommonMatchingRule entity) {
        CheckBox checkBox = uiComponents.create(CheckBox.class);
        if (!(CUSTOM == entity.getRuleType())) {
            AbstractDbStoredMatchingRule dbRule = (AbstractDbStoredMatchingRule) entity;
            checkBox.setValue(dbRule.getIsTerminalRule());
            checkBox.addValueChangeListener(e -> {
                AbstractDbStoredMatchingRule mr = (AbstractDbStoredMatchingRule) matchingRuleTable.getSingleSelected();
                Boolean value = (Boolean) e.getValue();
                mr.setIsTerminalRule(value);
            });
        } else {
            checkBox.setEditable(false);
            checkBox.setEnabled(false);
        }

        return checkBox;
    }

    public Component generateMatchingRuleTableDescriptionColumnCell(AbstractCommonMatchingRule entity) {
        return new Table.PlainTextCell(matchingRuleUtils.generateMatchingRuleTableDescriptionColumn(entity));
    }

    public Component generateMatchingRuleTableTypeColumnCell(AbstractCommonMatchingRule entity) {
        return new Table.PlainTextCell(messageBundle.getMessage(entity.getRuleType().name()));
    }

    public Component generateMatchingRuleTableCubaColumnCell(AbstractCommonMatchingRule entity) {
        return new Table.PlainTextCell(matchingRuleUtils.generateMatchingRuleRolesAccessGroupColumn(entity));
    }

    public Component generateMatchingRuleTableOptionsColumnCell(AbstractCommonMatchingRule entity) {
        return new Table.PlainTextCell(matchingRuleUtils.generateMatchingRuleOptionsColumn(entity));
    }

    public Component generateMatchingRuleTableConditionColumnCell(AbstractCommonMatchingRule entity) {
        return new Table.PlainTextCell(matchingRuleUtils.generateMatchingRuleTableConditionColumn(entity));
    }

    public void onUpClick() {
        changeOrderClick("UP");
    }

    public void onDownClick() {
        changeOrderClick("DOWN");
    }

    private void changeOrderClick(String direction) {
//        AbstractCommonMatchingRule selected = matchingRuleTable.getSingleSelected();
//        if (selected == null) return;
//        int selectedOrder = selected.getOrder().getOrder();
//        int neighbourElementPosition = direction.equals("UP") ? -1 : 1;
//        List<AbstractCommonMatchingRule> items = matchingRuleService.getMatchingRulesGui();
//        int i = 0;
//        for (AbstractCommonMatchingRule acmr : items) {
//            if (acmr == selected) {
//                if ((i == 0 && direction.equals("UP")) || DEFAULT == selected.getRuleType()) return;
//                AbstractCommonMatchingRule neighbourElement = items.get(i + neighbourElementPosition);
//                if (DEFAULT == neighbourElement.getRuleType()) return;
//                selected.getOrder().setOrder(neighbourElement.getOrder().getOrder());
//                neighbourElement.getOrder().setOrder(selectedOrder);
//                sortDsByOrder();
//            }
//            i++;
//        }
    }

    private void sortDsByOrder() {
        // TODO: 28.03.2022 fixme
//        List<CollectionDatasource.Sortable.SortInfo> sorts = new ArrayList<>(1);
//        MetaPropertyPath orderPath = matchingRuleDatasource.getMetaClass().getPropertyPath("order.order");
//        CollectionDatasource.Sortable.SortInfo<MetaPropertyPath> sortInfo = new CollectionDatasource.Sortable.SortInfo<>();
//        sortInfo.setPropertyPath(orderPath);
//        sortInfo.setOrder(CollectionDatasource.Sortable.Order.ASC);
//        sorts.add(sortInfo);
//        matchingRuleDatasource.sort(sorts.toArray(new CollectionDatasource.Sortable.SortInfo[1]));
    }

    public void onTestRuleScreenTestButtonClick() {
        String login = testRuleScreenLogin.getValue();
        if (StringUtils.isNotEmpty(login)) {
            appliedMatchingRulesDs.getMutableItems().clear();
            appliedRolesDs.getMutableItems().clear();
            testRuleScreenAppliedGroup.setValue(StringUtils.EMPTY);

            TestUserSynchronizationDto dto =
                    userSynchronizationService.testUserSynchronization(login, getEditedEntity().getSysTenantId(), abstractMatchingRulesDs.getItems());
            if (!dto.isUserExistsInLdap()) {
                notifications.create(Notifications.NotificationType.HUMANIZED)
                        .withCaption(messageBundle.getMessage("testRuleScreenUserNotInLdapCaption"))
                        .withDescription(messageBundle.getMessage("testRuleScreenUserNotInLdap"))
                        .show();
            } else {
                appliedMatchingRulesDs.setItems(dto.getAppliedMatchingRules());
                appliedRolesDs.setItems(dto.getAppliedCubaRoles());
                testRuleScreenAppliedGroup.setValue(dto.getGroup() == null ? StringUtils.EMPTY : dto.getGroup().getName());
            }
        }
    }

    public Component generateTestMatchingRuleTableTypeColumnCell(AbstractCommonMatchingRule entity) {
        return new Table.PlainTextCell(entity.getRuleType().name());
    }

    public Component generateTestMatchingRuleTableOptionsColumnCell(AbstractCommonMatchingRule entity) {
        return new Table.PlainTextCell(matchingRuleUtils.generateMatchingRuleOptionsColumn(entity));
    }

    private boolean validateMatchingRulesOrder(List<AbstractCommonMatchingRule> matchingRules) {
        boolean result = true;
        Optional<AbstractCommonMatchingRule> defaultOrder = matchingRules.stream()
                .filter(mr -> DEFAULT_RULE_ORDER.equals(mr.getOrder().getOrder()))
                .findAny();
        if (defaultOrder.isPresent()) {
            result = false;
            notifications.create(Notifications.NotificationType.HUMANIZED)
                    .withCaption(messageBundle.getMessage("matchingRuleScreenEmptyOrderCaption"))
                    .withDescription(messageBundle.getMessage("matchingRuleScreenEmptyOrder"))
                    .show();
        }

        Map<Integer, Long> countMap = matchingRules.stream()
                .collect(Collectors.groupingBy(mr -> mr.getOrder().getOrder(), Collectors.counting()));
        for (Map.Entry<Integer, Long> entry : countMap.entrySet()) {
            if (entry.getValue() > 1) {
                result = false;
                notifications.create(Notifications.NotificationType.HUMANIZED)
                        .withCaption(messageBundle.getMessage("matchingRuleScreenDuplicationOrderCaption"))
                        .withDescription(messageBundle.getMessage("matchingRuleScreenDuplicationOrder"))
                        .show();
                break;
            }
        }

        return result;
    }

    @Subscribe(id = "abstractMatchingRulesDs", target = Target.DATA_CONTAINER)
    public void onAbstractMatchingRulesDsCollectionChange(CollectionContainer.CollectionChangeEvent<AbstractCommonMatchingRule> event) {
        if (event.getChangeType() == CollectionChangeType.REMOVE_ITEMS) {
            matchingRulesToDelete = new ArrayList<>(event.getChanges());
        }
    }

    @Install(to = "matchingRuleTable.remove", subject = "enabledRule")
    private boolean matchingRuleTableRemoveEnabledRule() {
        AbstractCommonMatchingRule selected = matchingRuleTable.getSingleSelected();
        return selected != null && selected.getRuleType() != DEFAULT;
    }

    @Install(to = "matchingRuleTable.edit", subject = "enabledRule")
    private boolean matchingRuleTableEditEnabledRule() {
        AbstractCommonMatchingRule selected = matchingRuleTable.getSingleSelected();
        return selected != null && selected.getRuleType() != DEFAULT;
    }

    @Subscribe
    public void onBeforeCommitChanges(BeforeCommitChangesEvent event) {
        DataContext dataContext = getScreenData().getDataContext();
        LdapConfig ldapConfig = dataContext.merge(getEditedEntity());
        List<AbstractCommonMatchingRule> matchingRules = abstractMatchingRulesDs.getMutableItems();
        matchingRules.forEach(mr -> {
            if (mr instanceof AbstractDbStoredMatchingRule) {
                ((AbstractDbStoredMatchingRule) mr).setLdapConfig(ldapConfig);
            }
        });
        dataContext.merge(matchingRules);
        matchingRulesToDelete.forEach(dataContext::remove);

        List<LdapUserAttribute> ldapUserAttributes = ldapUserAttributesDs.getMutableItems();
        ldapUserAttributes.forEach(la -> la.setLdapConfig(ldapConfig));
        dataContext.merge(ldapUserAttributes);
    }

}