package com.example.demo.common;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-01-02
 * Time: 20:28
 */
//初始化和退出操作
public class InitAndEnd {
    public static WebDriver webDriver;
    @BeforeAll
    static void SetUp(){
        webDriver = new ChromeDriver();
    }
    @AfterAll
    static void TearDown(){
        //webDriver.quit(); //关闭整个浏览器,
        webDriver.close(); //退出,关闭此页面
    }
}
