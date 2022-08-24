package com.blog.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.Mapper.ArticleBodyMapper;
import com.blog.Mapper.ArticleMapper;
import com.blog.Mapper.ArticleTagMapper;
import com.blog.Service.ArticleService;
import com.blog.Service.CategoryService;
import com.blog.Service.TagService;
import com.blog.Service.ThreadService;
import com.blog.pojo.Article;
import com.blog.pojo.ArticleBody;
import com.blog.pojo.ArticleTag;
import com.blog.util.UserThreadLocal;
import com.blog.vo.*;
import com.blog.vo.params.ArticleParams;
import com.blog.vo.params.PageParams;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private TagService tagService;

    @Autowired
    private SysUserServiceImpl sysUserService;

    @Autowired
    private ArticleBodyMapper articleBodyMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ThreadService threadService;

    @Autowired
    private ArticleTagMapper articleTagMapper;
    /**
     * 分页查询
     * @param pageParams
     * @return
     */
    @Override
    public Result listarticle(PageParams pageParams) {
        IPage<Article> page = new Page<>(pageParams.getPage(), pageParams.getSize());
        pageParams.setSize(4);
        //System.out.println(pageParams.getPage()+"  @  "+pageParams.getSize());
        LambdaQueryWrapper<Article> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Article::getDeleted,0);
        /**
         * 如果有文章分类传进来则是查看文章分类详情，加上查询条件
         */
        if (pageParams.getCategoryId()!=null){
            lqw.eq(Article::getCategoryId,pageParams.getCategoryId());
        }
        /**
         * 如果有文章标签传进来先通过标签查询文章id，再加上查询条件
         */
        if (pageParams.getTagId()!=null){
            LambdaQueryWrapper<ArticleTag> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(ArticleTag::getTagId,pageParams.getTagId());
            lambdaQueryWrapper.select(ArticleTag::getArticleId);
            List<ArticleTag> articleTags = articleTagMapper.selectList(lambdaQueryWrapper);
            List<Long> ids = new ArrayList();
            for (ArticleTag articleTag : articleTags) {
                ids.add(articleTag.getArticleId());
            }
            if (ids.size()>0){
                //根据list集合里的字段查询
                lqw.in(Article::getId,ids);
            }
        }
        //不能传空串所有判断长度是否大于0
        if (pageParams.getMonth()!=null&&pageParams.getMonth().length()>0
                &&pageParams.getYear()!=null&&pageParams.getYear().length()>0){
            lqw.apply("FROM_UNIXTIME(create_date/1000,'%Y')='"+pageParams.getYear()+"'"+"and FROM_UNIXTIME(create_date/1000,'%m') = "+pageParams.getMonth());

        }
        //如果作者id不为空则是查看已发布文章
        if (pageParams.getAuthorId()!=null){
            lqw.eq(Article::getAuthorId,pageParams.getAuthorId());
        }
        //是否置顶排序
        lqw.orderByDesc(Article::getWeight);
        //按时间倒叙排序
        lqw.orderByDesc(Article::getCreateDate);

        IPage<Article> articleIPage = articleMapper.selectPage(page, lqw);
        List<Article> records = articleIPage.getRecords();
        List<ArticleVo> articleVos=new ArrayList<>();
        //将查询结果复制到vo对象返回给前端
        articleVos=copyList(records,true,true,false,false);

        return Result.success(articleVos);
    }

    /**
     * 最热文章
     * @param limit
     * @return
     */
    @Override
    public Result hotArticle(int limit) {
        LambdaQueryWrapper<Article> lqw = new LambdaQueryWrapper();
        lqw.eq(Article::getDeleted,0);
        lqw.orderByDesc(Article::getViewCounts);
        lqw.select(Article::getAuthorId,Article::getTitle,Article::getId);
        lqw.last("limit "+limit);
        List<Article> articles = articleMapper.selectList(lqw);
       // System.out.println(copyList(articles,false,false,false,false));
        return Result.success(copyList(articles,false,false,false,false));

    }

    /**
     * 最新文章
     * @param limit
     * @return
     */
    @Override
    public Result newArticle(int limit) {
        LambdaQueryWrapper<Article> lqw = new LambdaQueryWrapper();
        lqw.eq(Article::getDeleted,0);
        lqw.orderByDesc(Article::getCreateDate);
        lqw.select(Article::getAuthorId,Article::getTitle,Article::getCreateDate,Article::getId);
        lqw.last("limit "+limit);
        List<Article> articles = articleMapper.selectList(lqw);
       // System.out.println("........................................................");
        //System.out.println(articles);
        return Result.success(copyList(articles,false,false,false,false));
    }

    /**
     * 文章归档
     * @return
     */
    @Override
    public Result listArchives() {

        return Result.success(articleMapper.listArchives());
    }

    /**
     * 文章详情
     * @param articleId
     * @return
     */
    @Override
    public Result findArticleById(Long articleId) {

        Article article = articleMapper.selectById(articleId);
        ArticleVo articleVo = copy(article, true, true, true, true);
        /*
        把浏览数更新操作扔到线程池，和主线程不相关
         */
        threadService.addArticleViewCounts(articleMapper,article);
        //System.out.println("....................");
        //System.out.println(articleVo);
        return Result.success(articleVo);
    }

    /**
     * 发布文章
     * @param articleParams
     * @return
     */
    @Override
    public Result publish(ArticleParams articleParams) {

        Article article = new Article();
        //如果有文章id传进来则是编辑，否则是发布文章
        if (articleParams.getId()!=null){
            article.setId(articleParams.getId());
            article.setSummary(articleParams.getSummary());
            article.setTitle(articleParams.getTitle());
            article.setCreateDate(System.currentTimeMillis());
            article.setCategoryId(articleParams.getCategory().getId());
            articleMapper.updateById(article);
        }else {
            //从ThreadLocal中获取作者id
            Long authorId = UserThreadLocal.get().getId();
            article.setAuthorId(authorId);
            article.setViewCounts(0);
            article.setCategoryId(articleParams.getCategory().getId());
            article.setCommentCounts(0);
            article.setWeight(0);
            article.setTitle(articleParams.getTitle());
            article.setSummary(articleParams.getSummary());
            article.setCreateDate(System.currentTimeMillis());
            articleMapper.insert(article);
        }
        //编辑文章后把之前的文章标签删除
        if (articleParams.getId()!=null){
            LambdaQueryWrapper<ArticleTag> lam=new LambdaQueryWrapper();
            lam.eq(ArticleTag::getArticleId,articleParams.getId());
            articleTagMapper.delete(lam);
        }
        //向数据库添加文章标签
        Long id = article.getId();
        List<TagVo> tags = articleParams.getTags();
        if (tags!=null){
            for (TagVo tag : tags) {
                ArticleTag articleTag = new ArticleTag();
                articleTag.setArticleId(id);
                articleTag.setTagId(tag.getId());
                articleTagMapper.insert(articleTag);
            }
        }
        //添加文章内容
        ArticleBody articleBody = new ArticleBody();
        articleBody.setArticleId(id);

        articleBody.setContent(articleParams.getBody().getContent());
        articleBody.setContentHtml(articleParams.getBody().getContentHtml());
        if (articleParams.getId()!=null){
            Article a = articleMapper.selectById(articleParams.getId());
            articleBody.setId(a.getBodyId());
            articleBodyMapper.updateById(articleBody);
        }else {
            articleBodyMapper.insert(articleBody);
            Long bodyId = articleBody.getId();
            article.setBodyId(bodyId);
            LambdaUpdateWrapper<Article> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(Article::getId, id).set(Article::getBodyId, bodyId);
            articleMapper.update(null, lambdaUpdateWrapper);
        }
        Map<String,String> map = new HashMap<>();
        map.put("id",id.toString());
        //ArticleTag articleTag = new ArticleTag();
        //List<TagVo> tags1 = articleParams.getTags();
        return Result.success(map);
    }

    /**
     * 查询文章
     * @param search
     * @return
     */
    @Override
    public Result searchArticle(String search) {
        LambdaQueryWrapper<Article> searchlqw= new LambdaQueryWrapper();
        searchlqw.eq(Article::getDeleted,0);
        searchlqw.select(Article::getId,Article::getTitle);
        searchlqw.like(Article::getTitle,search);
        searchlqw.orderByDesc(Article::getViewCounts);
        List<Article> articles = articleMapper.selectList(searchlqw);

        return Result.success(copyList(articles,false,false,false,false));
    }

    /**
     * 查看已发布的文章
     * @param authorId
     * @return
     */
    @Override
    public Result findArticleByauthorId(Long authorId) {

       // System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@");
       // System.out.println(authorId);

        IPage<Article> page = new Page<>(1,4);
        LambdaQueryWrapper<Article> lqw = new LambdaQueryWrapper<>();

        //是否置顶排序
        lqw.orderByDesc(Article::getWeight);
        //按时间倒叙排序
        lqw.orderByDesc(Article::getCreateDate);
        lqw.eq(Article::getAuthorId,authorId);

        IPage<Article> articleIPage = articleMapper.selectPage(page, lqw);
        List<Article> records = articleIPage.getRecords();
        List<ArticleVo> articleVos=new ArrayList<>();
        articleVos=copyList(records,true,true,false,false);

        return Result.success(articleVos);

    }

    /**
     * 删除文章
     * @param articleId
     * @return
     */
    @Override
    public Result deletedArticle(Long articleId) {
        LambdaUpdateWrapper<Article> luw = new LambdaUpdateWrapper();
        luw.eq(Article::getId,articleId).set(Article::getDeleted,1);
        int update = articleMapper.update(null, luw);
        Result result = update > 0 ? Result.success(null) : Result.fail(ErrorCode.DELETE_ER.getCode(), ErrorCode.DELETE_ER.getMsg());
        return result;
    }

    private List<ArticleVo> copyList(List<Article> articles,boolean isTag,boolean isAuthor,boolean isBody,boolean iscategorys){
        List<ArticleVo> articleVos=new ArrayList<>();
        for (Article a:articles){
            articleVos.add(copy(a,isTag,isAuthor,isBody,iscategorys));
        }
        return articleVos;
    }

    private ArticleVo copy(Article article,boolean isTag,boolean isAuthor,boolean isBody,boolean iscategorys){
        ArticleVo articleVo = new ArticleVo();
        BeanUtils.copyProperties(article,articleVo);
        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
        if (isTag){
            articleVo.setTags(tagService.findTagsByArticleId(article.getId()));
        }
        if (isAuthor){
            UserVo userVo = sysUserService.findUserVoById(article.getAuthorId());
            articleVo.setAuthor(userVo);
        }
        if (isBody){
            Long bodyId = article.getBodyId();
            articleVo.setBody(findArticleByBodyId(bodyId));
        }
        if (iscategorys){
            CategoryVo categoryVo = categoryService.findcategorysById(article.getCategoryId());
            articleVo.setCategory(categoryVo);
        }
        return articleVo;
    }

    private ArticleBodyVo findArticleByBodyId(Long bodyId) {
        ArticleBody articleBody = articleBodyMapper.selectById(bodyId);
        ArticleBodyVo articleBodyVo = new ArticleBodyVo();
        articleBodyVo.setContent(articleBody.getContent());

        return articleBodyVo;
    }

}
