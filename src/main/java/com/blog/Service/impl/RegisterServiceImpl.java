package com.blog.Service.impl;

import com.alibaba.fastjson.JSON;
import com.blog.Service.RegisterService;
import com.blog.Service.SysUserService;
import com.blog.pojo.SysUser;
import com.blog.util.JWTUtils;
import com.blog.vo.ErrorCode;
import com.blog.vo.Result;
import com.blog.vo.params.Register;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RegisterServiceImpl implements RegisterService {

    private static final String slat="sd@d%ad1";

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;



    /**
     *
     * @param register
     * @return
     */
    @Override
    public Result register(Register register) {
        /**
         * 1.判断参数是否合法
         * 2.判断账户是否存在
         * 3.不存在注册账户
         * 4.生成token
         * 5.存入redis
         * 6.加上事务
         */
        String account=register.getAccount();
        String password=register.getPassword();
        String nickname=register.getNickname();
        if (StringUtils.isBlank(account)||StringUtils.isBlank(password)||StringUtils.isBlank(nickname)){
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(),ErrorCode.PARAMS_ERROR.getMsg());
        }

        SysUser userByAccount = sysUserService.findUserByAccount(account);
        if (userByAccount!=null){
            return Result.fail(ErrorCode.ACCOUNT_EXIST.getCode(),ErrorCode.ACCOUNT_EXIST.getMsg());
        }
        SysUser sysUser=new SysUser();
        sysUser.setAccount(account);
        sysUser.setNickname(nickname);
        sysUser.setAdmin(0);
        sysUser.setAvatar("");
        sysUser.setCreateDate(System.currentTimeMillis());
        sysUser.setDeleted(0);
        sysUser.setEmail("");
        sysUser.setLastLogin(System.currentTimeMillis());
        sysUser.setMobilePhoneNumber("");
        sysUser.setStatus("");
        sysUser.setSalt("");
        sysUser.setPassword(DigestUtils.md5Hex(password+slat));
        Boolean b = sysUserService.save(sysUser);
        if (b==false){
            return Result.fail(ErrorCode.SAVE_ER.getCode(),ErrorCode.SAVE_ER.getMsg());
        }

        String token = JWTUtils.getJwtToken(sysUser.getId());

        redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSONString(userByAccount),1, TimeUnit.DAYS);
        return Result.success(token);
    }
}
