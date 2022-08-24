package com.blog.Service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.blog.Mapper.ArticleMapper;
import com.blog.pojo.Article;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ThreadService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Async("taskExecutorView")
    public void addArticleViewCounts(ArticleMapper articleMapper, Article article) {

//        Integer viewCounts = article.getViewCounts();
//        Article articleUpdate=new Article();
//        articleUpdate.setViewCounts(viewCounts+1);
//        LambdaQueryWrapper<Article> lqw = new LambdaQueryWrapper();
//        lqw.eq(Article::getId,article.getId());
//        lqw.eq(Article::getViewCounts,article.getViewCounts());
//        articleMapper.update(articleUpdate,lqw);
        log.info("进入线程池");
        String articleId = article.getId().toString();
        String articleViewId="articleViewId_"+articleId;
        log.info("key:{}",articleViewId);
        Long increment = redisTemplate.opsForValue().increment(articleViewId, 1);
        log.info("increment:{}",increment);

    }

    @Async("taskExecutorComment")
    public void addArticleCommentCounts(ArticleMapper articleMapper, Long articleId) {

        log.info("进入线程池");
        String articleComment="articleComment_"+articleId.toString();
        log.info("key:{}",articleComment);
        Long increment = redisTemplate.opsForValue().increment(articleComment, 1);
        log.info("increment:{}",increment);



//        Article article = articleMapper.selectById(articleId);
//        log.info("article:{}",article);
//        Integer commentCounts = article.getCommentCounts();
//        Article articleUpdate=new Article();
//        articleUpdate.setCommentCounts(commentCounts+1);
//        articleUpdate.setId(articleId);
//        LambdaUpdateWrapper<Article> lqw = new LambdaUpdateWrapper<>();
//        lqw.eq(Article::getId,article.getId());
//        lqw.eq(Article::getCommentCounts,article.getCommentCounts());
//        log.info("articleUpdate:{}",articleUpdate);
//        articleMapper.update(articleUpdate,lqw);
    }



}
