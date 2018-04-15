update LDAP_MATCHING_RULE set RULE_TYPE = 'SIMPLE' where RULE_TYPE is null ;
alter table LDAP_MATCHING_RULE alter column RULE_TYPE set not null ;
