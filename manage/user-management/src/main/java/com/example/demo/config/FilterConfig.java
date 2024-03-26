package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.DispatcherType;
import javax.servlet.annotation.WebFilter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-03-24
 * Time: 0:55
 */
@Configuration
public class FilterConfig {
    @Autowired
    private RepeatableFilter repeatableFilter;

    @Bean
    public FilterRegistrationBean filterRegistration()
    {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        //registration.setFilter(new RepeatableFilter());
        registration.setFilter(repeatableFilter);
        registration.addUrlPatterns("/*");
        registration.setName("repeatParFilter");
        // 默认最后处理。注意优先级，如果有其他过滤器需要读参数，必须让该过滤器处于优先
        registration.setOrder(FilterRegistrationBean.LOWEST_PRECEDENCE);
        return registration;
    }

}
