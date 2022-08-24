package com.blog.Service;

import com.blog.vo.Result;
import com.blog.vo.TagVo;

import java.util.List;

public interface TagService {

    List<TagVo> findTagsByArticleId(Long articleId);

    Result hots(int limit);

    Result findAll();

    Result tagsDetail();

    Result findArticleBytagId(Long id);
}
