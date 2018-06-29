package com.haulmont.addon.ldap.service;

import com.haulmont.addon.ldap.dto.GroovyScriptTestResultDto;
import com.haulmont.addon.ldap.entity.LdapConfig;

import java.util.List;

public interface LdapService {

    String NAME = "ldap_LdapConnectionTesterService";

    /**
     * Tests connection to LDAP server.
     */
    String testConnection();

    /**
     * Loads attributes of provided classes from the LDAP schema
     */
    void fillLdapUserAttributes(String schemaBase, String objectClasses, String objectClassName, String attributeClassName);

    /**
     * Returns a list of LDAP attributes loaded using {@link #fillLdapUserAttributes(String, String, String, String)}
     */
    List<String> getLdapUserAttributesNames();

    /**
     * Tests a groovy script
     */
    GroovyScriptTestResultDto testGroovyScript(String groovyScript, String login);

    /**
     * Returns a configuration of the LDAP addon
     */
    LdapConfig getLdapConfig();

}
