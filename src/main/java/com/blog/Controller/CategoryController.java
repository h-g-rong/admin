package com.blog.Controller;


import com.blog.Service.CategoryService;
import com.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("categorys")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public Result categories(){
        return categoryService.allCategories();
    }

    @GetMapping("detail")
    public Result categoryDetail(){
        return categoryService.categoryDetail();
    }

    @GetMapping("/detail/{id}")
    public Result categoryList(@PathVariable("id") Long categoryId){
        return categoryService.findArticleBycategoryId(categoryId);
    }

}
