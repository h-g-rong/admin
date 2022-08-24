package com.blog.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.Mapper.ArticleMapper;
import com.blog.Mapper.CommentsMapper;
import com.blog.Service.CommentsService;
import com.blog.Service.ThreadService;
import com.blog.pojo.Comment;
import com.blog.pojo.SysUser;
import com.blog.util.UserThreadLocal;
import com.blog.vo.CommentVo;
import com.blog.vo.Result;
import com.blog.vo.UserVo;
import com.blog.vo.params.CommentParams;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class CommentsServiceImpl implements CommentsService {

    @Autowired
    private CommentsMapper commentsMapper;

    @Autowired
    private SysUserServiceImpl sysUserService;

    @Autowired
    private ThreadService threadService;

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public Result commentsByArticleId(Long id) {

        LambdaQueryWrapper<Comment> lqw = new LambdaQueryWrapper();
        lqw.eq(Comment::getArticleId,id);
        lqw.eq(Comment::getLevel,1);
        List<Comment> comments = commentsMapper.selectList(lqw);
        List<CommentVo> commentVos = copylist(comments);

        return Result.success(commentVos);
    }

    @Override
    public Result comment(CommentParams commentParams) {

        Comment comment = new Comment();
        SysUser sysUser = UserThreadLocal.get();
        comment.setArticleId(commentParams.getArticleId());
        comment.setAuthorId( sysUser.getId());
        comment.setContent(commentParams.getContent());
        comment.setCreateDate(System.currentTimeMillis());

        Long parent = commentParams.getParent();
        if (parent==null||parent==0){
            comment.setLevel(1);
        }else {
            comment.setLevel(2);
        }
        comment.setParentId( parent == null ? 0:parent);
        comment.setToUid(commentParams.getToUserId() == null ? 0 : commentParams.getToUserId());
        commentsMapper.insert(comment);
        threadService.addArticleCommentCounts(articleMapper,comment.getArticleId());


        return Result.success(null);
    }

    private List<CommentVo> copylist(List<Comment> comments) {
       List<CommentVo> commentVo = new ArrayList();
        for (Comment comment : comments) {
            commentVo.add(copy(comment));
        }
        return commentVo;
    }

    private CommentVo copy(Comment comment) {
        CommentVo commentVo = new CommentVo();
        BeanUtils.copyProperties(comment,commentVo);
        commentVo.setCreatDate(new DateTime(comment.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
        UserVo userVo = sysUserService.findUserVoById(comment.getAuthorId());
        commentVo.setAuthor(userVo);
        if (comment.getLevel()==2){
            commentVo.setToUser(sysUserService.findUserVoById(comment.getToUid()));
            return commentVo;
        }

            LambdaQueryWrapper<Comment> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Comment::getParentId,comment.getId());
            lqw.eq(Comment::getLevel,2);
            List<Comment> comments = commentsMapper.selectList(lqw);
            commentVo.setChildrens(copylist(comments));

//        if (1==comment.getLevel()){
//            List<CommentVo> commentVoList = findCommentByParentId(comment.getId());
//            commentVo.setChildrens(commentVoList);
//        }
//        if (comment.getLevel()>1){
//            UserVo toUserVo=sysUserService.findUserVoById(comment.getToUid());
//            commentVo.setToUser(toUserVo);
//        }

        return commentVo;
        }

    private List<CommentVo> findCommentByParentId(Long id) {
        LambdaQueryWrapper<Comment> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Comment::getParentId,id);
            lqw.eq(Comment::getLevel,2);
            List<Comment> comments = commentsMapper.selectList(lqw);
            return copylist(comments);
    }


}
