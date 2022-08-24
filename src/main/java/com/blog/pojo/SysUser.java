package com.blog.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class SysUser {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /*
    * 账号*/
    private String account;

    /*
    * 是否管理员*/
    private Integer admin;

    /*
    * 头像*/
    private String avatar;

    /*
    * 注册时间*/
    private Long createDate;

    /*
    * 是否删除*/
    private Integer deleted;

    /*
    * 邮箱*/
    private String email;

    /*
    * 最后登陆时间*/
    private Long lastLogin;

    /*
    * 手机号*/
    private String mobilePhoneNumber;

    /*
    * 昵称*/
    private String nickname;

    /*
    * 密码*/
    private String password;

    /*
    * 加密盐*/
    private String salt;

    /*
    * 状态*/
    private String status;
}
