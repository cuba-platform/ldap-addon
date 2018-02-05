create table LDAP_SIMPLE_RULE_CONDITION (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ATTRIBUTE varchar(50),
    ATTRIBUTE_VALUE text,
    SIMPLE_MATCHING_RULE_ID uuid,
    --
    primary key (ID)
);
