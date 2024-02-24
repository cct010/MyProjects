package com.example.demo.config;

import com.example.demo.common.AjaxResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2023-12-06
 * Time: 21:37
 *
 */
//实现统一数据返回的保底类
//在返回数据之前,检测数据的类型是否一致,如果不一致,就封装成统一的格式
@ControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice {
    @Autowired
    private ObjectMapper objectMapper; //属性注入

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true; //打开开关
    }

    @SneakyThrows //使用注解抛出objectMapper.writeValueAsString的异常
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        //对数据进行校验和封装,统一数据格式
        if(body instanceof AjaxResult){
            return body; //格式一致,就直接返回
        }
        if(body instanceof String){
            //string有自己的转换格式,需要单独处理
            return objectMapper.writeValueAsString(AjaxResult.success(body));
        }
        return AjaxResult.success(body);
    }
}
