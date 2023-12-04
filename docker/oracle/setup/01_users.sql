alter session set container = XEPDB1;

ALTER SYSTEM SET db_create_file_dest = '/opt/oracle/oradata';

create tablespace JAWS;

create user "JAWS_OWNER" profile "DEFAULT" identified by "password" default tablespace "JAWS" account unlock;

grant connect to JAWS_OWNER;
grant unlimited tablespace to JAWS_OWNER;

grant create view to JAWS_OWNER;
grant create sequence to JAWS_OWNER;
grant create table to JAWS_OWNER;
grant create procedure to JAWS_OWNER;
grant create type to JAWS_OWNER;
grant create trigger to JAWS_OWNER;