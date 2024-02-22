package com.example.demo.mapper;

import com.example.demo.entity.Articleinfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2023-12-08
 * Time: 15:11
 */
//文章
@Mapper
public interface ArticleMapper {
    //获取文章数量
    int getArtCountByUid(@Param("uid") Integer uid);

    //获取同一个人的文章所有列表详细文章
    List<Articleinfo> getMyList(@Param("uid") Integer uid);

    //删除指定文章
    //@Param("id") Integer id,@Param("uid") Integer uid
    int delete(Articleinfo articleinfo);

    //把State修改,文章状态的修改
    int updateState(Articleinfo articleinfo);

    //获取,具体的一篇文章内容
    Articleinfo getDetail(@Param("id") Integer id);

    int incrRCount(@Param("id") Integer id);

    //添加新文章
    int add(Articleinfo articleinfo);

    //修改文章
    int update(Articleinfo articleinfo);

    //获取所有文章
    List<Articleinfo> getListByPage(@Param("psize") Integer psize,
                                    @Param("offsize") Integer offsize);

    //获取所有文章数量
    int getCount();

    //草稿添加,返回自增主键id
    int draftAdd(Articleinfo articleinfo);

    //草稿发表
    int draftupdate(Articleinfo articleinfo);

    //草稿列表的获取
    List<Articleinfo> getMyDraftList(@Param("uid") Integer uid);

}
