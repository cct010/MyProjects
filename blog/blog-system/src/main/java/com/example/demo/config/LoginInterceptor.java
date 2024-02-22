package com.example.demo.config;

import com.example.demo.common.AppVariable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2023-12-07
 * Time: 23:45
 */
//拦截器
@Component
public class LoginInterceptor implements HandlerInterceptor {
    //用户已经登陆,就返回true
    //用户还未登陆,就返回false
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession(false);//没有,就不会创建会话
        if(session!=null && session.getAttribute(AppVariable.USER_SESSION_KEY) !=null){
            return true;
        }
        response.sendRedirect("/blog/login.html");//未登录就跳转到登录页面
        return false;
    }
}

