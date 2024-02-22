package com.example.demo.config;

import com.example.demo.common.AjaxResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.DataTruncation;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2023-12-24
 * Time: 22:26
 */
//统一的异常处理
@ControllerAdvice
@ResponseBody
public class ErrorAdvice {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public AjaxResult sqlIntegerException(SQLIntegrityConstraintViolationException e){
        if(e.getMessage().contains("Duplicate entry")){
            //数据库注册用户名,插入错误
            String[] split = e.getMessage().split(" ");//空格分隔
            String msg = split[2]+"已存在!";
            return AjaxResult.fail(-1,msg);
        }
        String msg = "未知错误,无法定位!";
        //return AjaxResult.fail(-1,msg);
        return AjaxResult.fail(-1,msg);
    }

    //@ExceptionHandler(MysqlDataTruncation.class)
    @ExceptionHandler(DataTruncation.class)
    public AjaxResult sqlException(DataTruncation e){
        if(e.getMessage().contains("Data truncation: Data too long for column 'title'")){
            //数据库,插入标题,超过100字符
            String msg = "标题超过100字符!请减少标题字数!";
            return AjaxResult.fail(-1,msg);
        }
        String msg = "未知错误,无法定位!";
        //return AjaxResult.fail(-1,msg);
        return AjaxResult.fail(-1,msg);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public AjaxResult methodException(HttpRequestMethodNotSupportedException e){
        if(e.getMessage().contains("Request method")&& e.getMessage().contains("not supported")){
            //只支持post请求
            String[] split = e.getMessage().split(" ");//空格分隔
            String msg = "当前请求不支持"+split[2] +"方法!";
            return AjaxResult.fail(-1,msg);
        }
        String msg = "未知错误,无法定位!";
        return AjaxResult.fail(-1,msg);
    }

    @ExceptionHandler(Exception.class)
    public AjaxResult Exception(Exception e){
        String msg = "未知错误,无法定位!";
        return AjaxResult.fail(-1,msg);
    }

}
