package com.blog.Controller;

import com.blog.Service.RegisterService;
import com.blog.vo.Result;
import com.blog.vo.params.Register;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("register")
public class RegisterController {

    @Autowired
    private RegisterService registerService;
    @PostMapping
    public Result register(@RequestBody Register register){
        return registerService.register(register);
    }
}
