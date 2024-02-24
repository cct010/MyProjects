package com.example.demo.common;

import com.example.demo.entity.Userinfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-01-15
 * Time: 19:06
 */
//当前用户的相关操作
public class UserSessionUtils {
    //得到当前登陆的用户
    public static Userinfo getUser(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if(session!=null && session.getAttribute(AppVariable.USER_SESSION_KEY)!=null){
            return (Userinfo) session.getAttribute(AppVariable.USER_SESSION_KEY);
        }
        return null;
    }
}
