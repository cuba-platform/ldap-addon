package com.haulmont.addon.ldap.web.scriptingmatchingrule;

import com.google.common.base.Strings;
import com.haulmont.addon.ldap.dto.GroovyScriptTestResultDto;
import com.haulmont.addon.ldap.entity.ScriptingMatchingRule;
import com.haulmont.addon.ldap.service.LdapService;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.entity.Role;

import javax.inject.Inject;
import javax.inject.Named;

import static com.haulmont.addon.ldap.dto.GroovyScriptTestResult.*;
import static com.haulmont.cuba.gui.components.Frame.NotificationType.WARNING;
import static com.haulmont.cuba.gui.components.Frame.NotificationType.WARNING_HTML;

public class ScriptingMatchingRuleEdit extends AbstractEditor<ScriptingMatchingRule> {

    @Named("userLoginTextField")
    private TextField userLoginTextField;

    @Inject
    private LdapService ldapService;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private Table<Role> rolesTable;

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        if (rolesTable.getDatasource().getItems().isEmpty()) {
            errors.add(rolesTable, getMessage("validationEmptyRoles"));
        }
    }

    public void onTestConstraintButtonClick() {
        String login = userLoginTextField.getValue();
        String groovyScript = getItem().getScriptingCondition();
        if (!Strings.isNullOrEmpty(login) && !Strings.isNullOrEmpty(groovyScript)) {
            GroovyScriptTestResultDto result = ldapService.testGroovyScript(groovyScript, login);
            if (NO_USER.equals(result.getResult())) {
                showNotification(getMessage("notificationError"), formatMessage("testGroovyScriptResultNoUser", login), WARNING);
            } else if (COMPILATION_ERROR.equals(result.getResult())) {
                showNotification(getMessage("notificationError"), formatMessage("testGroovyScriptResultCompilationError",
                        result.getErrorText()), WARNING_HTML);
            } else if (OTHER_ERROR.equals(result.getResult())) {
                showNotification(getMessage("notificationError"), formatMessage("testGroovyScriptResultOtherError",
                        result.getErrorText()), WARNING_HTML);
            } else if (NON_BOOLEAN_RESULT.equals(result.getResult())) {
                showNotification(getMessage("notificationError"), formatMessage("testGroovyScriptResultNonBoolean"), WARNING);
            } else if (FALSE.equals(result.getResult())) {
                showNotification(getMessage("notificationSuccess"), formatMessage("testGroovyScriptResultFalse"), WARNING);
            } else if (TRUE.equals(result.getResult())) {
                showNotification(getMessage("notificationSuccess"), formatMessage("testGroovyScriptResultTrue"), WARNING);
            }
        }
    }

    public Component generateScriptingConditionField(Datasource datasource, String fieldId) {
        FlowBoxLayout fb = componentsFactory.createComponent(FlowBoxLayout.class);
        LinkButton lb = componentsFactory.createComponent(LinkButton.class);
        Action action = new EmptyGroovyScriptHelpAction("") {
            @Override
            public void actionPerform(Component component) {
                showMessageDialog(getMessage("groovyScriptConditionTitle"), getMessage("groovyScriptCondition"),
                        MessageType.CONFIRMATION_HTML
                                .modal(false)
                                .width("600px"));
            }
        };
        lb.setAction(action);
        SourceCodeEditor sourceCodeEditor = componentsFactory.createComponent(SourceCodeEditor.class);
        sourceCodeEditor.setDatasource(datasource, fieldId);
        sourceCodeEditor.setRequired(true);
        sourceCodeEditor.setMode(SourceCodeEditor.Mode.Groovy);
        sourceCodeEditor.setWidth("97%");
        fb.add(sourceCodeEditor);
        fb.add(lb);
        return fb;
    }
}