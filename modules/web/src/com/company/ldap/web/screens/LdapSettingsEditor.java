package com.company.ldap.web.screens;

import com.company.ldap.config.LdapConfig;
import com.company.ldap.service.LdapConnectionTesterService;
import com.company.ldap.service.LdapUserService;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.TextArea;
import com.haulmont.cuba.gui.components.TextField;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

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
    private TextField passwordField;

    @Named("connectionStatus")
    private TextArea connectionStatusTextArea;

    @Named("loginAttribute")
    private TextField loginAttributeField;

    @Named("emailAttribute")
    private TextField emailAttributeField;

    @Named("snAttribute")
    private TextField snAttributeField;

    @Named("cnAttribute")
    private TextField cnAttributeField;

    @Named("memberOfAttribute")
    private TextField memberOfAttributeField;

    @Named("userBase")
    private TextField userBaseField;

    @Inject
    private LdapUserService ldapUserService;

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

        ldapUserService.find("");
    }

    public void onTestConnectionClick() {
        String contextSourceUrl = urlField.getValue();
        String contextSourceBase = baseField.getValue();
        String contextSourceUserName = userField.getValue() == null ? StringUtils.EMPTY : userField.getValue();
        String contextSourcePassword = passwordField.getValue() == null ? StringUtils.EMPTY : passwordField.getValue();

        String result = ldapConnectionTester.testConnection(contextSourceUrl, contextSourceBase, contextSourceUserName, contextSourcePassword);
        connectionStatusTextArea.setValue(result);
    }

    public void onSaveSchemaSettingsClick() {
        String loginAttribute = loginAttributeField.getValue();
        String emailAttribute = emailAttributeField.getValue();
        String snAttribute = snAttributeField.getValue();
        String cnAttribute = cnAttributeField.getValue();
        String memberOfAttribute = memberOfAttributeField.getValue();
        String userBase = userBaseField.getValue();

        ldapConfig.setLoginAttribute(loginAttribute);
        ldapConfig.setEmailAttribute(emailAttribute);
        ldapConfig.setSnAttribute(snAttribute);
        ldapConfig.setCnAttribute(cnAttribute);
        ldapConfig.setMemberOfAttribute(memberOfAttribute);
        ldapConfig.setUserBase(userBase);
    }
}