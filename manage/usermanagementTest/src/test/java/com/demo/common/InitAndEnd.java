package com.demo.common;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-02-23
 * Time: 18:29
 */
public class InitAndEnd {
    public static WebDriver webDriver;

    @BeforeAll
    static void SetuUp(){
        webDriver = new ChromeDriver();
    }

    @AfterAll
    static void TearDown(){
        webDriver.close();//关闭当前网页
    }
}
