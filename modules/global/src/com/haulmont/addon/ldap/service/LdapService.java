package com.haulmont.addon.ldap.service;

import com.haulmont.addon.ldap.dto.GroovyScriptTestResult;
import com.haulmont.addon.ldap.dto.GroovyScriptTestResultDto;

import java.util.List;

public interface LdapService {

    String NAME = "ldap_LdapConnectionTesterService";

    String testConnection(String url, String base, String userDn, String password);

    void fillLdapUserAttributes(String schemaBase, String objectClasses, String metaObjectClassName, String objectClassName, String attributeClassName, String url, String user, String password);

    List<String> getLdapUserAttributesNames();

    GroovyScriptTestResultDto testGroovyScript(String groovyScript, String login);
}
