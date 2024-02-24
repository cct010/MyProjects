package com.example.demo.service;

import com.example.demo.entity.Articleinfo;
import com.example.demo.entity.Commentinfo;
import com.example.demo.entity.Userinfo;
import com.example.demo.entity.vo.CommentinfoVO;
import com.example.demo.mapper.ArticleMapper;
import com.example.demo.mapper.CommentMapper;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2023-12-11
 * Time: 16:10
 */
@Service
public class CommentService {
    @Resource
    private CommentMapper commentMapper;

//    @Resource
//    private UserService userService;
//
//    @Resource
//    private ArticleService articleService;




    //获取一篇文章的评论,获取指定文章的所有评论,单表查询,没有联表查询
    public List<Commentinfo> getCommentByArticle(Integer articleid){
        return commentMapper.getCommentByArticle(articleid);
    }

    public List<CommentinfoVO> getCommentVOByArticle(Integer articleid){
        return commentMapper.getCommentVOByArticle(articleid);
    }

    //获取一级评论
    public List<CommentinfoVO> getCommentOne(Integer articleid){
        return commentMapper.getCommentOne(articleid);
    }

    //获取二级评论,也就是回复
    public List<CommentinfoVO> getCommentTwo(Integer articleid){
        return commentMapper.getCommentTwo(articleid);
    }

    //获取一篇文章的评论,扩展
    /*
    @SneakyThrows
    public List<CommentinfoVO> getComVOByArticle(Integer articleid){
        //获取里面的所有用户id,分出
        HashSet<Integer> alluser = new HashSet<>();//具有去重特性
        List<CommentinfoVO> comments = getCommentVOByArticle(articleid);//所有评论与回复
        List<CommentinfoVO> commentOnly = getCommentOne(articleid);//一级评论
        List<CommentinfoVO> replyOnly = getCommentTwo(articleid);//二级评论,回复

        if(commentOnly == null){
            return null;
        }
        for(CommentinfoVO comment : comments){
            alluser.add(comment.getUid());
            if(comment.getReplyid()!=null ){
                alluser.add(comment.getReplyid());
            }
        }
        //查询所有获取的用户头像,用户名
        Map<Integer, Userinfo> alluserId = new HashMap<>();
        //Userinfo userinfo = new Userinfo();
        for(int id : alluser){
            Userinfo user = userService.getUserById(id);
            Userinfo userinfo = new Userinfo();
            userinfo.setUsername(user.getUsername());
            userinfo.setId(user.getId());
            userinfo.setPhoto(user.getPhoto());
            alluserId.put(id,userinfo);
        }

        for (Map.Entry<Integer, Userinfo> entry : alluserId.entrySet()) {
            System.out.print("key = " + entry.getKey());
            System.out.println(", value = " + entry.getValue());
        }
        //将用户名称,头像设置进评论,回复
        for(CommentinfoVO commentVO : commentOnly){
            int value = commentVO.getUid(); //获取用户名
            Userinfo userData = alluserId.get(value);
            commentVO.setUsername(userData.getUsername());
            commentVO.setUserphoto(userData.getPhoto());
        }
        if(replyOnly != null ){
            for(CommentinfoVO commentVO : replyOnly){
                int value = commentVO.getUid(); //获取用户名
                Userinfo userData = alluserId.get(value);
                commentVO.setUsername(userData.getUsername());
                commentVO.setUserphoto(userData.getPhoto());
                int valuereply = commentVO.getReplyid(); //获取回复的用户名
                userData = alluserId.get(valuereply);
                commentVO.setReplyname(userData.getUsername());
            }
            //整合评论
            for(CommentinfoVO comment : commentOnly){
                int value = comment.getId();//一级评论commentId
                for(CommentinfoVO reply : replyOnly){
                    int parentId = reply.getParentid(); //找到父commentId
                    if(value == parentId){
                        comment.setReplies(reply);
                    }
                }
            }
        }

        return commentOnly;
    }
     */


    //联表查询,获取一篇文章的一级评论,获取一级评论
    public  List<CommentinfoVO> getFirstCommentByArticle(Integer articleid){
        return commentMapper.getFirstCommentByArticle(articleid);
    }

    //联表查询,获取一篇文章的二级评论,获取二级评论
    public  List<CommentinfoVO> getSecondaryCommentByArticle(Integer articleid){
        return commentMapper.getSecondaryCommentByArticle(articleid);
    }


    //新增一个评论,或回复,返回自增主键id
    public int add(Commentinfo commentinfo){
        return commentMapper.add(commentinfo);
    }

    //删除评论,自己的发表的评论
    //Integer id,Integer uid,一条
    public int deleteOfMineById(Integer id,Integer uid){
        return commentMapper.deleteOfMineById(id,uid);
    }

    //删除评论,二级评论,
    public int deleteSecondaryComment(Integer parentid){
        return commentMapper.deleteSecondaryComment(parentid);
    }

    //删除自己的评论,一篇文章里的所有评论
    //articleid,//删除一篇文章的所有评论
    public int deleteOfMineByArticle(Integer articleid){
        return commentMapper.deleteOfMineByArticle(articleid);
    }

    //查询评论,自己已发表的评论
    public List<CommentinfoVO> getCommentOfMine(Integer uid){
        return commentMapper.getCommentOfMine(uid); // 获取登陆者发表的所有评论
    }

    //查询评论,联表查询
    public List<CommentinfoVO> getCommentsByUid(Integer uid){
        return commentMapper.getCommentsByUid(uid);// 获取登陆者发表的所有评论
    }

    //查询评论,我已发表的评论
    /*
    public List<CommentinfoVO> getCommentOfMine(Integer uid){
        //获取里面的所有文章id,
        HashSet<Integer> allarticle = new HashSet<>();//具有去重特性
        List<CommentinfoVO> comments = commentMapper.getCommentOfMine(uid); // 获取登陆者发表的所有评论

        if(comments == null){
            return null;
        }
        for(CommentinfoVO comment : comments){
            if(comment.getArticleid()!=null){
                allarticle.add(comment.getArticleid());
            }
        }
        //查询所有获取的文章id,文章名称,文章作者名称
        Map<Integer, CommentinfoVO> articleMap = new HashMap<>();
        for(int id : allarticle){
            Articleinfo articleinfo = articleService.getDetail(id);//获取文章详细信息
            CommentinfoVO commentinfoVO = new CommentinfoVO();
            String title = articleinfo.getTitle();//获取title,并且要截取
            if(title.length()>=23){
                title = title.substring(0,23) + "...";
            }
            commentinfoVO.setArticlename(title);//文章详细信息里的title,设置名称
            int author = articleinfo.getUid();//文章详细信息里的id
            Userinfo authorname = userService.getUserById(author);
            commentinfoVO.setArticleauthor(authorname.getUsername());//设置作者名称
            articleMap.put(id,commentinfoVO);//放进去
        }
        //将文章名称,作者名称设置进评论里
        for(CommentinfoVO comment : comments){
            int articleid = comment.getArticleid();
            comment.setArticlename(articleMap.get(articleid).getArticlename());
            comment.setArticleauthor(articleMap.get(articleid).getArticleauthor());
            //处理时间格式
            comment.setShowtime(comment.getCreatetime());
            comment.setCreatetime(null);//创建时间置空
        }
        return comments;
    }

     */
}
