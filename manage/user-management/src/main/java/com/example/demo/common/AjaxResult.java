package com.example.demo.common;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-01-15
 * Time: 18:03
 */
@Data
//统一返回格式
public class AjaxResult {
    private Integer code;
    private String msg;
    private Object data;

    public static AjaxResult success(Object data){
        AjaxResult result = new AjaxResult();
        result.setCode(200);
        result.setMsg("");
        result.setData(data);
        return result;
    }

    public static AjaxResult success(int code,String msg ,Object data){
        AjaxResult result = new AjaxResult();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    public static AjaxResult fail(int code,String msg){
        AjaxResult result = new AjaxResult();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }

    public static AjaxResult fail (int code ,String msg ,Object data){
        AjaxResult result = new AjaxResult();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

}
