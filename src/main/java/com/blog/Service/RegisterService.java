package com.blog.Service;

import com.blog.vo.Result;
import com.blog.vo.params.Register;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface RegisterService {

    Result register(Register register);

}
