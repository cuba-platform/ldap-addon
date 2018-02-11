-- begin LDAP_MATCHING_RULE
create table LDAP_MATCHING_RULE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    RULE_TYPE varchar(31),
    --
    ACCESS_GROUP_ID uuid,
    IS_TERMINAL_RULE boolean,
    IS_OVERRIDE_EXISTING_ROLES boolean,
    IS_OVERRIDE_EXIST_ACCESS_GRP boolean,
    IS_DISABLED boolean,
    DESCRIPTION varchar(1500),
    ORDER_ integer,
    --
    -- from ldap$ScriptingMatchingRule
    STRING_CONDITION text,
    --
    primary key (ID)
)^
-- end LDAP_MATCHING_RULE
-- begin LDAP_MATCHING_RULE_ROLE_LINK
create table LDAP_MATCHING_RULE_ROLE_LINK (
    MATCHING_RULE_ID uuid,
    ROLE_ID uuid,
    primary key (MATCHING_RULE_ID, ROLE_ID)
)^
-- end LDAP_MATCHING_RULE_ROLE_LINK
-- begin LDAP_SIMPLE_RULE_CONDITION
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
)^
-- end LDAP_SIMPLE_RULE_CONDITION
