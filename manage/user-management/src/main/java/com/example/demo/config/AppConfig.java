package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-01-15
 * Time: 20:54
 */
@Configuration
public class AppConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Autowired
    private WebsInterceptor websInterceptor;

    @Autowired
    private DedupInterceptor dedupInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(websInterceptor) //接口访问次数限制
                .order(0);
        registry.addInterceptor(loginInterceptor) //用户登陆权限验证
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**")
                .excludePathPatterns("/image/**")
                .excludePathPatterns("/js/**")
                .excludePathPatterns("/upload/**")
                .excludePathPatterns("/login.html")
                .excludePathPatterns("/user/login")
                .excludePathPatterns("/user/getcaptcha")
                .excludePathPatterns("/manage/user/login")
                .excludePathPatterns("/user/uploadimg")
                .order(1); // order越小越先执行
        registry.addInterceptor(dedupInterceptor) //请求处理去重
                .order(3);
    }
}




