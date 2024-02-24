package com.example.demo.service;

import com.example.demo.entity.Userinfo;
import com.example.demo.mapper.UserMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-01-16
 * Time: 15:56
 */
@Service
public class UserService {
    @Resource
    private UserMapper userMapper;

    public Userinfo gerUserByLoginname(String loginname){
        return userMapper.gerUserByLoginname(loginname);
    }

    public List<Userinfo> getList(){
        return userMapper.getList();
    }

    public int delUser(Integer id,Integer uid){
        return userMapper.delUser(id, uid);
    }

    public int delByIds(Integer[] ids){
        return userMapper.delByIds(ids);
    }

    public Userinfo getUserById(Integer id){
        return userMapper.getUserById(id);
    }

    public int getUserByKey(String key,String value){
        return userMapper.getUserByKey(key,value);
    }

    public Integer getUserIdByKey(String key,String value){
        return userMapper.getUserIdByKey(key, value);
    }

    public Integer getCount(String username,
                            String address,
                            String email){
        return userMapper.getCount(username,address,email);
    }

    public List<Userinfo> getListByPage(Integer psize,Integer offsize,
                                         String username,
                                         String address,
                                            String email){
        return userMapper.getListByPage(psize, offsize, username,address,email);
    }

    public int add(Userinfo userinfo){
        return userMapper.add(userinfo);
    }

    public int update(Userinfo userinfo){
        return userMapper.update(userinfo);
    }

}
