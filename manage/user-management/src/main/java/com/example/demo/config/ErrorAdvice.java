package com.example.demo.config;

import com.example.demo.common.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-01-16
 * Time: 15:44
 */
@ControllerAdvice
@ResponseBody
@Slf4j
public class ErrorAdvice {
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public AjaxResult Exception(HttpRequestMethodNotSupportedException e){
        if(e.getMessage().contains("Request method")&& e.getMessage().contains("not supported")){
            //只支持post请求
            String[] split = e.getMessage().split(" ");//空格分隔
            String msg = "当前请求不支持"+split[2] +"方法";
            return AjaxResult.fail(-1,msg);
        }
        String msg = "未知错误,无法定位";
        return AjaxResult.fail(-1,msg);
    }


    @ExceptionHandler(Exception.class)
    public AjaxResult Exception(Exception e){
        String msg = "未知错误,无法定位";
        return AjaxResult.fail(-1,msg);
    }
}
