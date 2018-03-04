alter table LDAP_MATCHING_RULE_ORDER add column CUSTOM_MATCHING_RULE_ID varchar(255) ;
alter table LDAP_MATCHING_RULE_ORDER drop column MATCHING_RULE_ID cascade ;
