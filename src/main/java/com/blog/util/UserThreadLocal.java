package com.blog.util;

import com.blog.pojo.SysUser;

/**
 * 线程变量隔离，每个线程中都创建一个ThreadLocal，每个线程之间的ThreadLocal都互不相关
 * 每个线程内部都有个map，调用ThreadLocal.set(object)方法时，把这个ThreadLocal对象作为key(弱引用),object作为值存到了这个内部map里面（强引用)。
 */
public class UserThreadLocal {

    private UserThreadLocal(){}

    private static final ThreadLocal<SysUser> LOCAL = new ThreadLocal();

    public static void put(SysUser sysUser){
        LOCAL.set(sysUser);
    }

    public static SysUser get(){
        return LOCAL.get();
    }

    public static void remove(){
        LOCAL.remove();
    }
}
