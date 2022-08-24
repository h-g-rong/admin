package com.blog.vo;

public enum  ErrorCode {

    PARAMS_ERROR(10001,"参数有误"),
    ACCOUNT_PWD_NOT_EXIST(10002,"用户名或密码不存在"),
    TOKEN_ER(10003,"TOKEN不合法"),
    ACCOUNT_EXIST(1004,"账户已存在"),
    NO_PERMISSION(70001,"无权限访问"),
    SESSION_TIME_OUT(90001,"会话超时"),
    SAVE_ER(20010,"保存失败"),
    DELETE_ER(20011,"删除失败"),

    NO_LOGIN(90002,"未登录");

    private int code;
    private String msg;

    ErrorCode(int code,String msg){
        this.code=code;
        this.msg=msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
