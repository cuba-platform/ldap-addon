-- begin LDAP_MATCHING_RULE
create table LDAP_MATCHING_RULE (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    RULE_TYPE varchar(50) not null,
    MATCHING_RULE_STATUS_ID varchar(36) not null,
    MATCHING_RULE_ORDER_ID varchar(36) not null,
    DESCRIPTION varchar(1500),
    --
    ACCESS_GROUP_ID varchar(36),
    IS_TERMINAL_RULE boolean,
    IS_OVERRIDE_EXISTING_ROLES boolean,
    IS_OVERRIDE_EXIST_ACCESS_GRP boolean,
    --
    -- from ldap$ScriptingMatchingRule
    STRING_CONDITION longvarchar,
    --
    primary key (ID)
)^
-- end LDAP_MATCHING_RULE
-- begin LDAP_SIMPLE_RULE_CONDITION
create table LDAP_SIMPLE_RULE_CONDITION (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ATTRIBUTE varchar(255),
    ATTRIBUTE_VALUE longvarchar,
    SIMPLE_MATCHING_RULE_ID varchar(36),
    --
    primary key (ID)
)^
-- end LDAP_SIMPLE_RULE_CONDITION
-- begin LDAP_MATCHING_RULE_ORDER
create table LDAP_MATCHING_RULE_ORDER (
    ID varchar(36) not null,
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
    ID varchar(36) not null,
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
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    --
    ATTRIBUTE_NAME varchar(255) not null,
    --
    primary key (ID)
)^
-- end LDAP_USER_ATTRIBUTE
-- begin LDAP_USER_SYNCHRONIZATION_LOG
create table LDAP_USER_SYNCHRONIZATION_LOG (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    LOGIN varchar(255) not null,
    LDAP_ATTRIBUTES longvarchar,
    RESULT_ varchar(50) not null,
    APPLIED_RULES longvarchar,
    ROLES_BEFORE longvarchar,
    ROLES_AFTER longvarchar,
    ACCESS_GROUP_BEFORE varchar(255),
    ACCESS_GROUP_AFTER varchar(255),
    USER_INFO_BEFORE longvarchar,
    USER_INFO_AFTER longvarchar,
    ERROR_TEXT longvarchar,
    IS_NEW_USER boolean,
    IS_DEACTIVATED boolean,
    --
    primary key (ID)
)^
-- end LDAP_USER_SYNCHRONIZATION_LOG
-- begin LDAP_LDAP_CONFIG
create table LDAP_LDAP_CONFIG (
    ID varchar(36) not null,
    UPDATE_TS timestamp,
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
    primary key (ID)
)^
-- end LDAP_LDAP_CONFIG
-- begin LDAP_MATCHING_RULE_ROLE_LINK
create table LDAP_MATCHING_RULE_ROLE_LINK (
    MATCHING_RULE_ID varchar(36) not null,
    ROLE_ID varchar(36) not null,
    primary key (MATCHING_RULE_ID, ROLE_ID)
)^
-- end LDAP_MATCHING_RULE_ROLE_LINK
