--这个文件主要是保存建库建表语句

drop database if exists blog_system;
create database blog_system default character set utf8mb4;
use blog_system;

drop table if exists commentinfo;
drop table if exists userinfo;
drop table if exists articleinfo;

create table userinfo(
     id int primary key auto_increment,
     username varchar(100) unique not null,
     password varchar(65) not null,
     photo varchar(500) default './image/00.png',
     createtime timestamp default current_timestamp,
     updatetime timestamp default current_timestamp,
     `state` int default 1
) default charset 'utf8mb4';

create table articleinfo(
     id int primary key auto_increment,
     title varchar(100) not null,
     content text not null,
     createtime timestamp default current_timestamp,
     updatetime timestamp default current_timestamp,
     uid int not null,
     rcount int not null default 1,
     `state` int default 1
)default charset 'utf8mb4';

create table commentinfo(
     id int primary key auto_increment,
     articleid int ,foreign key (articleid) references articleinfo(id),
     uid int not null,
     content text not null,
     createtime timestamp default current_timestamp,
     replyid int,
     parentid int
)default charset 'utf8mb4';


insert into userinfo (id,username,password,photo,createtime,updatetime, state) values
(1, 'admin', 'admin', '', '2021-12-06 17:10:48', '2021-12-06 17:10:48', 1);
insert into articleinfo(title,content,uid) values('Java','Java正⽂',1);

insert into articleinfo(title,content,uid) values('Java','Java正⽂',1);

insert into articleinfo(title,content,uid) values ('这是我的第一篇博客','今天风和日丽,servlet的生命是这样度过的',2),
('这是我的第二篇博客','servlet开始的时候执行init,每次收到请求执行service,销毁之前执行destroy',2),
('Cookie和Session之间的关联和区别','关联:在网站的登录中需要配合使用.区别:Cookie可以存各种键值对,Session则专门用来保存用户的身份信息.',2);
4
insert into commentinfo (articleid ,uid ,content , createtime, replyid,parentid)values
(4,58,"好久不见",now(),null,-1),(4,2,"嘿嘿,好久不见",now(),58,1);
insert into commentinfo (articleid ,uid ,content , createtime, replyid,parentid)values
(4,58,"好久不见",now(),1,1),(4,2,"嘿嘿,好久不见",now(),58,1);
insert into commentinfo (articleid ,uid ,content , createtime,parentid)values
(4,58,"好久不见",now(),-1),(4,2,"嘿嘿,好久不见",now(),-1);


