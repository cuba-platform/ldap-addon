package com.haulmont.addon.ldap.web.scriptingmatchingrule;

import com.google.common.base.Strings;
import com.haulmont.addon.ldap.dto.GroovyScriptTestResultDto;
import com.haulmont.addon.ldap.service.LdapService;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.addon.ldap.entity.ScriptingMatchingRule;
import com.haulmont.cuba.gui.components.TextField;

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

    public void onTestConstraintButtonClick() {
        String login = userLoginTextField.getValue();
        String groovyScript = getItem().getScriptingCondition();
        if (!Strings.isNullOrEmpty(login) && !Strings.isNullOrEmpty(groovyScript)) {
            GroovyScriptTestResultDto result = ldapService.testGroovyScript(groovyScript, login);
            if (NO_USER.equals(result.getResult())) {
                showNotification(getMessage("notificationError"), formatMessage("testGroovyScriptResultNoUser", login), WARNING);
            } else if (COMPILATION_ERROR.equals(result.getResult())) {
                showNotification(getMessage("notificationError"), formatMessage("testGroovyScriptResultCompilationError", result.getErrorText()), WARNING_HTML);
            } else if (OTHER_ERROR.equals(result.getResult())) {
                showNotification(getMessage("notificationError"), formatMessage("testGroovyScriptResultOtherError", result.getErrorText()), WARNING_HTML);
            } else if (NON_BOOLEAN_RESULT.equals(result.getResult())) {
                showNotification(getMessage("notificationError"), formatMessage("testGroovyScriptResultNonBoolean"), WARNING);
            } else if (FALSE.equals(result.getResult())) {
                showNotification(getMessage("notificationSuccess"), formatMessage("testGroovyScriptResultFalse"), WARNING);
            } else if (TRUE.equals(result.getResult())) {
                showNotification(getMessage("notificationSuccess"), formatMessage("testGroovyScriptResultTrue"), WARNING);
            }
        }
    }
}