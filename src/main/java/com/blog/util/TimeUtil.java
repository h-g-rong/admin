package com.blog.util;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.blog.Mapper.ArticleMapper;
import com.blog.pojo.Article;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@Slf4j
public class TimeUtil {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private ArticleMapper articleMapper;

    //此处为间隔一秒
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(fixedDelay = 60 * 1000,initialDelay=1000)
    public void fixedDelay() throws InterruptedException {
        log.info("定时器开启");
        String pattern = "articleViewId_*";
        //获取全部文章id的key
        Set<String> keys = redisTemplate.keys(pattern);
        log.info("浏览量更新文章id:{}",keys);
        for (String key : keys) {
            // 将key拆分
            String[] split = key.split("_");
            long id = Long.parseLong(split[1]);
            log.info(split[1]);
            String s = redisTemplate.opsForValue().get(key);
            int value= Integer.parseInt(s);
            log.info("浏览量加：{}",value);
            Article article = articleMapper.selectById(id);
            Integer viewCounts = article.getViewCounts();
            LambdaUpdateWrapper<Article> lqw = new LambdaUpdateWrapper<>();
            lqw.eq(Article::getId,id).set(Article::getViewCounts, viewCounts+value);
            articleMapper.update(null, lqw);
            log.info("文章：{},浏览量更新成功",split[1]);
            redisTemplate.delete(key);
        }

        String pattern2 = "articleComment_*";
        //获取全部文章id的key
        Set<String> keys2 = redisTemplate.keys(pattern2);
        log.info("评论数更新文章id:{}",keys2);
        for (String key2 : keys2) {
            // 将key拆分
            String[] split = key2.split("_");
            long id = Long.parseLong(split[1]);
            log.info(split[1]);
            String s = redisTemplate.opsForValue().get(key2);
            int value= Integer.parseInt(s);
            log.info("评论数加：{}",value);
            Article article = articleMapper.selectById(id);
            Integer commentCounts = article.getCommentCounts();
            LambdaUpdateWrapper<Article> lqw = new LambdaUpdateWrapper<>();
            lqw.eq(Article::getId,id).set(Article::getCommentCounts, commentCounts+value);
            articleMapper.update(null, lqw);
            log.info("文章：{},评论数更新成功",split[1]);
            redisTemplate.delete(key2);
        }
    }

}
