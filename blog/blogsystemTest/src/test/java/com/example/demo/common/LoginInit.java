package com.example.demo.common;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-01-03
 * Time: 20:50
 */
public class LoginInit extends InitAndEnd{
    //登陆
    @BeforeAll
    static void init(){
        //登陆
        //输入账号密码,并点击提交按钮
        webDriver.get(WebVariable.BLOG_LOGIN);
        webDriver.findElement(By.cssSelector("#username")).sendKeys("tenjutest");
        webDriver.findElement(By.cssSelector("#password")).sendKeys("123456789");
        webDriver.findElement(By.cssSelector("#captchatext")).sendKeys("12345"); //验证码
        webDriver.findElement(By.cssSelector("#submit")).click();
        //跳转到了列表页
        //等到按钮可以被点击
        WebDriverWait waitLink = new WebDriverWait(webDriver,10);
        waitLink.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#artList > div:nth-child(1) > a")));
        System.out.println(">>>>>>>");
    }




    //判断注销
    @AfterAll
    static void logout() throws InterruptedException {
        webDriver.switchTo().defaultContent();//如果有进入子页面,就跳出子页面
        System.out.println("<<<<<<<<<<<<<<<<<");
        //点击,注销按钮
        //webDriver.findElement(By.cssSelector("body > div.daohang > a:nth-child(6)")).click();
        //Thread.sleep(3000);
        webDriver.findElement(By.linkText("注销")).click();
        //Thread.sleep(3000);
        //隐式等待,等待弹窗的出现
        WebDriverWait wait = new WebDriverWait(webDriver, 8000);
        wait.until(ExpectedConditions.alertIsPresent());
        //点击弹窗,确认注销
        webDriver.switchTo().alert().accept();
        //隐式等待,等待验证码按钮可以被点击
        WebDriverWait waitLogout = new WebDriverWait(webDriver, 8);
        waitLogout.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#captchatext")));
        String cur_url = webDriver.getCurrentUrl();
        Assertions.assertEquals(WebVariable.BLOG_LOGIN,cur_url);
    }

}
