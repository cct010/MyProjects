package com.example.demo.mapper;

import com.example.demo.entity.Articleinfo;
import com.example.demo.entity.Commentinfo;
import com.example.demo.entity.vo.CommentinfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2023-12-11
 * Time: 15:59
 */
@Mapper
public interface CommentMapper {
    //获取评论,单表查询
    List<Commentinfo> getCommentByArticle(@Param("articleid") Integer articleid);

    //获取评论,扩展,单表查询
    List<CommentinfoVO> getCommentVOByArticle(@Param("articleid") Integer articleid);

    //获取评论,一级评论,单表查询
    List<CommentinfoVO> getCommentOne(@Param("articleid") Integer articleid);

    //获取评论,二级评论,回复,单表查询
    List<CommentinfoVO> getCommentTwo(@Param("articleid") Integer articleid);

    //获取一篇文章的一级评论,联表查询,一级评论,评论用户的名称和头像
    List<CommentinfoVO> getFirstCommentByArticle(@Param("articleid") Integer articleid);

    //获取一篇文章的二级评论,联表查询,二级评论,评论者和被评论者的用户名
    List<CommentinfoVO> getSecondaryCommentByArticle(@Param("articleid") Integer articleid);


    //新增评论
    int add(Commentinfo commentinfo);

    //删除评论,@Param("id") Integer id ,@Param("uid") Integer uid
    int deleteOfMineById(@Param("id") Integer id ,@Param("uid") Integer uid);

    //删除评论,二级评论
    int deleteSecondaryComment(@Param("parentid") Integer parentid);

    //删除评论,一篇文章的所有评论
    int deleteOfMineByArticle(@Param("articleid") Integer articleid);

    //获取评论,我已发表的评论,单表查询
    List<CommentinfoVO> getCommentOfMine(@Param("uid") Integer id);

    //获取评论,我已发表的评论,联表查询
    List<CommentinfoVO> getCommentsByUid(@Param("uid") Integer id);
}
