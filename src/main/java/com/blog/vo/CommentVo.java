package com.blog.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

@Data
public class CommentVo {
    //分布式id比较长，传到前端有精度损失，转为String进行传输
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String content;
    private String creatDate;

    private UserVo author;

    private UserVo toUser;
    private List<CommentVo> childrens;


    private Integer level;


}
