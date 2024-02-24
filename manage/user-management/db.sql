--这个文件主要是保存建库建表语句


drop database if exists user_management;
create database user_management default character set utf8mb4;
use user_management;


drop table if exists userinfo;

create table userinfo(
     id int primary key auto_increment,
     loginname varchar(100) unique not null,
     username varchar(100) unique not null,
     password varchar(65) not null,
     sex varchar(2) default '男',
     age int default 0,
     address varchar (250),
     qq varchar(250),
     email varchar(250),
     isadmin bit default 0,
     `state` int default 1,
     createtime timestamp default current_timestamp,
     updatetime timestamp default current_timestamp
) default charset 'utf8mb4';



insert into userinfo (loginname,username,password,isadmin) values
( 'peachmango123456','admin1', '4efeb7e6dec74dffada6ce78f85b75e1$8f2f131ded66d28d525629b73aea841a',1);
insert into userinfo (loginname,username,password,isadmin,sex) values
( 'peachmango','peachmango', '4efeb7e6dec74dffada6ce78f85b75e1$8f2f131ded66d28d525629b73aea841a',1,'女');
insert into userinfo (loginname,username,password,isadmin,sex) values
( 'peach','peach', '4efeb7e6dec74dffada6ce78f85b75e1$8f2f131ded66d28d525629b73aea841a',0,'男');
insert into userinfo (loginname,username,password,isadmin,sex) values
( 'mango','mango', '4efeb7e6dec74dffada6ce78f85b75e1$8f2f131ded66d28d525629b73aea841a',0,'女');

insert into userinfo (loginname,username,password,isadmin,sex,age) values
( 'peachmj','peachmj', '4efeb7e6dec74dffada6ce78f85b75e1$8f2f131ded66d28d525629b73aea841a',0,'男',45);
insert into userinfo (loginname,username,password,isadmin,sex,age) values
( 'peachj','peachj', '4efeb7e6dec74dffada6ce78f85b75e1$8f2f131ded66d28d525629b73aea841a',0,'男',23);
insert into userinfo (loginname,username,password,isadmin,sex,email) values
( 'mangoj','mangoj', '4efeb7e6dec74dffada6ce78f85b75e1$8f2f131ded66d28d525629b73aea841a',0,'女',"45@qq.com");
insert into userinfo (loginname,username,password,isadmin,sex,address) values
( 'peachmjk','peachmjk', '4efeb7e6dec74dffada6ce78f85b75e1$8f2f131ded66d28d525629b73aea841a',0,'男',"南宁");