create table LDAP_USER_SYNCHRONIZATION_LOG (
    ID uuid,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    LOGIN varchar(255) not null,
    LDAP_ATTRIBUTES text,
    RESULT_ varchar(50) not null,
    APPLIED_RULES text,
    ROLES_BEFORE text,
    ROLES_AFTER text,
    ACCESS_GROUP_BEFORE varchar(255),
    ACCESS_GROUP_AFTER varchar(255),
    USER_INFO_BEFORE text,
    USER_INFO_AFTER varchar(255),
    ERROR_TEXT text,
    IS_NEW_USER boolean,
    IS_DEACTIVATED boolean,
    --
    primary key (ID)
);
