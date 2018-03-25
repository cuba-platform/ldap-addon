package com.haulmont.addon.ldap.service;

import com.haulmont.addon.ldap.dto.GroovyScriptTestResultDto;
import com.haulmont.addon.ldap.dto.LdapContextDto;
import com.haulmont.addon.ldap.entity.LdapConfig;

import java.util.List;

public interface LdapService {

    String NAME = "ldap_LdapConnectionTesterService";

    String testConnection();

    void fillLdapUserAttributes(String schemaBase, String objectClasses, String objectClassName, String attributeClassName);

    List<String> getLdapUserAttributesNames();

    GroovyScriptTestResultDto testGroovyScript(String groovyScript, String login);

    LdapConfig getLdapConfig();

    LdapContextDto getLdapContextConfig();
}
