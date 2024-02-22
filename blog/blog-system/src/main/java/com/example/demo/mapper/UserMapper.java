package com.example.demo.mapper;

import com.example.demo.entity.Userinfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2023-12-07
 * Time: 21:36
 */
@Mapper
public interface UserMapper {
    //注册,新增用户
    int reg(Userinfo userinfo);

    //根据用户名查询userinfo对象
    Userinfo getUserByName(@Param("username") String username);

    //根据id查询用户
    Userinfo getUserById(@Param("id") Integer id);

    //修改头像
    int updatePhoto(Userinfo userinfo);
}
