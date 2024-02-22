package com.example.demo.config;

import com.example.demo.common.PreHandleFalseResponse;
import com.example.demo.common.RedisUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-02-13
 * Time: 23:12
 */
//限制接口访问次数
@Component
public class WebsInterceptor implements HandlerInterceptor {
    @Resource
    private PreHandleFalseResponse handleFalseResponse; //拦截器false时返回的response信息

    @Resource
    private RedisUtils redisUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //在黑名单里,直接拒绝访问
        String blackList = "blackList";
        String userId = request.getRemoteAddr(); //用户ip
        Boolean isBlack = redisUtils.sIsMember(blackList,userId);//判断set集合里是否有此ip
        if(Boolean.TRUE.equals(isBlack)){
            handleFalseResponse.preHandleFalseResponseJsonBody(response,"拒绝访问!");
            return false;
        }

        //  判断是否属于方法handler
        if (handler instanceof HandlerMethod) {
            //  获取判断是否含有注解
            AccessLimit accessLimit = ((HandlerMethod) handler).getMethodAnnotation(AccessLimit.class);
            //  没有注解标记 直接返回允许通行
            if (accessLimit == null) {
                return true;
            }
            //  获取 限流的参数
            int maxCount = accessLimit.maxCount();
            int seconds = accessLimit.seconds();

            //  获取方法名 这里实现对方法级别的限制访问
            String methodName = ((HandlerMethod) handler).getMethod().getName();
            String method = request.getRequestURI(); //获取web项目的相对路径
            //  获取当前时间戳
            long nowTime = System.currentTimeMillis();

            //  设置方法访问的 时间戳
            //把用户IP和接口方法名拼接成 redis 的 key
            String redisKey = "access:U=" + userId + "M=" + method;

            //用于执行多个 Redis 命令
            //将所有命令缓存在管道中，最终一次性发送给 Redis 服务器。这样可以减少与 Redis 服务器的通信次数，提高命令的执行效率和吞吐量。
            List<Object> result = redisUtils.zExecutePipelined(redisKey,nowTime,seconds);
            Long count = (Long) result.get(3);

            //  如果超出访问限制 限流/拉黑
            if (count > maxCount) {
                Long addResult = redisUtils.sAdd(blackList,userId);//加入黑名单,使用set集合类型,无序并唯一,r=1
                if(addResult==1){
                    //加入黑名单成功之后,删除此有序集合
                    redisUtils.del(redisKey);
                }
                handleFalseResponse.preHandleFalseResponseJsonBody(response,"拒绝访问!");
                return false;
            }
        }
        return true;
    }
}
