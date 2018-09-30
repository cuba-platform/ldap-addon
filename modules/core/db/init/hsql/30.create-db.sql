--add default matching rule
INSERT INTO LDAP_MATCHING_RULE_ORDER(id,version,create_ts,created_by,order_) values('ff2ebe74-3836-465b-9185-60141a6a0548',0,now(),'admin',2147483647);
INSERT INTO LDAP_MATCHING_RULE_STATUS(id,version,create_ts,created_by,is_active) values('ff2ebe74-3836-465b-9185-60141a6a0548',0,now(),'admin',true);

INSERT INTO LDAP_MATCHING_RULE (id,version,create_ts,created_by,rule_type,description,is_terminal_rule,is_override_existing_roles,
is_override_exist_access_grp,matching_rule_order_id,matching_rule_status_id)
values
('ff2ebe74-3836-465b-9185-60141a6a0548',0,now(),'admin','DEFAULT','Default rule',false,false,false,'ff2ebe74-3836-465b-9185-60141a6a0548','ff2ebe74-3836-465b-9185-60141a6a0548');

update LDAP_MATCHING_RULE set access_group_id = (select id from sec_group where name='Company') where id = 'ff2ebe74-3836-465b-9185-60141a6a0548';

insert into LDAP_MATCHING_RULE_ROLE_LINK (matching_rule_id, role_id) values ('ff2ebe74-3836-465b-9185-60141a6a0548', (select id from sec_role where name='Administrators'));

--add ldap configuration
INSERT INTO LDAP_LDAP_CONFIG (id,version,update_ts,updated_by,SCHEMA_BASE,LDAP_USER_OBJECT_CLASSES,OBJECT_CLASS_PROPERTY_NAME,ATTRIBUTE_PROPERTY_NAMES,
EMAIL_ATTRIBUTE,CN_ATTRIBUTE,SN_ATTRIBUTE,MEMBER_OF_ATTRIBUTE,POSITION_ATTRIBUTE,OU_ATTRIBUTE,LANGUAGE_ATTRIBUTE,INACTIVE_USER_ATTRIBUTE,USER_BASE,LOGIN_ATTRIBUTE,
GIVEN_NAME_ATTRIBUTE,MIDDLE_NAME_ATTRIBUTE,DEFAULT_ACCESS_GROUP_NAME)
values
('ff2ebe74-3836-465b-9185-60141a6a0548',0,now(),'admin','CN=Schema,CN=Configuration','person;inetOrgPerson','CN','systemMustContain;systemMayContain;mayContain;MustContain',
'mail','cn','sn','memberOf','employeeType','ou','preferredLanguage','userAccountControl','','uid','givenName','middleName','Company');

INSERT INTO sec_role (id, create_ts, created_by, version, update_ts, updated_by, delete_ts, deleted_by, name, loc_name, description, is_default_role, role_type) VALUES ('ecf07a97-7e35-caf1-f6b4-6667cc1ebcde', now(), 'admin', 1, now(), NULL, NULL, NULL, 'Mathematicians', NULL, NULL, NULL, NULL);
INSERT INTO sec_role (id, create_ts, created_by, version, update_ts, updated_by, delete_ts, deleted_by, name, loc_name, description, is_default_role, role_type) VALUES ('b483c4d0-bb9a-1fc3-cbe3-d313661e0a40', now(), 'admin', 1, now(), NULL, NULL, NULL, 'Gauss', NULL, NULL, NULL, NULL);
INSERT INTO sec_role (id, create_ts, created_by, version, update_ts, updated_by, delete_ts, deleted_by, name, loc_name, description, is_default_role, role_type) VALUES ('6285efa4-f89c-b94a-de90-ae113c08b2b6', now(), 'admin', 1, now(), NULL, NULL, NULL, 'Tesla role', NULL, NULL, NULL, NULL);


INSERT INTO ldap_user_attribute (id, create_ts, created_by, attribute_name) VALUES ('29acce89-0215-db1d-7561-2a79a7a2b6b8', now(), 'admin', 'mail');
INSERT INTO ldap_user_attribute (id, create_ts, created_by, attribute_name) VALUES ('25bbf214-d24b-c5e4-e8c6-a34fdc67d52b', now(), 'admin', 'cn');
INSERT INTO ldap_user_attribute (id, create_ts, created_by, attribute_name) VALUES ('1e2d2648-8edd-d6a4-b5a6-25edf8d60a3f', now(), 'admin', 'ou');
INSERT INTO ldap_user_attribute (id, create_ts, created_by, attribute_name) VALUES ('27a05df9-3173-d2d5-da32-311da25daf68', now(), 'admin', 'sn');

--order
INSERT INTO ldap_matching_rule_order (id, version, create_ts, created_by, update_ts, updated_by, delete_ts, deleted_by, order_, custom_matching_rule_id) VALUES ('4d76fafb-121e-36da-63eb-7c7afd608d5f', 1, now(), 'admin', now(), NULL, NULL, NULL, 1, NULL);
INSERT INTO ldap_matching_rule_order (id, version, create_ts, created_by, update_ts, updated_by, delete_ts, deleted_by, order_, custom_matching_rule_id) VALUES ('1f0cb7c8-532a-958d-482f-42c32a9f1408', 1, now(), 'admin', now(), NULL, NULL, NULL, 2, NULL);
INSERT INTO ldap_matching_rule_order (id, version, create_ts, created_by, update_ts, updated_by, delete_ts, deleted_by, order_, custom_matching_rule_id) VALUES ('ddfd8e2d-dd73-0444-96bb-7be9bb313a6f', 1, now(), 'admin', now(), NULL, NULL, NULL, 3, 'com.haulmont.addon.ldap.core.TeslaCustomLdapRule');

--status
INSERT INTO ldap_matching_rule_status (id, version, create_ts, created_by, update_ts, updated_by, delete_ts, deleted_by, custom_matching_rule_id, is_active) VALUES ('08968d29-ca43-2cf4-e046-fd3b1081f263', 1, now(), 'admin', now(), NULL, NULL, NULL, NULL, true);
INSERT INTO ldap_matching_rule_status (id, version, create_ts, created_by, update_ts, updated_by, delete_ts, deleted_by, custom_matching_rule_id, is_active) VALUES ('3747f923-c3f0-66d4-96f1-8a11d95a35e5', 1, now(), 'admin', now(), NULL, NULL, NULL, NULL, true);
INSERT INTO ldap_matching_rule_status (id, version, create_ts, created_by, update_ts, updated_by, delete_ts, deleted_by, custom_matching_rule_id, is_active) VALUES ('b7585087-5715-3cb9-3eca-4bb11d62c541', 1, now(), 'admin', now(), NULL, NULL, NULL, 'com.haulmont.addon.ldap.core.TeslaCustomLdapRule', true);

--MR
INSERT INTO ldap_matching_rule (id, version, create_ts, created_by, update_ts, updated_by, delete_ts, deleted_by, rule_type, matching_rule_status_id, matching_rule_order_id, description, access_group_id, is_terminal_rule, is_override_existing_roles, is_override_exist_access_grp, string_condition) VALUES ('5eec7833-151e-40e6-a8c0-b41b71f6e1b2', 7, now(), 'admin', now(), 'admin', NULL, NULL, 'SCRIPTING', '08968d29-ca43-2cf4-e046-fd3b1081f263', '1f0cb7c8-532a-958d-482f-42c32a9f1408', 'Assign roles according to user CN and SN attribute', '0fa2b1a5-1d68-4d69-9fbd-dff348347f93', false, false, false, '{ldapContext}.ldapUser.cn=="Carl Friedrich Gauss"  && {ldapContext}.ldapUser.sn == "Gauss"');
INSERT INTO ldap_matching_rule (id, version, create_ts, created_by, update_ts, updated_by, delete_ts, deleted_by, rule_type, matching_rule_status_id, matching_rule_order_id, description, access_group_id, is_terminal_rule, is_override_existing_roles, is_override_exist_access_grp, string_condition) VALUES ('b5d4effd-f202-2170-e0d7-414760c56f49', 7, now(), 'admin', now(), 'admin', NULL, NULL, 'SIMPLE', '3747f923-c3f0-66d4-96f1-8a11d95a35e5', '4d76fafb-121e-36da-63eb-7c7afd608d5f', 'Assign rules according to MAIL attribute', '0fa2b1a5-1d68-4d69-9fbd-dff348347f93', false, false, false, NULL);

--SMRC
INSERT INTO ldap_simple_rule_condition (id, version, create_ts, created_by, update_ts, updated_by, delete_ts, deleted_by, attribute, attribute_value, simple_matching_rule_id) VALUES ('06f8b254-a099-1c47-afd2-dd4364cd192e', 2, now(), 'admin', now(), 'admin', NULL, NULL, 'mail', 'riemann@ldap.forumsys.com', 'b5d4effd-f202-2170-e0d7-414760c56f49');

INSERT INTO ldap_matching_rule_role_link (matching_rule_id, role_id) VALUES ('5eec7833-151e-40e6-a8c0-b41b71f6e1b2', 'b483c4d0-bb9a-1fc3-cbe3-d313661e0a40');
INSERT INTO ldap_matching_rule_role_link (matching_rule_id, role_id) VALUES ('b5d4effd-f202-2170-e0d7-414760c56f49', 'ecf07a97-7e35-caf1-f6b4-6667cc1ebcde');


