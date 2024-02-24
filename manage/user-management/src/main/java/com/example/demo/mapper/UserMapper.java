package com.example.demo.mapper;

import com.example.demo.entity.Userinfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-01-15
 * Time: 17:59
 */
@Mapper
public interface UserMapper {
    Userinfo gerUserByLoginname (@Param("loginname") String loginname);

    List<Userinfo> getList();

    int delUser(@Param("id") Integer id,@Param("uid") Integer uid);

    Userinfo getUserById (@Param("id") Integer id);

    int getUserByKey( @Param("key") String key,@Param("value")String value);

    Integer getUserIdByKey( @Param("key") String key,@Param("value")String value);

    int getCount(@Param("username") String username,
                 @Param("address") String address,
                 @Param("email") String email);

    List<Userinfo> getListByPage(@Param("psize")Integer psize,
                                 @Param("offsize") Integer offsize,
                                 @Param("username") String username,
                                 @Param("address") String address,
                                 @Param("email") String email);

    int add (Userinfo userinfo);

    int update(Userinfo userinfo);

    int delByIds(@Param("ids")Integer[] ids);

}
