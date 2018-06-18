create table LDAP_MATCHING_RULE_ROLE_LINK (
    MATCHING_RULE_ID uniqueidentifier,
    ROLE_ID uniqueidentifier,
    primary key (MATCHING_RULE_ID, ROLE_ID)
);
