package com.example.demo.common;

import eu.bitwalker.useragentutils.UserAgent;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-01-15
 * Time: 19:22
 */
@Aspect //表明是一个切面
@Component
@Slf4j
public class AopLog {
    private static final String START_TIME = "request-start";

    //切入点
    @Pointcut("execution( * com.example.demo.controller.*.*(..))")
    public void log(){ }

    //前置操作
    @Before("log()")
    public void beforeLog(JoinPoint point){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
        Map<String,String[]> parameterMap = request.getParameterMap();
        log.info("[请求 url]: {}",request.getRequestURL());
        log.info("[请求 ip]: {}",request.getRemoteAddr());
        log.info("[请求 类名]: {},[请求 方法名]: {}",point.getSignature().getDeclaringTypeName(),
                point.getSignature().getName());
        log.info("[请求 body]: {}", JSONUtil.toJsonStr(point.getArgs()));
        log.info("[请求 参数]: {}",JSONUtil.toJsonStr(parameterMap));
        Long start = System.currentTimeMillis();
        request.setAttribute(START_TIME,start);
    }

    //环绕通知
    @Around("log()")
    public Object aroundLog(ProceedingJoinPoint point) throws Throwable {
        Object result = point.proceed();
        log.info("[返回值]: {}",JSONUtil.toJsonStr(result));
        return result;
    }

    //后置操作
    @AfterReturning("log()")
    public void afterReturning(){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
        Long start = (Long) request.getAttribute(START_TIME);
        Long end = System.currentTimeMillis();
        log.info("[请求 耗时]: {}毫秒",end-start);
        String header = request.getHeader("User-Agent");
        UserAgent userAgent = UserAgent.parseUserAgentString(header);
        log.info("[浏览器类型]: {},[操作系统]: {},[原始User-Agent]: {}",
                userAgent.getBrowser().toString(),userAgent.getOperatingSystem().toString(), header);
    }

}
