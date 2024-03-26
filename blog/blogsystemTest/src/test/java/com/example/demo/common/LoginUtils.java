package com.example.demo.common;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-02-19
 * Time: 0:48
 */
public class LoginUtils extends InitAndEnd{
    public static void login(String username ,String password,String captchatext,String msg){
        WebDriverWait waitCaptcha = new WebDriverWait(webDriver,20);
        waitCaptcha.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#captcha"))); //等待验证码可以被点击
        webDriver.findElement(By.cssSelector("#username")).clear();
        webDriver.findElement(By.cssSelector("#password")).clear();
        webDriver.findElement(By.cssSelector("#captchatext")).clear(); //验证码
        //输入账号密码,并点击提交按钮
        webDriver.findElement(By.cssSelector("#username")).sendKeys(username);
        webDriver.findElement(By.cssSelector("#password")).sendKeys(password);
        webDriver.findElement(By.cssSelector("#captchatext")).sendKeys(captchatext); //验证码
        webDriver.findElement(By.cssSelector("#submit")).click();
        //显式等待,等待弹窗的出现
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        wait.until(ExpectedConditions.alertIsPresent());
        //判断弹窗,提示框文本
        String text = webDriver.switchTo().alert().getText();
        Assertions.assertEquals(msg,text);
        //弹窗确认
        webDriver.switchTo().alert().accept();
    }
}
