package com.blog.Service.impl;

import com.alibaba.fastjson.JSON;
import com.blog.Service.LoginService;
import com.blog.Service.SysUserService;
import com.blog.pojo.SysUser;
import com.blog.util.JWTUtils;
import com.blog.vo.ErrorCode;
import com.blog.vo.Result;
import com.blog.vo.params.LoginParams;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    private static final String slat="sd@d%ad1";

    @Override
    public Result login(LoginParams loginParams) {
        /**
         * 1.检查参数是否合法
         * 2.根据用户名和密码去user表查询是否存在
         * 3.如果不存在，登陆失败
         * 4.如果存在使用jwt生成token返回给前端
         * 5.token放入redis中，token：user信息 存储，设置过期时间
         * (登录认证的时候，先认证token字符串是否合法，去redis认证是否存在)
         */
        String account = loginParams.getAccount();
        String password = loginParams.getPassword();
        if (StringUtils.isBlank(account)||StringUtils.isBlank(password)){
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(),ErrorCode.PARAMS_ERROR.getMsg());
        }
        password= DigestUtils.md5Hex(password+slat);
        SysUser user = sysUserService.findUser(account, password);
       // System.out.println("........................");
       // System.out.println(user.getPassword());
        if (user==null){
            return Result.fail(ErrorCode.ACCOUNT_PWD_NOT_EXIST.getCode(),ErrorCode.ACCOUNT_PWD_NOT_EXIST.getMsg());
        }
        String token = JWTUtils.getJwtToken(user.getId());
      //  System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
       // System.out.println(token);

        redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSONString(user),1, TimeUnit.DAYS);
        return Result.success(token);
    }

    @Override
    public SysUser checkToken(String token) {

        if(StringUtils.isBlank(token)){
            return null;
        }

        Map<String, Object> checkToken = JWTUtils.checkToken(token);
        System.out.println("...................................");
        System.out.println(checkToken);
        if (checkToken==null){
            return null;
        }

        String userJson = redisTemplate.opsForValue().get("TOKEN_" + token);
        System.out.println(userJson);
        if (StringUtils.isBlank(userJson)){
            return null;
        }

        SysUser sysUser = JSON.parseObject(userJson, SysUser.class);
        System.out.println("........................");
        System.out.println(sysUser);

        return sysUser;
    }

    @Override
    public Result logout(String token) {
        redisTemplate.delete("TOKEN_"+token);
        return Result.success(null);
    }


}
