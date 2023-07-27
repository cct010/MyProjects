--这个文件主要是保存建库建表语句

create database if not exists blog_system;
use blog_system;
drop table if exists user;
drop table if exists blog;


create table user(
    userId int primary key auto_increment,
    username varchar(20) unique,
    password varchar(20)
);

create table blog(
    blogId int primary key auto_increment,
    title varchar(128),
    content varchar(4096),
    postTime datetime,
    userId int
);

--构造测试数据
insert into user (username ,password)values ('taozi','123456789'),('caomei','98765432');
insert into blog (title,content,postTime,userId)values ('这是我的第一篇博客','今天风和日丽,servlet的生命是这样度过的',now(),1),
('这是我的第二篇博客','servlet开始的时候执行init,每次收到请求执行service,销毁之前执行destroy',now(),1),
('Cookie和Session之间的关联和区别','关联:在网站的登录中需要配合使用.区别:Cookie可以存各种键值对,Session则专门用来保存用户的身份信息.','2023-07-08 11:14:13',2);
