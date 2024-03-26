package com.example.demo.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-03-05
 * Time: 20:28
 */
//获取配置文件的项目路径
@Data
@Component
public class MyContextPath {
    @Value("${server.servlet.context-path}") //只获取配置文件中的某项值,项目路径
    String contextPath;
}
