package com.example.demo.config;

import cn.hutool.json.JSONUtil;
import com.example.demo.common.AjaxResult;
import com.example.demo.common.PreHandleFalseResponse;
import com.example.demo.common.RedisUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-02-12
 * Time: 22:26
 */
//请求去重处理
@Component
public class DedupInterceptor implements HandlerInterceptor {
    @Resource
    private PreHandleFalseResponse handleFalseResponse;

    @Resource
    private RedisUtils redisUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //  判断是否属于方法handler
        if (handler instanceof HandlerMethod) {
            //  获取判断是否含有注解
            DedupLimit dedupLimit = ((HandlerMethod) handler).getMethodAnnotation(DedupLimit.class);
            //  没有注解标记 直接返回允许通行
            if (dedupLimit == null) {
                return true;
            }
            //  获取参数
            long expireTime = dedupLimit.expireTime();
            //  获取方法名 这里实现对方法级别的限制访问
            String methodName = ((HandlerMethod) handler).getMethod().getName();
            String method = request.getRequestURI(); //获取web项目的相对路径
            //获取request参数,需要排序,不然位置不同,生成的md5也不一样
            Map<String, String[]> parameterMap = request.getParameterMap();
            String parameter = "";
            if(!parameterMap.isEmpty()){
                List<Map.Entry<String,String[]>> lstEntry=new ArrayList<>(parameterMap.entrySet());
                lstEntry.sort(((o1, o2) -> {
                    return o1.getKey().compareTo(o2.getKey());
                }));
                //String parameter = JSONUtil.toJsonStr(parameterMap);
                parameter = JSONUtil.toJsonStr(lstEntry);
            }
            Map<String, String> map = new HashMap<String, String>();
            map.put("url", String.valueOf(request.getRequestURL()));
            map.put("parameter",parameter);

            ObjectMapper objectMapper = new ObjectMapper();
            String paramTreeMapJSON = objectMapper.writeValueAsString(map);
            String md5deDupParam = DigestUtils.md5DigestAsHex(paramTreeMapJSON.getBytes());  //进行MD5

            //存储到redis
            String userId = request.getRemoteAddr(); //用户ip
            String KEY = "dedup:U=" + userId + "M=" + method + "P=" + md5deDupParam;


            //long expireTime =  1900000;// 3000毫秒过期，3000ms内的重复请求会认为重复
            long expireAt = System.currentTimeMillis() + expireTime;
            String val = "expireAt@" + expireAt;

            //redis key还存在的话要就认为请求是重复的
            Boolean firstSet = redisUtils.setIfAbsent(KEY,val,expireTime); //key还存在的话,就认为请求是重复的

            if (firstSet != null && firstSet) {// 第一次访问
                return true;
            } else { // redis值已存在，认为是重复了
                //是重复请求,设置response返回信息
                handleFalseResponse.preHandleFalseResponseJsonBody(response,"重复请求!");
            }
            return false;
        }
        return true;
    }
}
