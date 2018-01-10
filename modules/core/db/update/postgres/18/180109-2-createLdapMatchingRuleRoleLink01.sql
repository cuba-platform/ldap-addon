alter table LDAP_MATCHING_RULE_ROLE_LINK add constraint FK_MATRULROL_MATCHING_RULE foreign key (MATCHING_RULE_ID) references LDAP_MATCHING_RULE(ID);
alter table LDAP_MATCHING_RULE_ROLE_LINK add constraint FK_MATRULROL_ROLE foreign key (ROLE_ID) references SEC_ROLE(ID);
