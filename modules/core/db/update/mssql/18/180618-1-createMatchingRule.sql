create table LDAP_MATCHING_RULE (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY varchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY varchar(50),
    DELETE_TS datetime2,
    DELETED_BY varchar(50),
    RULE_TYPE varchar(50) not null,
    MATCHING_RULE_STATUS_ID uniqueidentifier not null,
    MATCHING_RULE_ORDER_ID uniqueidentifier not null,
    DESCRIPTION varchar(1500),
    RULE_TYPE varchar(31),
    --
    ACCESS_GROUP_ID uniqueidentifier,
    IS_TERMINAL_RULE tinyint,
    IS_OVERRIDE_EXISTING_ROLES tinyint,
    IS_OVERRIDE_EXIST_ACCESS_GRP tinyint,
    --
    -- from ldap$ScriptingMatchingRule
    STRING_CONDITION varchar(max),
    --
    primary key nonclustered (ID)
);
