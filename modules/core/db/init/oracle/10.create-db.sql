-- begin LDAP_MATCHING_RULE
create table LDAP_MATCHING_RULE (
    ID varchar2(32),
    VERSION number(10) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar2(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar2(50),
    DELETE_TS timestamp,
    DELETED_BY varchar2(50),
    RULE_TYPE varchar2(50) not null,
    MATCHING_RULE_STATUS_ID varchar2(32) not null,
    MATCHING_RULE_ORDER_ID varchar2(32) not null,
    DESCRIPTION varchar2(1500),
    --
    ACCESS_GROUP_ID varchar2(32),
    ROLES_LIST clob,
    IS_TERMINAL_RULE char(1),
    IS_OVERRIDE_EXISTING_ROLES char(1),
    IS_OVERRIDE_EXIST_ACCESS_GRP char(1),
    ACCESS_GROUP_NAME varchar2(255),
    --
    -- from ldap$ScriptingMatchingRule
    STRING_CONDITION clob,
    LDAP_CONFIG_ID varchar2(32),
    --
    primary key (ID)
)^
-- end LDAP_MATCHING_RULE
-- begin LDAP_SIMPLE_RULE_CONDITION
create table LDAP_SIMPLE_RULE_CONDITION (
    ID varchar2(32),
    VERSION number(10) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar2(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar2(50),
    DELETE_TS timestamp,
    DELETED_BY varchar2(50),
    --
    ATTRIBUTE varchar2(255),
    ATTRIBUTE_VALUE clob,
    SIMPLE_MATCHING_RULE_ID varchar2(32),
    --
    primary key (ID)
)^
-- end LDAP_SIMPLE_RULE_CONDITION
-- begin LDAP_MATCHING_RULE_ORDER
create table LDAP_MATCHING_RULE_ORDER (
    ID varchar2(32),
    VERSION number(10) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar2(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar2(50),
    DELETE_TS timestamp,
    DELETED_BY varchar2(50),
    --
    ORDER_ number(10) not null,
    CUSTOM_MATCHING_RULE_ID varchar2(255),
    --
    primary key (ID)
)^
-- end LDAP_MATCHING_RULE_ORDER
-- begin LDAP_MATCHING_RULE_STATUS
create table LDAP_MATCHING_RULE_STATUS (
    ID varchar2(32),
    VERSION number(10) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar2(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar2(50),
    DELETE_TS timestamp,
    DELETED_BY varchar2(50),
    --
    CUSTOM_MATCHING_RULE_ID varchar2(255),
    IS_ACTIVE char(1) not null,
    --
    primary key (ID)
)^
-- end LDAP_MATCHING_RULE_STATUS
-- begin LDAP_USER_ATTRIBUTE
create table LDAP_USER_ATTRIBUTE (
    ID varchar2(32),
    CREATE_TS timestamp,
    CREATED_BY varchar2(50),
    --
    ATTRIBUTE_NAME varchar2(255) not null,
    LDAP_CONFIG_ID varchar2(32),
    --
    primary key (ID)
)^
-- end LDAP_USER_ATTRIBUTE
-- begin LDAP_USER_SYNCHRONIZATION_LOG
create table LDAP_USER_SYNCHRONIZATION_LOG (
    ID varchar2(32),
    CREATE_TS timestamp,
    CREATED_BY varchar2(50),
    DELETE_TS timestamp,
    DELETED_BY varchar2(50),
    --
    LOGIN varchar2(255) not null,
    LDAP_ATTRIBUTES clob,
    RESULT_ varchar2(50) not null,
    APPLIED_RULES clob,
    ROLES_BEFORE clob,
    ROLES_AFTER clob,
    ACCESS_GROUP_BEFORE varchar2(255),
    ACCESS_GROUP_AFTER varchar2(255),
    USER_INFO_BEFORE clob,
    USER_INFO_AFTER clob,
    ERROR_TEXT clob,
    IS_NEW_USER char(1),
    IS_DEACTIVATED char(1),
    --
    primary key (ID)
)^
-- end LDAP_USER_SYNCHRONIZATION_LOG
-- begin LDAP_LDAP_CONFIG
create table LDAP_LDAP_CONFIG (
    ID varchar2(32),
    UPDATE_TS timestamp,
    UPDATED_BY varchar2(50),
    VERSION number(10) not null,
    --
    CONTEXT_SOURCE_BASE varchar2(255),
    CONTEXT_SOURCE_URL varchar2(255),
    CONTEXT_SOURCE_USER_NAME varchar2(255),
    CONTEXT_SOURCE_PASSWORD varchar2(255),
    SCHEMA_BASE varchar2(255),
    DEFAULT_ACCESS_GROUP_NAME varchar2(255),
    LDAP_USER_OBJECT_CLASSES varchar2(2000),
    OBJECT_CLASS_PROPERTY_NAME varchar2(255),
    ATTRIBUTE_PROPERTY_NAMES varchar2(2000),
    LOGIN_ATTRIBUTE varchar2(255),
    EMAIL_ATTRIBUTE varchar2(255),
    CN_ATTRIBUTE varchar2(255),
    SN_ATTRIBUTE varchar2(255),
    GIVEN_NAME_ATTRIBUTE varchar2(255),
    MIDDLE_NAME_ATTRIBUTE varchar2(255),
    MEMBER_OF_ATTRIBUTE varchar2(255),
    ACCESS_GROUP_ATTRIBUTE varchar2(255),
    POSITION_ATTRIBUTE varchar2(255),
    OU_ATTRIBUTE varchar2(255),
    LANGUAGE_ATTRIBUTE varchar2(255),
    INACTIVE_USER_ATTRIBUTE varchar2(255),
    USER_BASE varchar2(255),
    SYS_TENANT_ID varchar2(255),
    --
    primary key (ID)
)^
-- end LDAP_LDAP_CONFIG
