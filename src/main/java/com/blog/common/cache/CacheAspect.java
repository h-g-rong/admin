package com.blog.common.cache;

import com.alibaba.fastjson.JSON;
import com.blog.vo.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Duration;

@Component
@Aspect//切面 定义了通知和切点的关系
@Slf4j
@Order(1)
public class CacheAspect {

    @Pointcut("@annotation(com.blog.common.cache.Cache)")
    public void pointcut(){}

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint){
        /*
         getSignature()获取被增强的方法相关信息.其后续方法有两个
         getDeclaringTypeName: 返回方法所在的包名和类名
         getName(): 返回方法名
         */
        try {
        Signature signature = joinPoint.getSignature();
        //获取类名
        String className = joinPoint.getTarget().getClass().getSimpleName();
        //调用的方法名
        String methodName = signature.getName();
        //获取参数类别
        Class[] parameterTypes = new Class[joinPoint.getArgs().length];
        //获取参数值
        Object[] args = joinPoint.getArgs();
        //参数
        String params = "";
        for(int i=0; i<args.length; i++) {
            if(args[i] != null) {
                params += JSON.toJSONString(args[i]);
                parameterTypes[i] = args[i].getClass();
            }else {
                parameterTypes[i] = null;
            }
        }
        if (StringUtils.isNotEmpty(params)) {
            //加密 以防出现key过长以及字符转义获取不到的情况
            params = DigestUtils.md5Hex(params);
        }
        Method method = joinPoint.getSignature().getDeclaringType().getMethod(methodName, parameterTypes);
        //获取Cache注解
        Cache annotation = method.getAnnotation(Cache.class);
        //缓存过期时间
        long expire = annotation.expire();
        //缓存名称
        String name = annotation.name();
        //先从redis获取
        String redisKey = name + "::" + className+"::"+methodName+"::"+params;
        String redisValue = redisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isNotEmpty(redisValue)){
            log.info("走了缓存~~~,{},{}",className,methodName);
            log.info("key,{}",redisKey);
            return JSON.parseObject(redisValue, Result.class);
        }
        Object proceed = joinPoint.proceed();
        redisTemplate.opsForValue().set(redisKey, new ObjectMapper().writeValueAsString(proceed), Duration.ofMillis(expire));
        log.info("缓存时间  ,{}",Duration.ofMillis(expire));
        log.info("存入缓存~~~ {},{}",className,methodName);
        return proceed;
    } catch (Throwable throwable) {
        throwable.printStackTrace();
    }
        return Result.fail(-999,"系统错误");
    }
}

