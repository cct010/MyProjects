package com.example.demo.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-02-13
 * Time: 23:11
 */
//处理重复请求
@Target(ElementType.METHOD)  //  作用于方法上
@Retention(RetentionPolicy.RUNTIME)  //  保留注解到运行时
public @interface DedupLimit {
    long expireTime() default 3000L; //默认过期时间3秒
}
