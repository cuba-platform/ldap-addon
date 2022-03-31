/*
 * Copyright (c) 2008-2022 Haulmont.
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

/*
 * Copyright (c) 2008-2022 Haulmont.
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

ALTER TABLE LDAP_MATCHING_RULE ADD LDAP_CONFIG_ID varchar2(32)^
ALTER TABLE LDAP_USER_ATTRIBUTE ADD LDAP_CONFIG_ID varchar2(32)^
ALTER TABLE LDAP_LDAP_CONFIG ADD SYS_TENANT_ID varchar2(255)^
ALTER TABLE LDAP_LDAP_CONFIG ADD CONTEXT_SOURCE_BASE varchar2(255)^
ALTER TABLE LDAP_LDAP_CONFIG ADD CONTEXT_SOURCE_URL varchar2(255)^
ALTER TABLE LDAP_LDAP_CONFIG ADD CONTEXT_SOURCE_USER_NAME varchar2(255)^
ALTER TABLE LDAP_LDAP_CONFIG ADD CONTEXT_SOURCE_PASSWORD varchar2(255)^

UPDATE LDAP_MATCHING_RULE
SET LDAP_CONFIG_ID = 'ff2ebe743836465b918560141a6a0548'^
alter table LDAP_MATCHING_RULE add constraint FK_LDAP_MATRUL_ON_LDAPCONF foreign key (LDAP_CONFIG_ID) references LDAP_LDAP_CONFIG (ID) ON DELETE CASCADE^

UPDATE LDAP_USER_ATTRIBUTE
SET LDAP_CONFIG_ID = 'ff2ebe743836465b918560141a6a0548'^
alter table LDAP_USER_ATTRIBUTE add constraint FK_LDAP_UATTR_ON_LDAPCONF foreign key (LDAP_CONFIG_ID) references LDAP_LDAP_CONFIG (ID) ON DELETE CASCADE^

create unique index IDX_LDAP_CONF_UK_TENID on LDAP_LDAP_CONFIG (SYS_TENANT_ID) WHERE SYS_TENANT_ID IS NOT NULL^