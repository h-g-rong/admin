package com.blog.Service;

import com.blog.vo.Result;
import com.blog.vo.params.ArticleParams;
import com.blog.vo.params.PageParams;


public interface ArticleService {

    /**
     * 首页 文章列表
     * @param pageParams
     * @return
     */
    public Result listarticle(PageParams pageParams);

    public Result hotArticle(int limit);

    public Result newArticle(int limit);

    public Result listArchives();

    Result findArticleById(Long articleId);

    Result publish(ArticleParams articleParams);

    Result searchArticle(String search);

    Result findArticleByauthorId(Long authorId);

    Result deletedArticle(Long articleId);
}
