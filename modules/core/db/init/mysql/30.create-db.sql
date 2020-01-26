INSERT INTO LDAP_MATCHING_RULE_ORDER(id,version,create_ts,created_by,order_) values('ff2ebe743836465b918560141a6a0548',0,curdate(),'admin',2147483647);
INSERT INTO LDAP_MATCHING_RULE_STATUS(id,version,create_ts,created_by,is_active) values('ff2ebe743836465b918560141a6a0548',0,curdate(),'admin',true);

INSERT INTO LDAP_MATCHING_RULE (id,version,create_ts,created_by,rule_type,description,is_terminal_rule,is_override_existing_roles,
is_override_exist_access_grp,matching_rule_order_id,matching_rule_status_id)
values
('ff2ebe743836465b918560141a6a0548',0,now(),'admin','DEFAULT','Default rule',false,false,false,'ff2ebe743836465b918560141a6a0548','ff2ebe743836465b918560141a6a0548');

update LDAP_MATCHING_RULE set access_group_id = (select id from SEC_GROUP where name='Company') where id = 'ff2ebe743836465b918560141a6a0548';

--insert into LDAP_MATCHING_RULE_ROLE_LINK (matching_rule_id, role_id) values ('ff2ebe743836465b918560141a6a0548', (select id from SEC_ROLE where name='Administrators'));

INSERT INTO LDAP_LDAP_CONFIG (id,version,update_ts,updated_by,SCHEMA_BASE,LDAP_USER_OBJECT_CLASSES,OBJECT_CLASS_PROPERTY_NAME,ATTRIBUTE_PROPERTY_NAMES,
EMAIL_ATTRIBUTE,CN_ATTRIBUTE,SN_ATTRIBUTE,MEMBER_OF_ATTRIBUTE,POSITION_ATTRIBUTE,OU_ATTRIBUTE,LANGUAGE_ATTRIBUTE,INACTIVE_USER_ATTRIBUTE,USER_BASE,LOGIN_ATTRIBUTE,
GIVEN_NAME_ATTRIBUTE,MIDDLE_NAME_ATTRIBUTE,DEFAULT_ACCESS_GROUP_NAME)
values
('ff2ebe743836465b918560141a6a0548',0,curdate(),'admin','CN=Schema,CN=Configuration','person;inetOrgPerson','CN','systemMustContain;systemMayContain;mayContain;MustContain',
'mail','cn','sn','memberOf','employeeType','ou','preferredLanguage','userAccountControl','','sAMAccountName','givenName','middleName','Company');
