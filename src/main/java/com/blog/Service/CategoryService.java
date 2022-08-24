package com.blog.Service;

import com.blog.vo.CategoryVo;
import com.blog.vo.Result;

public interface CategoryService {
    CategoryVo findcategorysById(Long categoryId);

    Result allCategories();

    Result categoryDetail();

    Result findArticleBycategoryId(Long categoryId);
}
