package com.example.demo.common;

import lombok.SneakyThrows;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2023-12-21
 * Time: 17:20
 */
//redis的一些工具方法
@Component
public class RedisUtils {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public void set(String name,String value,Duration timeout) {
        // redis 写缓存操作
        //stringRedisTemplate.opsForValue().set(name, String.valueOf(count), Duration.ofSeconds(60));//过期时间
        stringRedisTemplate.opsForValue().set(name, value, timeout);//过期时间
    }
    public String setSession(String name,String sessionId) {
        // redis 写缓存操作
        //stringRedisTemplate.opsForValue().set(name, String.valueOf(count), Duration.ofSeconds(60));//过期时间
        stringRedisTemplate.opsForValue().set(name, sessionId, Duration.ofDays(1));//过期时间1day
        return "redis 存储成功";
    }
    //获取过期时间
    public long getExpireSeconds(String name){
        if(stringRedisTemplate.getExpire(name, TimeUnit.SECONDS)==null) {
            return -1;
        }
        //stringRedisTemplate.getExpire(name);
        int lockTime = Math.toIntExact(stringRedisTemplate.getExpire(name)); //单位是秒
        return lockTime;
    }
    //获取过期时间
    public String getExpire(String name){
        if(stringRedisTemplate.getExpire(name, TimeUnit.SECONDS)==null) {
            return "";
        }
        //stringRedisTemplate.getExpire(name);
        int lockTime = Math.toIntExact(stringRedisTemplate.getExpire(name));
        int hours = lockTime / (60 * 60);
        int minutes = (lockTime % ( 60 * 60)) /60;
        int seconds = lockTime % 60;
        String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return time;
    }

    //读缓存
    public String get(String name) {
        String result = stringRedisTemplate.opsForValue().get(name);
        return result;
    }

    //删除缓存
    public void del(String key){
        stringRedisTemplate.delete(key);
    }


    //set集合里是否有指定value值
    public Boolean sIsMember(String key,String value){
        //Boolean isBlack = RedisUtils.redisTemplate.execute((RedisCallback<Boolean>) connection -> connection.sIsMember(blackList.getBytes(),userId.getBytes()));
        return stringRedisTemplate.execute((RedisCallback<Boolean>) connection -> connection.sIsMember(key.getBytes(),value.getBytes()));
    }

    //set集合添加新value
    public Long sAdd(String key,String value){
        //RedisUtils.redisTemplate.execute((RedisCallback<Long>) connection -> connection.sAdd(blackList.getBytes(),userId.getBytes()));
        return stringRedisTemplate.execute((RedisCallback<Long>) connection -> connection.sAdd(key.getBytes(),value.getBytes()));
    }

    //执行多个redis命令
    //获取有序集合里的value
    //获取指定时间内,key的访问次数value,并删除更早之前的值
    public List<Object> zExecutePipelined(String methodName, long nowTime, int seconds){
        List<Object> results = stringRedisTemplate.executePipelined(new RedisCallback<Object>() {
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection stringRedisConn = (StringRedisConnection)connection;
                stringRedisConn.zAdd(methodName, nowTime , nowTime+ " "); //k,s,v
                stringRedisConn.expire(methodName,seconds* 10L); //设置过期时间,对一个已经设置了过期时间的key使用expire命令，可以更新其过期时间。
                stringRedisConn.zRemRangeByScore(methodName, 0, nowTime - seconds * 1000);//  删除窗口之外的数据 k,min,max
                stringRedisConn.zCard(methodName);//  获取窗口内的访问次数
                return null;
            }
        });
        return results;
    }


    //判断key值是否存在,true为设置成功,false说明设置失败/之前已经设置过
    public Boolean setIfAbsent(String key,String value,long expireTime){
        Boolean firstSet = stringRedisTemplate.execute((RedisCallback<Boolean>) connection -> connection.set(key.getBytes(), value.getBytes(),
                Expiration.milliseconds(expireTime), RedisStringCommands.SetOption.SET_IF_ABSENT)); //(如果没有这个key 则设置成功 ，如果已经存在则不成功)
        return firstSet;
    }

    //获取key的value ,并同时删除key
    public String deleteKey(String key){
        List<Object> results = stringRedisTemplate.executePipelined(new RedisCallback<Object>() {
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection stringRedisConn = (StringRedisConnection)connection;
                stringRedisConn.get(key);
                stringRedisConn.del(key);
                return null;
            }
        });
        if(results.get(0) == null)return "-1";
        //System.out.println(results.get(0));
        return (String) results.get(0);
    }


}
