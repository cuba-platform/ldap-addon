create table LDAP_MATCHING_RULE_ROLE_LINK (
    MATCHING_RULE_ID uuid,
    ROLE_ID uuid,
    primary key (MATCHING_RULE_ID, ROLE_ID)
);
