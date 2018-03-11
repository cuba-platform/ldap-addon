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
    MATCHING_RULE_STATUS_ID uuid not null,
    MATCHING_RULE_ORDER_ID uuid not null,
    DESCRIPTION varchar(1500),
    RULE_TYPE varchar(31),
    --
    ACCESS_GROUP_ID uuid,
    IS_TERMINAL_RULE boolean,
    IS_OVERRIDE_EXISTING_ROLES boolean,
    IS_OVERRIDE_EXIST_ACCESS_GRP boolean,
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
    ATTRIBUTE varchar(255),
    ATTRIBUTE_VALUE text,
    SIMPLE_MATCHING_RULE_ID uuid,
    --
    primary key (ID)
)^
-- end LDAP_SIMPLE_RULE_CONDITION
-- begin LDAP_MATCHING_RULE_ORDER
create table LDAP_MATCHING_RULE_ORDER (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ORDER_ integer not null,
    CUSTOM_MATCHING_RULE_ID varchar(255),
    --
    primary key (ID)
)^
-- end LDAP_MATCHING_RULE_ORDER
-- begin LDAP_MATCHING_RULE_STATUS
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
)^
-- end LDAP_MATCHING_RULE_STATUS
-- begin LDAP_USER_ATTRIBUTE
create table LDAP_USER_ATTRIBUTE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ATTRIBUTE_NAME varchar(255) not null,
    --
    primary key (ID)
)^
-- end LDAP_USER_ATTRIBUTE
