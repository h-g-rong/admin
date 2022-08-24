package com.blog.Controller;


import com.blog.Service.CommentsService;
import com.blog.common.aop.LogAnnotation;
import com.blog.vo.Result;
import com.blog.vo.params.CommentParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("comments")
public class CommentsController {

    @Autowired
    private CommentsService commentsService;

    @GetMapping("/article/{id}")
    public Result comments(@PathVariable("id") Long id){

        return commentsService.commentsByArticleId(id);
    }

    @LogAnnotation(module = "评论",operator = "评论")
    @PostMapping("/create/change")
    public Result comment(@RequestBody CommentParams commentParams){

        return commentsService.comment(commentParams);
    }
}
