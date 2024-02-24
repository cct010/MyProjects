package com.example.demo.common;

import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2023-12-09
 * Time: 22:37
 */
//密码加盐
public class PasswordUtils {
    //加盐密码的生成
    public static String encrypt(String password){
        //产生盐值
        String salt = UUID.randomUUID().toString().replace("-","");
        //生成加盐后的密码
        String saltPassword = DigestUtils.md5DigestAsHex((salt+password).getBytes());
        //生成65位,最终密码
        String finalPassword = salt+ "$" +saltPassword;
        return finalPassword;
    }

    //生成加盐的密码,重载
    public static String encrypt(String password,String salt){
        //生成加盐密码
        String saltPassword = DigestUtils.md5DigestAsHex((salt+password).getBytes());
        //生成最终密码
        String finalPassword = salt+ "$" + saltPassword;
        return finalPassword;
    }

    //验证两个加盐密码,用户输入的密码,数据库的最终密码
    public static boolean check(String inputPassword,String finalPassword){
        //判断密码合法性
        if(finalPassword.length()==65 && StringUtils.hasLength(inputPassword) &&
        StringUtils.hasLength(finalPassword)){
            //得到盐值
            String salt = finalPassword.split("\\$")[0];
            //将明文加密
            String confirmPassword = PasswordUtils.encrypt(inputPassword,salt);
            //对比两个密码
            return confirmPassword.equals(finalPassword);
        }
        return false;
    }
}
