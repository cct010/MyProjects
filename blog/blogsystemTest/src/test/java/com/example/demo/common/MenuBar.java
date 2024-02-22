package com.example.demo.common;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-02-18
 * Time: 19:01
 */
public class MenuBar extends InitAndEnd{

    //未登录下的菜单栏
    public static void menuBarUnlogin(String element ) {
        webDriver.findElement(By.linkText(element)).click(); //点击
        String cur_url = webDriver.getCurrentUrl();
        Assertions.assertEquals(WebVariable.BLOG_LOGIN,cur_url);
    }

    //登录下的菜单栏
    public static void menuBarLogin(String element,String url) {
        webDriver.findElement(By.linkText(element)).click(); //点击
        String cur_url = webDriver.getCurrentUrl();
        Assertions.assertEquals(url,cur_url);
    }
}
