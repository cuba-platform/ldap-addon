create table LDAP_LDAP_CONFIG (
    ID varchar(32),
    UPDATE_TS datetime(3),
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
);
