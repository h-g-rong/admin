package com.blog.Controller;

import com.blog.Service.ArticleService;
import com.blog.common.aop.LogAnnotation;
import com.blog.common.cache.Cache;
import com.blog.vo.Result;
import com.blog.vo.params.ArticleParams;
import com.blog.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("articles")
public class ArticleController {


    @Autowired
    private ArticleService articleService;


    /**
     * 首页文章列表
     * @param pageParams
     * @return
     */
    @PostMapping
    @LogAnnotation(module = "文章",operator = "获取文章列表")
    //@Cache(expire = 1 * 60 * 10000,name = "listArticle")
    public Result listArticle(@RequestBody PageParams pageParams){
//        System.out.println("..................");
//        System.out.println(articleService.listarticle(pageParams));
        return articleService.listarticle(pageParams);
    }


    @PostMapping("/hot")
    @Cache(expire = 1 * 60 * 10000,name = "hotArticle")
    public Result hotArticle(){
        int limit=5;
        return articleService.hotArticle(limit);
    }

    @PostMapping("/new")
    //@Cache(expire = 1 * 60 * 10000,name = "newArticle")
    public Result newArticle(){
        int limit=5;
        return articleService.newArticle(limit);
    }

    @PostMapping("/listArchives")
    //@Cache(expire = 1 * 60 * 10000,name = "listArticles")
    public Result listArchives(){
        return articleService.listArchives();
    }

    @PostMapping("/view/{id}")
    public Result ArticleDetails(@PathVariable("id") Long articleId){
            return articleService.findArticleById(articleId);
    }

    @PostMapping("/publish")
    public Result publish(@RequestBody ArticleParams articleParams){

        return articleService.publish(articleParams);
    }

    @PostMapping("/search")
    public Result search(@RequestBody ArticleParams articleParams){
        return articleService.searchArticle(articleParams.getSearch());
    }

    @PostMapping("/author/{id}")
    public Result ArticleByAuthor(@PathVariable("id") Long authorId){
        return articleService.findArticleByauthorId(authorId);
    }

    @PostMapping("/deleted/{id}")
    public Result ArticleDeleted(@PathVariable("id") Long articleId){
        return articleService.deletedArticle(articleId);
    }



}
