package com.example.demo.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-02-13
 * Time: 23:02
 */
//拦截器false时返回的信息
@Component
public class PreHandleFalseResponse {
    @Autowired
    private ObjectMapper objectMapper;

    //返回json格式的错误信息
    public void preHandleFalseResponseJsonBody(HttpServletResponse response, String msg) throws Exception {
        response.setContentType("application/json; charset=utf8");
        AjaxResult writer = AjaxResult.fail(-1, msg);
        String respJson = objectMapper.writeValueAsString(writer);
        response.getWriter().write(respJson);
    }
}
