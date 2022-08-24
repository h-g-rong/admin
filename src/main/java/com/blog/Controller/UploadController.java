package com.blog.Controller;

import com.blog.util.QiNiuUtils;
import com.blog.vo.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("upload")
public class UploadController {

    @Autowired
    private QiNiuUtils qiNiuUtils;

    @PostMapping
    public Result upload(@RequestParam("image") MultipartFile file){
        //原始文件名称
        String originalFilename = file.getOriginalFilename();
        //生成唯一的文件名
        String fileName = UUID.randomUUID().toString() + "." + StringUtils.substringAfterLast(originalFilename, ".");
        //上传到七牛云

        boolean upload = qiNiuUtils.upload(file, fileName);
        if (upload){
            //System.out.println(QiNiuUtils.url+fileName);
            return Result.success(QiNiuUtils.url+fileName);
        }
        return Result.fail(20001,"上传失败");

    }
}
