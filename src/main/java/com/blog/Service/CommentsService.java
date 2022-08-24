package com.blog.Service;

import com.blog.vo.Result;
import com.blog.vo.params.CommentParams;

public interface CommentsService {
    Result commentsByArticleId(Long id);

    Result comment(CommentParams commentParams);
}
