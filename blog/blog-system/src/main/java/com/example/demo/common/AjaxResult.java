package com.example.demo.common;

import lombok.Data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2023-12-06
 * Time: 21:25
 */
@Data
// 统一数据格式返回
public class AjaxResult implements Serializable {
    private Integer code; // 状态码
    private String msg; // 状态码描述的信息
    private Object data; //返回的数据

    //操作成功,返回的结果
    public static AjaxResult success(Object data){
        AjaxResult result = new AjaxResult();
        result.setCode(200);
        result.setMsg("");
        result.setData(data);
        return result;
    }
    //重载,操作成功,返回的结果
    public static AjaxResult success(int code,String msg, Object data){
        AjaxResult result = new AjaxResult();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }
    //操作失败,返回的结果
    public static AjaxResult fail(int code, String msg){
        AjaxResult result = new AjaxResult();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }
    public static AjaxResult fail(int code,String msg,Object data){
        AjaxResult result = new AjaxResult();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }
}
