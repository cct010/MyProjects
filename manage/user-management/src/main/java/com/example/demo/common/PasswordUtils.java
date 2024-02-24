package com.example.demo.common;

import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-01-15
 * Time: 19:10
 */
//密码加盐
public class PasswordUtils {
    //加盐密码的生成
    public static String encrypt(String password){
        String salt = UUID.randomUUID().toString().replace("-","");
        //加盐后的密码
        String saltPassword = DigestUtils.md5DigestAsHex((salt+password).getBytes());
        String finalPassword = salt + "$" + saltPassword;
        return finalPassword;
    }
    public static String encrypt(String password,String salt){
        String saltPassword = DigestUtils.md5DigestAsHex((salt+password).getBytes());
        String finalPassword = salt +"$"+saltPassword;
        return finalPassword;
    }

    public static boolean check(String inputPassword,String finalPassword){
        if(finalPassword.length()==65 && inputPassword.length()>0){
            //得到盐值
            String salt = finalPassword.split("\\$")[0];
            String confirmPassword = PasswordUtils.encrypt(inputPassword,salt);
            return confirmPassword.equals(finalPassword);
        }
        return false;
    }
}
