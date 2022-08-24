package com.blog.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.Mapper.CategoryMapper;
import com.blog.Service.CategoryService;
import com.blog.pojo.Category;
import com.blog.vo.CategoryVo;
import com.blog.vo.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public CategoryVo findcategorysById(Long categoryId) {
        Category category = categoryMapper.selectById(categoryId);
        CategoryVo categoryVo = new CategoryVo();
        categoryVo.setAvatar(category.getAvatar());
        categoryVo.setCategoryName(category.getCategoryName());
        categoryVo.setId(category.getId());
       // System.out.println("...................................");
       // System.out.println(categoryVo);
        return categoryVo;
    }

    @Override
    public Result allCategories() {
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.select(Category::getId,Category::getCategoryName);
        List<Category> categories = categoryMapper.selectList(lqw);
        return Result.success(copyList(categories));
    }

    @Override
    public Result categoryDetail() {
        List<Category> categories = categoryMapper.selectList(new LambdaQueryWrapper<>());
        return Result.success(categories);
    }

    @Override
    public Result findArticleBycategoryId(Long categoryId) {

        Category category = categoryMapper.selectById(categoryId);
        return Result.success(category);
    }

    private List<CategoryVo> copyList(List<Category> category){
        List<CategoryVo> categoryVos=new ArrayList<>();
        for (Category a:category){
            categoryVos.add(copy(a));
        }
        return categoryVos;
    }

    private CategoryVo copy(Category category){
        CategoryVo categoryVo = new CategoryVo();
        BeanUtils.copyProperties(category,categoryVo);
        return categoryVo;
    }
}
