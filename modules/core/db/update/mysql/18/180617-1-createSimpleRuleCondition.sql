create table LDAP_SIMPLE_RULE_CONDITION (
    ID varchar(32),
    VERSION integer not null,
    CREATE_TS datetime(3),
    CREATED_BY varchar(50),
    UPDATE_TS datetime(3),
    UPDATED_BY varchar(50),
    DELETE_TS datetime(3),
    DELETED_BY varchar(50),
    --
    ATTRIBUTE varchar(255),
    ATTRIBUTE_VALUE longtext,
    SIMPLE_MATCHING_RULE_ID varchar(32),
    --
    primary key (ID)
);
