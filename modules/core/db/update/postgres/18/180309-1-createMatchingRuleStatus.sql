create table LDAP_MATCHING_RULE_STATUS (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    CUSTOM_MATCHING_RULE_ID varchar(255),
    IS_ACTIVE boolean not null,
    --
    primary key (ID)
);
