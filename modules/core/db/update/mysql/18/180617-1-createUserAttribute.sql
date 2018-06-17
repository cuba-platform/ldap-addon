create table LDAP_USER_ATTRIBUTE (
    ID varchar(32),
    CREATE_TS datetime(3),
    CREATED_BY varchar(50),
    --
    ATTRIBUTE_NAME varchar(255) not null,
    --
    primary key (ID)
);
