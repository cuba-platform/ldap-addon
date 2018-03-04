-- alter table LDAP_MATCHING_RULE_ORDER add column MATCHING_RULE_ID varchar(255) ^
-- update LDAP_MATCHING_RULE_ORDER set MATCHING_RULE_ID = <default_value> ;
-- alter table LDAP_MATCHING_RULE_ORDER alter column MATCHING_RULE_ID set not null ;
alter table LDAP_MATCHING_RULE_ORDER add column MATCHING_RULE_ID varchar(255) ;
update LDAP_MATCHING_RULE_ORDER set ORDER_ = 0 where ORDER_ is null ;
alter table LDAP_MATCHING_RULE_ORDER alter column ORDER_ set not null ;
