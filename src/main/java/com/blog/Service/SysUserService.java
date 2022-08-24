package com.blog.Service;

import com.blog.pojo.SysUser;
import com.blog.vo.Result;
import com.blog.vo.UserVo;

public interface SysUserService {
    SysUser findUserById(Long articleId);

    SysUser findUser(String account, String password);

    SysUser findUserByAccount(String account);

    Boolean save(SysUser sysUser);

    Result findUserByToken(String token);

    UserVo findUserVoById(Long authorId);
}
