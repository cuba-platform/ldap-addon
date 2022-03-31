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

ALTER TABLE LDAP_MATCHING_RULE ADD LDAP_CONFIG_ID uniqueidentifier^

ALTER TABLE LDAP_USER_ATTRIBUTE ADD LDAP_CONFIG_ID uniqueidentifier^

ALTER TABLE LDAP_LDAP_CONFIG ADD SYS_TENANT_ID varchar(255)^
ALTER TABLE LDAP_LDAP_CONFIG ADD CONTEXT_SOURCE_BASE varchar(255)^
ALTER TABLE LDAP_LDAP_CONFIG ADD CONTEXT_SOURCE_URL varchar(255)^
ALTER TABLE LDAP_LDAP_CONFIG ADD CONTEXT_SOURCE_USER_NAME varchar(255)^
ALTER TABLE LDAP_LDAP_CONFIG ADD CONTEXT_SOURCE_PASSWORD varchar(255)^

UPDATE LDAP_MATCHING_RULE
SET LDAP_CONFIG_ID = 'ff2ebe74-3836-465b-9185-60141a6a0548'^
ALTER TABLE LDAP_MATCHING_RULE ADD CONSTRAINT FK_LDAP_MATCHING_RULE_LDAP_LDAP_CONFIG FOREIGN KEY (LDAP_CONFIG_ID) REFERENCES LDAP_LDAP_CONFIG(ID) ON DELETE CASCADE^

UPDATE LDAP_USER_ATTRIBUTE
SET LDAP_CONFIG_ID = 'ff2ebe74-3836-465b-9185-60141a6a0548'^
ALTER TABLE LDAP_USER_ATTRIBUTE ADD CONSTRAINT FK_LDAP_USER_ATTRIBUTE_LDAP_LDAP_CONFIG FOREIGN KEY (LDAP_CONFIG_ID) REFERENCES LDAP_LDAP_CONFIG (ID) ON DELETE CASCADE^

CREATE UNIQUE INDEX IDX_LDAP_LDAP_CONFIG_UNIQ_SYS_TENANT_ID on LDAP_LDAP_CONFIG (SYS_TENANT_ID)^
