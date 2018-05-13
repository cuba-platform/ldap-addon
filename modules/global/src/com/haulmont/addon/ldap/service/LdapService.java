package com.haulmont.addon.ldap.service;

import com.haulmont.addon.ldap.dto.GroovyScriptTestResultDto;
import com.haulmont.addon.ldap.dto.LdapContextDto;
import com.haulmont.addon.ldap.entity.LdapConfig;

import java.util.List;

public interface LdapService {

    String NAME = "ldap_LdapConnectionTesterService";

    /**
     * Test connection to LDAP server.
     */
    String testConnection();

    /**
     * Load attributes of provided classes from LDAP schema
     */
    void fillLdapUserAttributes(String schemaBase, String objectClasses, String objectClassName, String attributeClassName);

    /**
     * Returns list of LDAP attributes loaded using {@link #fillLdapUserAttributes(String, String, String, String)}
     */
    List<String> getLdapUserAttributesNames();

    /**
     * Test groovy script
     */
    GroovyScriptTestResultDto testGroovyScript(String groovyScript, String login);

    /**
     * Returns configuration of LDAP addon
     */
    LdapConfig getLdapConfig();

}
