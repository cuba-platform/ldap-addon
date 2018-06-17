create table LDAP_MATCHING_RULE_ROLE_LINK (
    MATCHING_RULE_ID varchar(32),
    ROLE_ID varchar(32),
    primary key (MATCHING_RULE_ID, ROLE_ID)
);
