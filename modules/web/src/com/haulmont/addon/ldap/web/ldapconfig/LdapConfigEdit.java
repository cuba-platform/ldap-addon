package com.haulmont.addon.ldap.web.ldapconfig;

import com.haulmont.addon.ldap.config.LdapContextConfig;
import com.haulmont.addon.ldap.dto.LdapContextDto;
import com.haulmont.addon.ldap.entity.LdapUserAttribute;
import com.haulmont.addon.ldap.service.LdapService;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.addon.ldap.entity.LdapConfig;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.CollectionPropertyDatasourceImpl;
import com.haulmont.cuba.web.gui.components.WebTextField;

import javax.inject.Inject;
import javax.inject.Named;

import java.util.Map;
import java.util.UUID;

import static com.haulmont.cuba.gui.components.Frame.NotificationType.HUMANIZED;
import static com.haulmont.cuba.gui.components.Frame.NotificationType.WARNING;

public class LdapConfigEdit extends AbstractEditor<LdapConfig> {

    @Inject
    private LdapService ldapService;

    @Named("ldapUserAttributesDs")
    private CollectionDatasource<LdapUserAttribute, UUID> ldapUserAttributesDs;

    @Named("ldapConfigDs")
    private Datasource<LdapConfig> ldapConfigDs;

    public void onTestConnectionClick() {
        String result = ldapService.testConnection();

        if ("SUCCESS".equals(result)) {
            showNotification(getMessage("settingsScreenConnectionSuccessCaption"), getMessage("settingsScreenConnectionSuccessMsg"), HUMANIZED);
        } else {
            showNotification(getMessage("settingsScreenConnectionErrorCaption"), result, WARNING);
        }
    }

    public void onUpdateLdapSchemaUserAttributesButtonClick() {
        LdapConfig lc = getItem();
        ldapService.fillLdapUserAttributes(lc.getSchemaBase(), lc.getLdapUserObjectClasses(), lc.getObjectClassPropertyName(), lc.getAttributePropertyNames());
        ldapUserAttributesDs.refresh();
    }


    @Override
    protected void postInit() {
        super.postInit();
        LdapConfig lc = ldapService.getLdapConfig();
        ldapConfigDs.setItem(lc);
    }
}