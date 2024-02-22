package com.example.demo.service;

import com.example.demo.entity.Articleinfo;
import com.example.demo.mapper.ArticleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2023-12-08
 * Time: 15:16
 */
@Service
public class ArticleService {
    @Resource
    private ArticleMapper articleMapper;



    //获取一个用户的所有文章数量
    public int getArtCountByUid(Integer uid){
        return articleMapper.getArtCountByUid(uid);
    }

    //获取一个用户的所有文章详情
    public List<Articleinfo> getMyList(Integer uid){
        return articleMapper.getMyList(uid);
    }

    //删除指定文章,Integer id,Integer uid
    public int delete (Articleinfo articleinfo){
        return articleMapper.delete(articleinfo);
    }

    //删除一片文章,把一篇文章的状态修改成-1,状态-1,设置为删除,不存在,Integer id,Integer uid,Integer state
    public int updateState(Articleinfo articleinfo){
        return articleMapper.updateState(articleinfo);
    }

    //获取一篇具体的文章内容
    public Articleinfo getDetail(Integer id){
        return articleMapper.getDetail(id);
    }

    //阅读量+1
    public int incrRCount(Integer id){
        return articleMapper.incrRCount(id);
    }

    //新增一片文章
    public int add(Articleinfo articleinfo){
        return articleMapper.add(articleinfo);
    }

    //修改文章
    public int update(Articleinfo articleinfo){
        return articleMapper.update(articleinfo);
    }

    //获取所有文章
    public List<Articleinfo> getListByPage(Integer psize,Integer offsize){
        return articleMapper.getListByPage(psize, offsize);
    }

    //获取所有文章数量
    public int getCount(){
        return articleMapper.getCount();
    }

    //添加草稿
    public int draftAdd(Articleinfo articleinfo){
        return articleMapper.draftAdd(articleinfo);
    }

    //修改的草稿,变成发表的文章
    public int draftupdate(Articleinfo articleinfo){
        return articleMapper.draftupdate(articleinfo);
    }

    //我的草稿列表获取
    public List<Articleinfo> getMyDraftList(Integer uid){
        return articleMapper.getMyDraftList(uid);
    }


}
