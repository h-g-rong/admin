package com.blog.handler;

import com.blog.vo.ErrorCode;
import com.blog.vo.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;

//@RestControllerAdvice
public class AllExceptionHandler {



    @ExceptionHandler
    public Result doException(Exception ex){
        //记录日记
        //通知运维
        //通知开发
        ex.printStackTrace();
        return Result.fail(ErrorCode.SESSION_TIME_OUT.getCode(),ErrorCode.SESSION_TIME_OUT.getMsg());
    }


}
