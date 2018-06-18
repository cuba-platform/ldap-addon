create table LDAP_USER_ATTRIBUTE (
    ID uniqueidentifier,
    CREATE_TS datetime2,
    CREATED_BY varchar(50),
    --
    ATTRIBUTE_NAME varchar(255) not null,
    --
    primary key nonclustered (ID)
);
