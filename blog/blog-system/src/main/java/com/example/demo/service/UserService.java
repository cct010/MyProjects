package com.example.demo.service;

import com.example.demo.entity.Userinfo;
import com.example.demo.mapper.UserMapper;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2023-12-07
 * Time: 21:53
 */
@Service
public class UserService {
    @Resource
    private UserMapper userMapper;


    public int reg(Userinfo userinfo){
        return userMapper.reg(userinfo);
    }


    public Userinfo getUserByName(String username){
        return userMapper.getUserByName(username);
    }

    public Userinfo getUserById(Integer id){
        return userMapper.getUserById(id);
    }

    public int updatePhoto (Userinfo userinfo){
        return userMapper.updatePhoto(userinfo);
    }
}
