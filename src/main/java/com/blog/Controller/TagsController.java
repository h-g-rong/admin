package com.blog.Controller;

import com.blog.Service.TagService;
import com.blog.common.cache.Cache;
import com.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tags")
public class TagsController {

    @Autowired
    private TagService tagService;

    @GetMapping("/hot")
    @Cache(expire = 1 * 60 * 10000,name = "hotTag")
    public Result hot(){
        int limit = 6;
        return tagService.hots(limit);
    }

    @GetMapping
    public Result findAll(){
        return tagService.findAll();
    }

    @GetMapping("/detail")
    public Result tagsDetail(){
        return tagService.tagsDetail();
    }

    @GetMapping("/detail/{id}")
    public Result tagsDetailList(@PathVariable("id") Long id){
        return tagService.findArticleBytagId(id);
    }

}
