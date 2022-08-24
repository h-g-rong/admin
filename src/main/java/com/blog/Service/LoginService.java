package com.blog.Service;

import com.blog.pojo.SysUser;
import com.blog.vo.Result;
import com.blog.vo.params.LoginParams;

public interface LoginService {
    Result login(LoginParams loginParams);
    SysUser checkToken(String token);
    Result logout(String token);
}
