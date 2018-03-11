package com.haulmont.addon.ldap.service;

import java.util.List;

public interface LdapService {

    String NAME = "ldap_LdapConnectionTesterService";

    String testConnection(String url, String base, String userDn, String password);

    void fillLdapUserAttributes(String schemaBase, String objectClasses, String metaObjectClassName, String objectClassName, String attributeClassName, String url, String user, String password);

    List<String> getLdapUserAttributesNames();
}
