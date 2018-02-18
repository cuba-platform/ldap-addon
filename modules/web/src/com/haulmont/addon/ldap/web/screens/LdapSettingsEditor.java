package com.haulmont.addon.ldap.web.screens;

import com.haulmont.addon.ldap.config.LdapConfig;
import com.haulmont.addon.ldap.service.LdapConnectionTesterService;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.PasswordField;
import com.haulmont.cuba.gui.components.TextArea;
import com.haulmont.cuba.gui.components.TextField;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

import static com.haulmont.cuba.gui.components.Frame.NotificationType.HUMANIZED;
import static com.haulmont.cuba.gui.components.Frame.NotificationType.WARNING;

public class LdapSettingsEditor extends AbstractWindow {

    @Inject
    private LdapConfig ldapConfig;

    @Named("url")
    private TextField urlField;

    @Named("base")
    private TextField baseField;

    @Named("user")
    private TextField userField;

    @Named("password")
    private PasswordField passwordField;

    @Named("loginAttribute")
    private TextField loginAttributeField;

    @Named("emailAttribute")
    private TextField emailAttributeField;

    @Named("snAttribute")
    private TextField snAttributeField;

    @Named("cnAttribute")
    private TextField cnAttributeField;

    @Named("languageAttribute")
    private TextField languageAttributeField;

    @Named("positionAttribute")
    private TextField positionAttributeField;

    @Named("memberOfAttribute")
    private TextField memberOfAttributeField;

    @Named("ouAttribute")
    private TextField ouAttributeAttributeField;

    @Named("accessGroupAttribute")
    private TextField accessGroupAttributeField;

    @Named("accountExpiresAttribute")
    private TextField accountExpiresAttributeField;

    @Named("userBase")
    private TextField userBaseField;

    @Inject
    private LdapConnectionTesterService ldapConnectionTester;

    @Override
    public void init(Map<String, Object> params) {
        urlField.setValue(ldapConfig.getContextSourceUrl());
        baseField.setValue(ldapConfig.getContextSourceBase());
        userField.setValue(ldapConfig.getContextSourceUserName());
        passwordField.setValue(ldapConfig.getContextSourcePassword());

        loginAttributeField.setValue(ldapConfig.getLoginAttribute());
        emailAttributeField.setValue(ldapConfig.getEmailAttribute());
        snAttributeField.setValue(ldapConfig.getSnAttribute());
        cnAttributeField.setValue(ldapConfig.getCnAttribute());
        memberOfAttributeField.setValue(ldapConfig.getMemberOfAttribute());
        userBaseField.setValue(ldapConfig.getUserBase());
        languageAttributeField.setValue(ldapConfig.getLanguageAttribute());
        positionAttributeField.setValue(ldapConfig.getPositionAttribute());
        ouAttributeAttributeField.setValue(ldapConfig.getOuAttribute());
        accessGroupAttributeField.setValue(ldapConfig.getAccessGroupAttribute());
        accountExpiresAttributeField.setValue(ldapConfig.getInactiveUserAttribute());
    }

    public void onSaveConnectionSettingsClick() {
        String contextSourceUrl = urlField.getValue();
        String contextSourceBase = baseField.getValue();
        String contextSourceUserName = userField.getValue();
        String contextSourcePassword = passwordField.getValue();

        ldapConfig.setContextSourceUrl(contextSourceUrl == null ? StringUtils.EMPTY : contextSourceUrl);
        ldapConfig.setContextSourceBase(contextSourceBase == null ? StringUtils.EMPTY : contextSourceBase);
        ldapConfig.setContextSourceUserName(contextSourceUserName == null ? StringUtils.EMPTY : contextSourceUserName);
        ldapConfig.setContextSourcePassword(contextSourcePassword == null ? StringUtils.EMPTY : contextSourcePassword);

    }

    public void onTestConnectionClick() {
        String contextSourceUrl = urlField.getValue();
        String contextSourceBase = baseField.getValue();
        String contextSourceUserName = userField.getValue() == null ? StringUtils.EMPTY : userField.getValue();
        String contextSourcePassword = passwordField.getValue() == null ? StringUtils.EMPTY : passwordField.getValue();

        String result = ldapConnectionTester.testConnection(contextSourceUrl, contextSourceBase, contextSourceUserName, contextSourcePassword);

        if ("SUCCESS".equals(result)) {
            showNotification(getMessage("settingsScreenConnectionSuccessCaption"), getMessage("settingsScreenConnectionSuccessMsg"), HUMANIZED);
        } else {
            showNotification(getMessage("settingsScreenConnectionErrorCaption"), result, WARNING);
        }

    }

    public void onSaveSchemaSettingsClick() {
        String loginAttribute = loginAttributeField.getValue();
        String emailAttribute = emailAttributeField.getValue();
        String snAttribute = snAttributeField.getValue();
        String cnAttribute = cnAttributeField.getValue();
        String memberOfAttribute = memberOfAttributeField.getValue();
        String userBase = userBaseField.getValue();
        String language = languageAttributeField.getValue();
        String position = positionAttributeField.getValue();
        String ou = ouAttributeAttributeField.getValue();
        String accessGroup = accessGroupAttributeField.getValue();
        String accountExpires = accountExpiresAttributeField.getValue();

        ldapConfig.setLoginAttribute(loginAttribute);
        ldapConfig.setEmailAttribute(emailAttribute);
        ldapConfig.setSnAttribute(snAttribute);
        ldapConfig.setCnAttribute(cnAttribute);
        ldapConfig.setMemberOfAttribute(memberOfAttribute);
        ldapConfig.setUserBase(userBase);
        ldapConfig.setLanguageAttribute(language);
        ldapConfig.setPositionAttribute(position);
        ldapConfig.setOuAttribute(ou);
        ldapConfig.setAccessGroupAttribute(accessGroup);
        ldapConfig.setInactiveUserAttribute(accountExpires);
    }
}