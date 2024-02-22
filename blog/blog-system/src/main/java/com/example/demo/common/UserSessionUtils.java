package com.example.demo.common;

import com.example.demo.entity.Userinfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2023-12-08
 * Time: 16:03
 */
//当前用户的相关操作
public class UserSessionUtils {
    //得到当前登陆的用户
    public static Userinfo getUser(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if(session != null && session.getAttribute(AppVariable.USER_SESSION_KEY) != null){
            //说明用户已经登陆
            return (Userinfo)session.getAttribute(AppVariable.USER_SESSION_KEY);
        }
        return null;
    }

    //上传头像,useinfo里的内容需要改变,需要更新
    public static void reflushUser(HttpServletRequest request,Userinfo userinfo){
        HttpSession session = request.getSession(false);
        if(session != null && session.getAttribute(AppVariable.USER_SESSION_KEY) != null){
            //说明用户已经登陆
            session.removeAttribute(AppVariable.USER_SESSION_KEY);//删除原来的
            session.setAttribute(AppVariable.USER_SESSION_KEY,userinfo);
            //return (Userinfo)session.getAttribute(AppVariable.USER_SESSION_KEY);
        }
        //return null;
    }

}
