package com.blog.handler;

import com.alibaba.fastjson.JSON;
import com.blog.Service.LoginService;
import com.blog.pojo.SysUser;
import com.blog.util.UserThreadLocal;
import com.blog.vo.ErrorCode;
import com.blog.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private LoginService loginService;
    /*
    执行controller方法之前执行
    1.判断请求接口的路径是否访问controller方法（HandlerMethod）
    2.判断token是否为空，未登录
    3.不为空进行登录认证
    4.认证成功放行
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info(".............request....................");
        log.info("uri:{}",request.getRequestURI());
        log.info("method:{}",request.getMethod());



        if (!(handler instanceof HandlerMethod)){
            //handler可能是访问static下的静态资源
            return true;
        }

        //判断token是否为空
        String token = request.getHeader("Authorization");
        if (StringUtils.isBlank(token)){
            Result fail = Result.fail(ErrorCode.NO_LOGIN.getCode(), ErrorCode.NO_LOGIN.getMsg());
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(fail));
            return false;
        }
        log.info("token:{}",token);
        //认证
        SysUser sysUser = loginService.checkToken(token);
        if (sysUser==null){
            Result fail = Result.fail(ErrorCode.NO_LOGIN.getCode(), ErrorCode.NO_LOGIN.getMsg());
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(fail));
            return false;
        }
        UserThreadLocal.put(sysUser);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //删除ThreadLocal中的信息，防止信息泄露
        UserThreadLocal.remove();
    }
}
