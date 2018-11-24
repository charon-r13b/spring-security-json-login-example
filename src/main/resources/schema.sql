drop table if exists user;

create table user (
  id int primary key auto_increment,
  email varchar(100),
  password varchar(100),
  name varchar(20),
  role varchar(20)
);