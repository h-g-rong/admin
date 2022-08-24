package com.blog.pojo;

import lombok.Data;

@Data
public class ArticleTag {
    private Long id;
    private Long ArticleId;
    private Long tagId;
}
