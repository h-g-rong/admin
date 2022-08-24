package com.blog.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.Mapper.SysUserMapper;
import com.blog.Service.LoginService;
import com.blog.Service.SysUserService;
import com.blog.pojo.SysUser;
import com.blog.vo.ErrorCode;
import com.blog.vo.LoginUserVo;
import com.blog.vo.Result;
import com.blog.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private LoginService loginService;

    @Override
    public SysUser findUserById(Long articleId) {
        return sysUserMapper.selectById(articleId);
    }

    @Override
    public SysUser findUser(String account, String password) {

        LambdaQueryWrapper<SysUser> lqw=new LambdaQueryWrapper<>();
        lqw.eq(SysUser::getAccount,account);
        lqw.eq(SysUser::getPassword,password);
        lqw.select(SysUser::getAccount,SysUser::getId,SysUser::getAvatar,SysUser::getNickname,SysUser::getPassword);
        lqw.last("limit 1");

        return sysUserMapper.selectOne(lqw);
    }

    @Override
    public SysUser findUserByAccount(String account) {

        LambdaQueryWrapper<SysUser> lqw=new LambdaQueryWrapper<>();
        lqw.eq(SysUser::getAccount,account);
        lqw.last("limit 1");
        return sysUserMapper.selectOne(lqw);
    }

    @Override
    public Boolean save(SysUser sysUser) {
        sysUser.setAvatar("/static/img/avatar.png");
        return sysUserMapper.insert(sysUser)>0;
    }

    @Override
    public Result findUserByToken(String token) {
        /**
         * 1.检验token是否·合法
         * 是否为空，解析是否成功，redis是否存在
         * 2.如果检验失败返回错误
         * 3.如果成功，返回对应的结果LoginUserVo
         */
        SysUser sysUser = loginService.checkToken(token);
        if (sysUser==null){
            return Result.fail(ErrorCode.TOKEN_ER.getCode(),ErrorCode.TOKEN_ER.getMsg());
        }

        LoginUserVo loginUserVo = new LoginUserVo();
        loginUserVo.setId(sysUser.getId());
        loginUserVo.setAccount(sysUser.getAccount());
        loginUserVo.setNickname(sysUser.getNickname());
        loginUserVo.setAvatar(sysUser.getAvatar());

        return Result.success(loginUserVo);
    }

    @Override
    public UserVo findUserVoById(Long authorId) {
        UserVo userVo = new UserVo();
        SysUser sysUser = sysUserMapper.selectById(authorId);
        BeanUtils.copyProperties(sysUser,userVo);
        return userVo;
    }
}
