/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.addon.ldap.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.Default;
import com.haulmont.cuba.core.config.defaults.DefaultString;
import com.haulmont.cuba.core.config.type.CommaSeparatedStringListTypeFactory;
import com.haulmont.cuba.core.config.type.Factory;

import java.util.List;

public interface LdapPropertiesConfig extends Config {

    @Source(type = SourceType.APP)
    @Property("ldap.contextSourceUrl")
    String getContextSourceUrl();

    @Source(type = SourceType.APP)
    @Property("ldap.contextSourceBase")
    String getContextSourceBase();

    @Source(type = SourceType.APP)
    @Property("ldap.contextSourceUserName")
    String getContextSourceUserName();

    @Source(type = SourceType.APP)
    @Property("ldap.contextSourcePassword")
    String getContextSourcePassword();

    @Source(type = SourceType.APP)
    @Property("ldap.sessionExpiringPeriodSec")
    int getSessionExpiringPeriodSec();

    @Source(type = SourceType.APP)
    @Property("cuba.web.standardAuthenticationUsers")
    @Factory(factory = CommaSeparatedStringListTypeFactory.class)
    @DefaultString("admin,anonymous")
    List<String> getStandardAuthenticationUsers();

    @Source(type = SourceType.APP)
    @Property("ldap.addonEnabled")
    Boolean getLdapAddonEnabled();

    @Source(type = SourceType.APP)
    @Property("ldap.expiringSessionsEnable")
    Boolean getExpiringSessionsEnable();

    @Source(type = SourceType.APP)
    @Property("ldap.userSynchronizationBatchSize")
    Integer getUserSynchronizationBatchSize();

    @Source(type = SourceType.APP)
    @Property("ldap.userSynchronizationOnlyActiveProperty")
    Boolean getUserSynchronizationOnlyActiveProperty();

    @Source(type = SourceType.APP)
    @Property("ldap.cubaGroupForSynchronization")
    @Factory(factory = CommaSeparatedStringListTypeFactory.class)
    List<String> getCubaGroupForSynchronization();

    @Source(type = SourceType.APP)
    @Property("ldap.cubaGroupForSynchronizationInverse")
    Boolean getCubaGroupForSynchronizationInverse();

    @Source(type = SourceType.APP)
    @Property("ldap.synchronizeCommonInfoFromLdap")
    Boolean getSynchronizeCommonInfoFromLdap();

    @Source(type = SourceType.APP)
    @Property("ldap.schemaBase")
    @Default("CN=Schema,CN=Configuration")
    String getSchemaBase();

    @Source(type = SourceType.APP)
    @Property("ldap.defaultAccessGroupName")
    @Default("Company")
    String getDefaultAccessGroupName();

    @Source(type = SourceType.APP)
    @Property("ldap.ldapUserObjectClasses")
    @Default("person;inetOrgPerson")
    String getLdapUserObjectClasses();

    @Source(type = SourceType.APP)
    @Property("ldap.objectClassPropertyName")
    @Default("CN")
    String getObjectClassPropertyName();

    @Source(type = SourceType.APP)
    @Property("ldap.attributePropertyNames")
    @Default("systemMustContain;systemMayContain;mayContain;MustContain")
    String getAttributePropertyNames();

    @Source(type = SourceType.APP)
    @Property("ldap.loginAttribute")
    @Default("sAMAccountName")
    String getLoginAttribute();

    @Source(type = SourceType.APP)
    @Property("ldap.emailAttribute")
    @Default("mail")
    String getEmailAttribute();

    @Source(type = SourceType.APP)
    @Property("ldap.cnAttribute")
    @Default("cn")
    String getCnAttribute();

    @Source(type = SourceType.APP)
    @Property("ldap.snAttribute")
    @Default("sn")
    String getSnAttribute();

    @Source(type = SourceType.APP)
    @Property("ldap.givenNameAttribute")
    @Default("sn")
    String getGivenNameAttribute();

    @Source(type = SourceType.APP)
    @Property("ldap.middleNameAttribute")
    @Default("middleName")
    String getMiddleNameAttribute();

    @Source(type = SourceType.APP)
    @Property("ldap.memberOfAttribute")
    @Default("memberOf")
    String getMemberOfAttribute();

    @Source(type = SourceType.APP)
    @Property("ldap.accessGroupAttribute")
    String getAccessGroupAttribute();

    @Source(type = SourceType.APP)
    @Property("ldap.positionAttribute")
    @Default("employeeType")
    String getPositionAttribute();

    @Source(type = SourceType.APP)
    @Property("ldap.ouAttribute")
    @Default("ou")
    String getOuAttribute();

    @Source(type = SourceType.APP)
    @Property("ldap.languageAttribute")
    @Default("preferredLanguage")
    String getLanguageAttribute();

    @Source(type = SourceType.APP)
    @Property("ldap.inactiveUserAttribute")
    @Default("userAccountControl")
    String getInactiveUserAttribute();

    @Source(type = SourceType.APP)
    @Property("ldap.userBase")
    String getUserBase();

    void setContextSourceUrl(String contextSourceUrl);

    void setContextSourceBase(String contextSourceBase);

    void setContextSourceUserName(String contextSourceUserName);

    void setContextSourcePassword(String contextSourcePassword);

    void setSessionExpiringPeriodSec(int sessionExpiringPeriod);

    void setStandardAuthenticationUsers(List<String> standardAuthenticationUsers);

    void setLdapAddonEnabled(Boolean ldapAddonEnabled);

    void setExpiringSessionsEnable(Boolean expiringSessionsEnable);

    void setUserSynchronizationBatchSize(Integer userSynchronizationBatchSize);

    void setUserSynchronizationOnlyActiveProperty(Boolean userSynchronizationOnlyActiveProperty);

    void setCubaGroupForSynchronization(List<String> cubaGroupForSynchronization);

    void setCubaGroupForSynchronizationInverse(Boolean cubaGroupForSynchronizationInverse);

    void setSynchronizeCommonInfoFromLdap(Boolean synchronizeCommonInfoFromLdap);

    void setSchemaBase();

    void setDefaultAccessGroupName();

    void setLdapUserObjectClasses();

    void setObjectClassPropertyName();

    void setAttributePropertyNames();

    void setLoginAttribute();

    void setEmailAttribute();

    void setCnAttribute();

    void setSnAttribute();

    void setGivenNameAttribute();

    void setMiddleNameAttribute();

    void setMemberOfAttribute();

    void setAccessGroupAttribute();

    void setPositionAttribute();

    void setOuAttribute();

    void setLanguageAttribute();

    void setInactiveUserAttribute();

    void setUserBase();

}
