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
);
