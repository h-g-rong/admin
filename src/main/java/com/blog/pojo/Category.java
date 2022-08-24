package com.blog.pojo;

import lombok.Data;

/**
 * 文章分类
 */
@Data
public class Category {
    private Long id;
    private String avatar;
    private String categoryName;
    private String description;
}
