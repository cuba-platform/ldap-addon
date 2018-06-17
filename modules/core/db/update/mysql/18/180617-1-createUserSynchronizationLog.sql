create table LDAP_USER_SYNCHRONIZATION_LOG (
    ID varchar(32),
    CREATE_TS datetime(3),
    CREATED_BY varchar(50),
    DELETE_TS datetime(3),
    DELETED_BY varchar(50),
    --
    LOGIN varchar(255) not null,
    LDAP_ATTRIBUTES longtext,
    RESULT_ varchar(50) not null,
    APPLIED_RULES longtext,
    ROLES_BEFORE longtext,
    ROLES_AFTER longtext,
    ACCESS_GROUP_BEFORE varchar(255),
    ACCESS_GROUP_AFTER varchar(255),
    USER_INFO_BEFORE longtext,
    USER_INFO_AFTER longtext,
    ERROR_TEXT longtext,
    IS_NEW_USER boolean,
    IS_DEACTIVATED boolean,
    --
    primary key (ID)
);
