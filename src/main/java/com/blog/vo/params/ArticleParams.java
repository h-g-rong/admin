package com.blog.vo.params;

import com.blog.vo.CategoryVo;
import com.blog.vo.TagVo;
import lombok.Data;

import java.util.List;

@Data
public class ArticleParams {
    private String title;
    private Long id;
    private ArticleBodyParam body;
    private CategoryVo category;
    private String summary;
    private List<TagVo> tags;
    private String search;

}
