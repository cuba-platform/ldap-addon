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

package com.haulmont.addon.ldap.dto;

import java.util.*;

public class LdapUser {

    private final Map<String, Object> ldapUserAttributesMap;
    private String dn;
    private String login;
    private String cn;
    private String givenName;
    private String sn;
    private String middleName;
    private String email;
    private List<String> memberOf;
    private List<String> accessGroups;
    private Boolean isDisabled = false;
    private String position;
    private String language;
    private String ou;

    public LdapUser(Map<String, Object> ldapUserAttributes) {
        this.ldapUserAttributesMap = ldapUserAttributes;
    }

    public LdapUser(LdapUser source) {
        this.login = source.login;
        this.dn = source.dn;
        this.cn = source.cn;
        this.givenName = source.givenName;
        this.sn = source.sn;
        this.middleName = source.middleName;
        this.email = source.email;
        this.memberOf = source.memberOf;
        this.accessGroups = source.accessGroups;
        this.isDisabled = source.isDisabled;
        this.position = source.position;
        this.language = source.language;
        this.ou = source.ou;
        this.ldapUserAttributesMap = new HashMap<>(source.ldapUserAttributesMap);
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getMemberOf() {
        return memberOf;
    }

    public void setMemberOf(List<String> memberOf) {
        this.memberOf = memberOf;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public List<String> getAccessGroups() {
        return accessGroups;
    }

    public void setAccessGroups(List<String> accessGroups) {
        this.accessGroups = accessGroups;
    }

    public Boolean getDisabled() {
        return isDisabled;
    }

    public void setDisabled(Boolean disabled) {
        isDisabled = disabled;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getOu() {
        return ou;
    }

    public void setOu(String ou) {
        this.ou = ou;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public Object getLdapAttribute(String attributeName) {
        return ldapUserAttributesMap.get(attributeName);
    }

    public Set<String> getLdapAttributeNames() {
        return ldapUserAttributesMap.keySet();
    }

    public Map<String, Object> getUnmodifiableLdapAttributeMap() {
        return Collections.unmodifiableMap(ldapUserAttributesMap);
    }

}