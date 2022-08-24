package com.blog.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.Mapper.TagMapper;
import com.blog.Service.TagService;
import com.blog.pojo.Tag;
import com.blog.vo.Result;
import com.blog.vo.TagVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Override
    public List<TagVo> findTagsByArticleId(Long articleId) {
        List<Tag> tagsByArticleId = tagMapper.findTagsByArticleId(articleId);
        //System.out.println("..........................");
        //System.out.println(tagsByArticleId);
//        for (Tag t:tagsByArticleId){
//            System.out.println(t);
//        }
        return copyList(tagsByArticleId);
    }

    @Override
    public Result hots(int limit) {

        List<Long> hotTagIds = tagMapper.findHotTagIds(limit);

        if (CollectionUtils.isEmpty(hotTagIds)){
            return Result.success(Collections.emptyList());
        }
        //List<Tag> tags =new ArrayList<>();
//        for (long a:hotTagIds){
//            tags.add(tagMapper.selectById(a));
//        }
        List<Tag> tags = tagMapper.findHotTagName(hotTagIds);
        //System.out.println(tags);
        List<TagVo> tagVos = copyList(tags);
        return Result.success(tagVos);
    }

    @Override
    public Result findAll() {
        LambdaQueryWrapper<Tag> lqw = new LambdaQueryWrapper<>();
        lqw.select(Tag::getId,Tag::getTagName);
        List<Tag> tags = tagMapper.selectList(lqw);
        return Result.success(copyList(tags));
    }

    @Override
    public Result tagsDetail() {
        List<Tag> tags = tagMapper.selectList(new LambdaQueryWrapper<>());
        return Result.success(tags);
    }

    @Override
    public Result findArticleBytagId(Long id) {
        Tag tag = tagMapper.selectById(id);
        return Result.success(tag);
    }

    private List<TagVo> copyList(List<Tag> tags){
        List<TagVo> TagVos=new ArrayList<>();
        for (Tag a:tags){
           TagVos.add(copy(a));
        }
        return TagVos;
    }

    private TagVo copy(Tag tag){
        TagVo tagVo = new TagVo();
        BeanUtils.copyProperties(tag,tagVo);
        return tagVo;
    }

}
