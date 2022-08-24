package com.blog.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.pojo.Article;
import com.blog.pojo.dos.Archives;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    @Select("select year(FROM_UNIXTIME(create_date/1000))as year,month (FROM_UNIXTIME(create_date/1000))as month,count(*)as count from ms_article where deleted = 0 group by year,month")
    List<Archives> listArchives();
}
