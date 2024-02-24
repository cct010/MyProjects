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
 * Date: 2024-01-15
 * Time: 20:16
 */
//用户登录权限,拦截器
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if(session!=null&&session.getAttribute(AppVariable.USER_SESSION_KEY)!=null){
            return true;
        }
        response.sendRedirect("/manage/login.html");
        return false;
    }
}
