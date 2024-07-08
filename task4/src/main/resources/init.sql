use task4;

drop table if exists user;

CREATE TABLE user (
                      id integer(10) NOT NULL primary key,
                      name VARCHAR(100) NOT NULL,
                      age integer(2) not null,
                      address varchar(100) not null
);