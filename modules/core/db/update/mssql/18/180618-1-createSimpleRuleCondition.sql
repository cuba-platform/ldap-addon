create table LDAP_SIMPLE_RULE_CONDITION (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY varchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY varchar(50),
    DELETE_TS datetime2,
    DELETED_BY varchar(50),
    --
    ATTRIBUTE varchar(255),
    ATTRIBUTE_VALUE varchar(max),
    SIMPLE_MATCHING_RULE_ID uniqueidentifier,
    --
    primary key nonclustered (ID)
);
