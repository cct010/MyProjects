package com.example.demo.common;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-02-24
 * Time: 0:11
 */
public class ArticleUtils {
    //判断是否都是空格和换行符
    public static boolean isEmpty (String text){
        if(text != null){
            text = text.replaceAll("\\s*|t|r|n","");
        }
        //文本为空,不做任何操作
        if("".equals(text) || text==null ){
            return true;//文本为空,返回true
        }
        return false;
    }
}
