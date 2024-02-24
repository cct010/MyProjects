package com.example.demo.controller;

import com.example.demo.common.AjaxResult;
import com.example.demo.common.UserSessionUtils;
import com.example.demo.config.AccessLimit;
import com.example.demo.config.DedupLimit;
import com.example.demo.entity.Articleinfo;
import com.example.demo.entity.Commentinfo;
import com.example.demo.entity.Userinfo;
import com.example.demo.entity.vo.CommentinfoVO;
import com.example.demo.service.ArticleService;
import com.example.demo.service.CommentService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2023-12-11
 * Time: 19:40
 */
@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private ArticleService articleService;

    @Resource
    private UserService userService;

    //获取指定文章的所有评论,单表查询,没有联表查询
    @AccessLimit(maxCount = 500,seconds = 5) //接口访问限流
    @PostMapping("/list1")
    public AjaxResult getComVOByArticle(Integer articleid){
        if(articleid==null){
            return AjaxResult.fail(-1,"参数非法!");
        }

        //获取文章里的所有评论用户id
        HashSet<Integer> alluser = new HashSet<>();//具有去重特性
        List<CommentinfoVO> comments = commentService.getCommentVOByArticle(articleid);//所有评论与回复
        List<CommentinfoVO> commentOnly = commentService.getCommentOne(articleid);//一级评论
        if(comments.size() == 0 || commentOnly.size()==0){
            //说明没有评论
            return AjaxResult.success(null);
        }
        List<CommentinfoVO> replyOnly = commentService.getCommentTwo(articleid);//二级评论,回复
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

        //将用户名称,头像设置进评论,回复
        for(CommentinfoVO commentVO : commentOnly){
            int value = commentVO.getUid(); //获取用户名
            Userinfo userData = alluserId.get(value);
            commentVO.setUsername(userData.getUsername());
            commentVO.setUserphoto(userData.getPhoto());
        }
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

        return AjaxResult.success(commentOnly);
        //return AjaxResult.success(commentService.getComVOByArticle(articleid));
    }

    //获取指定文章的所有评论,联表查询
    @AccessLimit(maxCount = 500,seconds = 5) //接口访问限流
    @PostMapping("/list")
    public AjaxResult getComVOByArticleid(Integer articleid){
        if(articleid==null || articleid<=0){
            return AjaxResult.fail(-1,"参数非法!");
        }

        //获取文章里的所有评论
        List<CommentinfoVO> commentOne = commentService.getFirstCommentByArticle(articleid);//一级评论
        List<CommentinfoVO> commentTwo = commentService.getSecondaryCommentByArticle(articleid);//二级评论

        if(commentOne.size()==0){
            return AjaxResult.success(null);//说明没有评论
        }

        //整合评论
        for(CommentinfoVO comment : commentOne){
            int value = comment.getId();//一级评论commentId
            for(CommentinfoVO reply : commentTwo){
                int parentId = reply.getParentid(); //找到父commentId
                if(value == parentId){
                    comment.setReplies(reply);
                }
            }
        }

        return AjaxResult.success(commentOne);
        //return AjaxResult.success(commentService.getComVOByArticle(articleid));
    }

    //新增评论,返回自增主键id
    @AccessLimit(maxCount = 500,seconds = 5) //接口访问限流
    @DedupLimit(expireTime = 5000) //去除重复请求,5秒之内
    @PostMapping("add")
    public AjaxResult add(HttpServletRequest request, Commentinfo commentinfo){
        Userinfo userinfo = UserSessionUtils.getUser(request); //新增的评论,一定是当前登陆者发的
        if(commentinfo==null || commentinfo.getArticleid()==null || userinfo==null || userinfo.getId()==null){
            return AjaxResult.fail(-1,"参数非法!");
        }
        //判断内容,文本为空,不做任何操作
        String content = commentinfo.getContent();
        //判断当前内容是否全是空格与换行符
        if(content != null){
            content = content.replaceAll("\\s*|t|r|n","");
        }
        if("".equals(content) || content==null){
            return AjaxResult.fail(-1,"文本为空!");
        }
        //判断文章状态,不是1说明不是已发布的文章
        Articleinfo articleinfo = articleService.getDetail(commentinfo.getArticleid());
        if(articleinfo.getState()!=1){
            return AjaxResult.fail(-1,"文章状态异常!");
        }
        //一级评论,只传内容,博客id
        //二级评论,传内容,博客id,回复id,父id
        //设置uid进去,时间设置进去
        if(commentinfo.getParentid()==null){
            commentinfo.setParentid(-1);//一级评论
        }else{
            //有父id,没有回复人id,或者回复id和当前登陆者id一样
            if(commentinfo.getReplyid()==null || commentinfo.getReplyid().equals(userinfo.getId())) {
                return AjaxResult.fail(-1, "参数非法!");
            }
        }
        commentinfo.setCreatetime(LocalDateTime.now());//时间
        commentinfo.setUid(userinfo.getId());//设置uid进去
        commentService.add(commentinfo);//返回自增主键id
        //commentinfo.setId(id);
        return AjaxResult.success(commentinfo);
    }

    //删除评论,自己的,Integer id,Integer uid
    @AccessLimit(maxCount = 500,seconds = 5) //接口访问限流
    @DedupLimit(expireTime = 5000) //去除重复请求,5秒之内
    @PostMapping("/delete")
    public AjaxResult deleteOfMineById(HttpServletRequest request, Integer id){
        //只传入,评论自增主键id
        Userinfo userinfo = UserSessionUtils.getUser(request);
        if(id==null || userinfo==null ||userinfo.getId()==null){
            return AjaxResult.fail(-1,"参数非法!");
        }
        //删除评论,一级评论或是二级评论
        int result = commentService.deleteOfMineById(id, userinfo.getId()); //不为0,则删除评论成功
        if(result!=0){
            //删除二级评论,如果有的话
            commentService.deleteSecondaryComment(id);//传入的是parentid
        }
        return AjaxResult.success(result);
    }

    //获取指定用户id的发表的所有评论,没有联表查询
    @AccessLimit(maxCount = 500,seconds = 5) //接口访问限流
    @PostMapping("mycomment1")
    public AjaxResult getCommentOfMine(HttpServletRequest request){
        Userinfo userinfo = UserSessionUtils.getUser(request);
        if(userinfo==null || userinfo.getId()==null){
            return AjaxResult.fail(-1,"参数非法!");
        }

        //获取里面的所有文章id,
        HashSet<Integer> allarticle = new HashSet<>();//具有去重特性
        List<CommentinfoVO> comments = commentService.getCommentOfMine(userinfo.getId()); // 获取登陆者发表的所有评论
        if(comments.size()==0){
            //说明没有评论
            return AjaxResult.success(null);
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
        return AjaxResult.success(comments);
        //return AjaxResult.success(commentService.getCommentOfMine(userinfo.getId()));
    }

    //获取指定用户id的发表的所有评论,联表查询
    @AccessLimit(maxCount = 500,seconds = 5) //接口访问限流
    @PostMapping("mycomment")
    public AjaxResult getCommentsByUid(HttpServletRequest request){
        Userinfo userinfo = UserSessionUtils.getUser(request);
        if(userinfo==null || userinfo.getId()==null){
            return AjaxResult.fail(-1,"参数非法!");
        }

        //获取评论
        List<CommentinfoVO> comments = commentService.getCommentsByUid(userinfo.getId()); // 获取登陆者发表的所有评论
        if(comments.size()==0){
            //说明没有评论
            return AjaxResult.success(null);
        }
        //截取文章标题显示的长度
        for(CommentinfoVO comment : comments){
            if(comment.getArticlename().length() >= 23){
                String title = comment.getArticlename();
                title = title.substring(0,23) + "...";
                comment.setArticlename(title);
            }
        }

        return AjaxResult.success(comments);
        //return AjaxResult.success(commentService.getCommentOfMine(userinfo.getId()));
    }

}
