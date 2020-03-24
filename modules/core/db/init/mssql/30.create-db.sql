--add default matching rule
INSERT INTO LDAP_MATCHING_RULE_ORDER(id,version,create_ts,created_by,order_) values('ff2ebe74-3836-465b-9185-60141a6a0548',0,getdate(),'admin',2147483647);
INSERT INTO LDAP_MATCHING_RULE_STATUS(id,version,create_ts,created_by,is_active) values('ff2ebe74-3836-465b-9185-60141a6a0548',0,getdate(),'admin',1);

INSERT INTO LDAP_MATCHING_RULE (id,version,create_ts,created_by,rule_type,description,is_terminal_rule,is_override_existing_roles,
is_override_exist_access_grp,matching_rule_order_id,matching_rule_status_id,roles_list)
values
('ff2ebe74-3836-465b-9185-60141a6a0548',0,getdate(),'admin','DEFAULT','Default rule',0,0,0,'ff2ebe74-3836-465b-9185-60141a6a0548','ff2ebe74-3836-465b-9185-60141a6a0548','ldap-administrator');

update LDAP_MATCHING_RULE set access_group_id = (select id from sec_group where name='Company') where id = 'ff2ebe74-3836-465b-9185-60141a6a0548';

insert into SEC_ROLE
(ID, VERSION, CREATE_TS, CREATED_BY, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, NAME, LOC_NAME, DESCRIPTION, ROLE_TYPE, IS_DEFAULT_ROLE, SYS_TENANT_ID, SECURITY_SCOPE)
values ('096f7cef-92b0-f354-d96a-1da0d654f95b', 1, '2020-01-26 15:12:42', 'admin', '2020-01-26 15:12:42', null, null, null, 'ldap-administrator', 'Ldap administrator', null, 0, null, null, 'GENERIC_UI');

insert into SEC_USER_ROLE
(ID, VERSION, CREATE_TS, CREATED_BY, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, USER_ID, ROLE_ID, ROLE_NAME)
values ('9eaf6af4-53b3-43f9-cbaf-dc6e90cd6872', 1, '2020-01-26 15:48:55', 'admin', '2020-01-26 15:48:55', null, null, null, '60885987-1b61-4247-94c7-dff348347f93', '096f7cef-92b0-f354-d96a-1da0d654f95b', null);

insert into SEC_PERMISSION
(ID, VERSION, CREATE_TS, CREATED_BY, UPDATE_TS, UPDATED_BY, DELETE_TS, DELETED_BY, PERMISSION_TYPE, TARGET, VALUE_, ROLE_ID)
values ('5290acf9-2cbf-508f-2917-39595f60ca0c', 1, '2020-03-19 15:53:07', 'admin', '2020-03-19 15:53:07', null, null, null, 40, 'cuba.gui.loginToClient', 1, '096f7cef-92b0-f354-d96a-1da0d654f95b');

--add ldap configuration
INSERT INTO LDAP_LDAP_CONFIG (id,version,update_ts,updated_by,SCHEMA_BASE,LDAP_USER_OBJECT_CLASSES,OBJECT_CLASS_PROPERTY_NAME,ATTRIBUTE_PROPERTY_NAMES,
EMAIL_ATTRIBUTE,CN_ATTRIBUTE,SN_ATTRIBUTE,MEMBER_OF_ATTRIBUTE,POSITION_ATTRIBUTE,OU_ATTRIBUTE,LANGUAGE_ATTRIBUTE,INACTIVE_USER_ATTRIBUTE,USER_BASE,LOGIN_ATTRIBUTE,
GIVEN_NAME_ATTRIBUTE,MIDDLE_NAME_ATTRIBUTE,DEFAULT_ACCESS_GROUP_NAME)
values
('ff2ebe74-3836-465b-9185-60141a6a0548',0,getdate(),'admin','CN=Schema,CN=Configuration','person;inetOrgPerson','CN','systemMustContain;systemMayContain;mayContain;MustContain',
'mail','cn','sn','memberOf','employeeType','ou','preferredLanguage','userAccountControl','','sAMAccountName','givenName','middleName','Company');

