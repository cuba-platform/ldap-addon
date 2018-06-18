create table LDAP_MATCHING_RULE_STATUS (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY varchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY varchar(50),
    DELETE_TS datetime2,
    DELETED_BY varchar(50),
    --
    CUSTOM_MATCHING_RULE_ID varchar(255),
    IS_ACTIVE tinyint not null,
    --
    primary key nonclustered (ID)
);
