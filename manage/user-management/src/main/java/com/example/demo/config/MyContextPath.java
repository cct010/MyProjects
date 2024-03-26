package com.example.demo.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-03-05
 * Time: 18:17
 */
//读取配置文件
@Data
@Component
public class MyContextPath {
    @Value("${server.servlet.context-path}") //只获取配置文件中的某项值,项目路径
    String contextPath;
}




//@Data
//@Component
//@ConfigurationProperties(prefix = "server.servlet" ) //专门编写了一个 JavaBean 来和配置文件进行映射
//public class MyConfig {
//    String contextPath;
//}
