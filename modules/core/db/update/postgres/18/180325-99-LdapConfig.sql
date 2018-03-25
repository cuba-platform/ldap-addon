INSERT INTO LDAP_LDAP_CONFIG (id,version,update_ts,updated_by,SCHEMA_BASE,LDAP_USER_OBJECT_CLASSES,OBJECT_CLASS_PROPERTY_NAME,ATTRIBUTE_PROPERTY_NAMES,
EMAIL_ATTRIBUTE,CN_ATTRIBUTE,SN_ATTRIBUTE,MEMBER_OF_ATTRIBUTE,POSITION_ATTRIBUTE,OU_ATTRIBUTE,LANGUAGE_ATTRIBUTE,INACTIVE_USER_ATTRIBUTE,USER_BASE)
values
('ff2ebe74-3836-465b-9185-60141a6a0548',0,now(),'admin','ou=schema','person;inetOrgPerson','m-name','m-must;m-may','email','cn','sn','memberOf','employeeType','ou',
'preferredLanguage','accountExpires','ou=people');
