alter table LDAP_MATCHING_RULE add constraint FK_LDAP_MATCHING_RULE_ACCESS_GROUP foreign key (ACCESS_GROUP_ID) references SEC_GROUP(ID);
create index IDX_LDAP_MATCHING_RULE_ACCESS_GROUP on LDAP_MATCHING_RULE (ACCESS_GROUP_ID);