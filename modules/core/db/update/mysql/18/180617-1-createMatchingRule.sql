create table LDAP_MATCHING_RULE (
    ID varchar(32),
    VERSION integer not null,
    CREATE_TS datetime(3),
    CREATED_BY varchar(50),
    UPDATE_TS datetime(3),
    UPDATED_BY varchar(50),
    DELETE_TS datetime(3),
    DELETED_BY varchar(50),
    RULE_TYPE varchar(50) not null,
    MATCHING_RULE_STATUS_ID varchar(32) not null,
    MATCHING_RULE_ORDER_ID varchar(32) not null,
    DESCRIPTION varchar(1500),
    RULE_TYPE varchar(31),
    --
    ACCESS_GROUP_ID varchar(32),
    IS_TERMINAL_RULE boolean,
    IS_OVERRIDE_EXISTING_ROLES boolean,
    IS_OVERRIDE_EXIST_ACCESS_GRP boolean,
    --
    -- from ldap$ScriptingMatchingRule
    STRING_CONDITION longtext,
    --
    primary key (ID)
);
