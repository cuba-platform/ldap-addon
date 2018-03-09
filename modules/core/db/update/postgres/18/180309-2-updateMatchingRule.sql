-- alter table LDAP_MATCHING_RULE add column MATCHING_RULE_STATUS_ID uuid ^
-- update LDAP_MATCHING_RULE set MATCHING_RULE_STATUS_ID = <default_value> ;
-- alter table LDAP_MATCHING_RULE alter column MATCHING_RULE_STATUS_ID set not null ;
alter table LDAP_MATCHING_RULE add column MATCHING_RULE_STATUS_ID uuid not null ;
alter table LDAP_MATCHING_RULE drop column IS_DISABLED cascade ;
-- update LDAP_MATCHING_RULE set MATCHING_RULE_ORDER_ID = <default_value> where MATCHING_RULE_ORDER_ID is null ;
alter table LDAP_MATCHING_RULE alter column MATCHING_RULE_ORDER_ID set not null ;
