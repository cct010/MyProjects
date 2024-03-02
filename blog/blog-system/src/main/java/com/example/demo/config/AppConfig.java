package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2023-12-07
 * Time: 23:50
 */
@Configuration
public class AppConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;

    @Autowired
    private DedupInterceptor dedupInterceptor;

    @Autowired
    private WebsInterceptor websInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(websInterceptor) //接口访问次数限制
                .order(0);
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**") //不拦截所有静态资源
                .excludePathPatterns("/editor.md/**")
                .excludePathPatterns("/image/**")
                .excludePathPatterns("/js/**")
                .excludePathPatterns("/upload/**")
                .excludePathPatterns("/captcha/**")
                .excludePathPatterns("/login.html") //不拦截登陆和注册网页
                .excludePathPatterns("/reg.html")
                .excludePathPatterns("/user/login") //不拦截登陆和注册方法,获取验证码
                .excludePathPatterns("/user/reg")
                .excludePathPatterns("/user/getcaptcha")
                .order(1);
        registry.addInterceptor(dedupInterceptor) //请求处理去重
                .order(2);
    }
}
