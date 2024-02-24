package com.example.demo.controller;

import com.example.demo.common.AjaxResult;
import com.example.demo.common.ArticleUtils;
import com.example.demo.common.UserSessionUtils;
import com.example.demo.config.AccessLimit;
import com.example.demo.config.DedupLimit;
import com.example.demo.entity.Articleinfo;
import com.example.demo.entity.Userinfo;
import com.example.demo.service.ArticleService;
import com.example.demo.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2023-12-08
 * Time: 17:45
 */
@RestController
@RequestMapping("/art")
@Slf4j
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @Autowired
    private CommentService commentService;

    @AccessLimit(maxCount = 500,seconds = 5) //接口访问限流
    @PostMapping("mylist")
    public AjaxResult getMyList(HttpServletRequest request){
        Userinfo userinfo = UserSessionUtils.getUser(request);
        if(userinfo == null){
            return AjaxResult.fail(-1,"请求非法!");
        }
        List<Articleinfo> list = articleService.getMyList(userinfo.getId());
        ArticleUtils.cutContent(list); //因为是列表,文章需要截取
        return AjaxResult.success(list);
    }

    //删除一篇文章,只需要一个id,Integer id
    @AccessLimit(maxCount = 500,seconds = 5) //接口访问限流
    @DedupLimit(expireTime = 5000) //去除重复请求,5秒之内
    @PostMapping("/delete")
    public AjaxResult delete(HttpServletRequest request,Articleinfo articleinfo ){
        if(articleinfo.getId() == null || articleinfo.getId()<=0){
            return AjaxResult.fail(-1,"参数异常!");
        }
        Userinfo userinfo = UserSessionUtils.getUser(request);
        if(userinfo == null){
            return AjaxResult.fail(-1,"用户未登陆!");
        }
        articleinfo.setUid(userinfo.getId());//里面有id,此时再设置用户id进去
        //文章可能有评论,有外键依赖可能删不成功
        //先判断是否是当前登录者的文章
        int userid = articleService.getDetail(articleinfo.getId()).getUid();
        if(userid == userinfo.getId()){
            //是当前登陆者的文章,先删评论,评论有外键约束
           commentService.deleteOfMineByArticle(articleinfo.getId());
        }
        return AjaxResult.success(articleService.delete(articleinfo));
    }

    //查看一篇文章
    @AccessLimit(maxCount = 500,seconds = 5) //接口访问限流
    @PostMapping("/detail")
    public AjaxResult getDetail(Integer id){
        if(id == null || id<=0){
            return AjaxResult.fail(-1,"参数非法!");
        }
        return AjaxResult.success(articleService.getDetail(id));
    }

    //阅读量+1
    @AccessLimit(maxCount = 500,seconds = 5) //接口访问限流
    @PostMapping("/incrcount")
    public AjaxResult incrRCount(Integer id){
        if(id != null && id>0){
            return AjaxResult.success(articleService.incrRCount(id));
        }
        return AjaxResult.fail(-1,"未知错误!");
    }

    //新增一篇文章
    @AccessLimit(maxCount = 500,seconds = 5) //接口访问限流
    @DedupLimit(expireTime = 5000) //去除重复请求,5秒之内
    @PostMapping("/add")
    public AjaxResult add(HttpServletRequest request, Articleinfo articleinfo){
        //非空判断,
        if(articleinfo==null || !StringUtils.hasLength(articleinfo.getTitle()) ||
        !StringUtils.hasLength(articleinfo.getContent())){
            return AjaxResult.fail(-1,"参数非法!");
        }
        //判断文章里是不是都是空格跟换行符
        if(ArticleUtils.isEmpty(articleinfo)){
            return AjaxResult.fail(-1,"文本为空!"); //文本为空,不做任何操作
        }
        //判断文章标题长度
        if(articleinfo.getTitle().length()>100){
            return AjaxResult.fail(-1,"标题超过100字符!请减少标题字数!");
        }
//        String content = articleinfo.getContent();
//        if(content != null){
//            content = content.replaceAll("\\s*|t|r|n","");
//        }
//
//        if("".equals(content) || content==null){
//            return AjaxResult.fail(-1,"文本为空!");
//        }
        //数据库添加
        //得到当前用户
        Userinfo userinfo = UserSessionUtils.getUser(request);
        if(userinfo==null || userinfo.getId()<=0){
            return AjaxResult.fail(-1,"用户无效!");
        }
        articleinfo.setUid(userinfo.getId());
        //判断是新写的文章,发布,还是草稿里的文章发布
        if(articleinfo.getId()==null){
            //新写的文章,没有id
            return AjaxResult.success(articleService.add(articleinfo));
        }else{
            //草稿变成的文章,有id
            //还需要修改文章的状态,添加更新时间
            articleinfo.setUpdatetime(LocalDateTime.now());
            articleinfo.setState(1);
            articleService.draftupdate(articleinfo);//修改文章
            return AjaxResult.success( articleService.draftupdate(articleinfo));
        }
    }

    //修改一篇文章
    @AccessLimit(maxCount = 500,seconds = 5) //接口访问限流
    @DedupLimit(expireTime = 5000) //去除重复请求,5秒之内
    @PostMapping("/update")
    public AjaxResult update(HttpServletRequest request,Articleinfo articleinfo){
        if(articleinfo==null || !StringUtils.hasLength(articleinfo.getTitle()) ||
        !StringUtils.hasLength(articleinfo.getContent()) || articleinfo.getId()==null){
            return AjaxResult.fail(-1,"参数非法!");
        }
        //判断文章里是不是都是空格跟换行符
        if(ArticleUtils.isEmpty(articleinfo)){
            return AjaxResult.fail(-1,"文本为空!"); //文本为空,不做任何操作
        }
        //判断文章标题长度
        if(articleinfo.getTitle().length()>100){
            return AjaxResult.fail(-1,"标题超过100字符!请减少标题字数!");
        }
//        String content = articleinfo.getContent();
//        if(content != null){
//            content = content.replaceAll("\\s*|t|r|n","");
//        }
//        //文本为空,不做任何操作
//        if("".equals(content) || content==null){
//            return AjaxResult.fail(-1,"文本为空!");
//        }
        //得到当前用户
        Userinfo userinfo = UserSessionUtils.getUser(request);
        if(userinfo == null || userinfo.getId() == null){
            return AjaxResult.fail(-1,"用户无效!");
        }
        articleinfo.setUid(userinfo.getId());//修改的是当前的是当前登陆者的文章
        articleinfo.setUpdatetime(LocalDateTime.now());//修改文章的修改时间
        return AjaxResult.success(articleService.update(articleinfo));

    }

    //查询所有文章
    @AccessLimit(maxCount = 500,seconds = 5) //接口访问限流
    @PostMapping("/listbypage")
    public AjaxResult getListByPage(Integer pindex,Integer psize){
        if(pindex==null || pindex<=1){
            pindex=1;
        }
        if(psize==null || psize<=1){
            psize=6;
        }
        //(当前页码-1)*每页显示条数
        int offset = (pindex-1)*psize;
        //文章所有数据
        List<Articleinfo> list = articleService.getListByPage(psize,offset);
        ArticleUtils.cutContent(list); //因为是列表,文章需要截取
        //计算当前列表总共有多少页
        //总条数/每页显示条数,向上取整
        int totalcount = articleService.getCount();
        double pcountdb = totalcount / (psize*1.0);
        int pcount =  (int) Math.ceil(pcountdb);

        HashMap<String ,Object> result = new HashMap<>();
        result.put("list",list);
        result.put("pcount",pcount);

        return AjaxResult.success(result);
    }

    //草稿保存
    @AccessLimit(maxCount = 500,seconds = 5) //接口访问限流
    @DedupLimit(expireTime = 5000) //去除重复请求,5秒之内
    @PostMapping("/draftadd")
    public AjaxResult draftAdd (HttpServletRequest request, Articleinfo articleinfo){
        //非空判断,
        if(articleinfo==null || !StringUtils.hasLength(articleinfo.getTitle()) ||
                !StringUtils.hasLength(articleinfo.getContent())){
            return AjaxResult.fail(-1,"参数非法!");
        }

        //判断标题,文章里是不是都是空格跟换行符
        if(ArticleUtils.isEmpty(articleinfo)){
            return AjaxResult.fail(-1,"文本为空!"); //文本为空,不做任何操作
        }
        //判断文章标题长度
        if(articleinfo.getTitle().length()>100){
            return AjaxResult.fail(-1,"标题超过100字符!请减少标题字数!");
        }

//        String content = articleinfo.getContent();
//        if(content != null){
//            content = content.replaceAll("\\s*|t|r|n","");
//        }
//        //文本为空,不做任何操作
//        if("".equals(content) || content==null){
//            return AjaxResult.fail(-1,"文本为空!");
//        }
        //得到当前用户
        Userinfo userinfo = UserSessionUtils.getUser(request);
        if(userinfo==null || userinfo.getId()<=0){
            return AjaxResult.fail(-1,"用户无效!");
        }
        articleinfo.setUid(userinfo.getId());
        //判断草稿是否是第一次创建
        //id==null,说明是新创建的,没有id
        if(articleinfo.getId()==null){
            articleinfo.setState(-1);//说明是草稿
            articleService.draftAdd(articleinfo);//新建文章,state状态为-1,返回自增主键id
        }else{
            //说明草稿箱文章,已经创建过,
            //修改文章即可,title,content,updatetime,uid,id
            articleinfo.setUpdatetime(LocalDateTime.now());
            articleService.update(articleinfo);//修改文章
        }
        return AjaxResult.success(articleinfo.getId());//返回自增主键
    }

    //获取草稿列表
    @AccessLimit(maxCount = 500,seconds = 5) //接口访问限流
    @PostMapping("mydraftlist")
    public AjaxResult getMyDraftList(HttpServletRequest request){
        Userinfo userinfo = UserSessionUtils.getUser(request);
        if(userinfo == null){
            return AjaxResult.fail(-1,"请求非法!");
        }
        List<Articleinfo> list = articleService.getMyDraftList(userinfo.getId());
        ArticleUtils.cutContent(list); //因为是列表,文章需要截取
        return AjaxResult.success(list);
    }

}
