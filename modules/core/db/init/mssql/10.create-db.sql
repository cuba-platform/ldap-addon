-- begin LDAP_MATCHING_RULE
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
    --
    ACCESS_GROUP_ID uniqueidentifier,
    ROLES_LIST varchar(max),
    IS_TERMINAL_RULE tinyint,
    IS_OVERRIDE_EXISTING_ROLES tinyint,
    IS_OVERRIDE_EXIST_ACCESS_GRP tinyint,
    ACCESS_GROUP_NAME varchar(255),
    --
    -- from ldap$ScriptingMatchingRule
    STRING_CONDITION varchar(max),
    --
    primary key nonclustered (ID)
)^
-- end LDAP_MATCHING_RULE
-- begin LDAP_SIMPLE_RULE_CONDITION
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
)^
-- end LDAP_SIMPLE_RULE_CONDITION
-- begin LDAP_MATCHING_RULE_ORDER
create table LDAP_MATCHING_RULE_ORDER (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY varchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY varchar(50),
    DELETE_TS datetime2,
    DELETED_BY varchar(50),
    --
    ORDER_ integer not null,
    CUSTOM_MATCHING_RULE_ID varchar(255),
    --
    primary key nonclustered (ID)
)^
-- end LDAP_MATCHING_RULE_ORDER
-- begin LDAP_MATCHING_RULE_STATUS
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
)^
-- end LDAP_MATCHING_RULE_STATUS
-- begin LDAP_USER_ATTRIBUTE
create table LDAP_USER_ATTRIBUTE (
    ID uniqueidentifier,
    CREATE_TS datetime2,
    CREATED_BY varchar(50),
    --
    ATTRIBUTE_NAME varchar(255) not null,
    --
    primary key nonclustered (ID)
)^
-- end LDAP_USER_ATTRIBUTE
-- begin LDAP_USER_SYNCHRONIZATION_LOG
create table LDAP_USER_SYNCHRONIZATION_LOG (
    ID uniqueidentifier,
    CREATE_TS datetime2,
    CREATED_BY varchar(50),
    DELETE_TS datetime2,
    DELETED_BY varchar(50),
    --
    LOGIN varchar(255) not null,
    LDAP_ATTRIBUTES varchar(max),
    RESULT_ varchar(50) not null,
    APPLIED_RULES varchar(max),
    ROLES_BEFORE varchar(max),
    ROLES_AFTER varchar(max),
    ACCESS_GROUP_BEFORE varchar(255),
    ACCESS_GROUP_AFTER varchar(255),
    USER_INFO_BEFORE varchar(max),
    USER_INFO_AFTER varchar(max),
    ERROR_TEXT varchar(max),
    IS_NEW_USER tinyint,
    IS_DEACTIVATED tinyint,
    --
    primary key nonclustered (ID)
)^
-- end LDAP_USER_SYNCHRONIZATION_LOG
-- begin LDAP_LDAP_CONFIG
create table LDAP_LDAP_CONFIG (
    ID uniqueidentifier,
    UPDATE_TS datetime2,
    UPDATED_BY varchar(50),
    VERSION integer not null,
    --
    SCHEMA_BASE varchar(255),
    DEFAULT_ACCESS_GROUP_NAME varchar(255),
    LDAP_USER_OBJECT_CLASSES varchar(2000),
    OBJECT_CLASS_PROPERTY_NAME varchar(255),
    ATTRIBUTE_PROPERTY_NAMES varchar(2000),
    LOGIN_ATTRIBUTE varchar(255),
    EMAIL_ATTRIBUTE varchar(255),
    CN_ATTRIBUTE varchar(255),
    SN_ATTRIBUTE varchar(255),
    GIVEN_NAME_ATTRIBUTE varchar(255),
    MIDDLE_NAME_ATTRIBUTE varchar(255),
    MEMBER_OF_ATTRIBUTE varchar(255),
    ACCESS_GROUP_ATTRIBUTE varchar(255),
    POSITION_ATTRIBUTE varchar(255),
    OU_ATTRIBUTE varchar(255),
    LANGUAGE_ATTRIBUTE varchar(255),
    INACTIVE_USER_ATTRIBUTE varchar(255),
    USER_BASE varchar(255),
    --
    primary key nonclustered (ID)
)^
-- end LDAP_LDAP_CONFIG