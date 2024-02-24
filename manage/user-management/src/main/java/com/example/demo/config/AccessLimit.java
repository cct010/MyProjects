package com.example.demo.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-02-12
 * Time: 22:21
 */
//自定义注解
//接口配置,允许访问次数
@Target(ElementType.METHOD)  //  作用于方法上
@Retention(RetentionPolicy.RUNTIME)  //  保留注解到运行时
public @interface AccessLimit {
    //  定义的两个注解参数
    int maxCount(); //最大允许访问数量
    int seconds(); //单位时间(秒)
}
