package com.example.demo.common;

import com.example.demo.entity.Articleinfo;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2023-12-13
 * Time: 0:25
 */
public class ArticleUtils {

    //截取文章的正文和标题
    public static void cutContent (List<Articleinfo> articleinfoList){
        for(Articleinfo articleinfo : articleinfoList){
            if(articleinfo.getContent().length() >= 100){
                String content = articleinfo.getContent();
                content = content.substring(0,100) + "...";
                content = content.replaceAll("#","");
                articleinfo.setContent(content);
            }
            if(articleinfo.getTitle().length() >= 50){
                String title = articleinfo.getTitle();
                title = title.substring(0,50) + "...";
                articleinfo.setTitle(title);
            }
        }
    }

    //判断文章的正文和标题.是否都是空格和换行符
    public static boolean isEmpty (Articleinfo articleinfo){
        String content = articleinfo.getContent(); //文章正文
        String title = articleinfo.getTitle(); //文章标题
        if(content != null){
            content = content.replaceAll("\\s*|t|r|n","");
        }
        if(title != null){
            title = title.replaceAll("\\s*|t|r|n","");
        }
        //文本为空,不做任何操作
        if("".equals(content) || content==null || "".equals(title) || title==null){
            return true;//文本为空,返回true
        }
        return false;
    }
}
