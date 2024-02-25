package com.demo.seleniumTest;

import com.demo.common.InitAndEnd;
import com.demo.common.WebVariable;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-02-23
 * Time: 18:32
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginTest extends InitAndEnd {
    @BeforeEach
    void init(){
        //到达登录页面
        webDriver.get(WebVariable.LOGIN);
        WebDriverWait waitCaptcha = new WebDriverWait(webDriver,20);
        waitCaptcha.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#captcha"))); //等待验证码可以被点击
    }

    @Order(1)
    @ParameterizedTest
    @CsvFileSource(resources = "loginFail.csv")
    //登录失败
    void loginFail(String loginname ,String password,String captchatext,String msg){

        webDriver.findElement(By.cssSelector("#loginname")).clear();
        webDriver.findElement(By.cssSelector("#password")).clear();
        webDriver.findElement(By.cssSelector("#captchatext")).clear(); //验证码
        //输入账号密码,并点击提交按钮
        webDriver.findElement(By.cssSelector("#loginname")).sendKeys(loginname);
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


    @Order(2)
    //登陆成功
    @ParameterizedTest
    @CsvSource(value = {"tenjutest,123456789,12345"})
    void loginSuccess(String username,String password,String captchatext) throws InterruptedException, IOException {
        //输入账号密码,并点击提交按钮
        webDriver.findElement(By.cssSelector("#loginname")).sendKeys(username);
        webDriver.findElement(By.cssSelector("#password")).sendKeys(password);
        webDriver.findElement(By.cssSelector("#captchatext")).sendKeys(captchatext); //验证码
        webDriver.findElement(By.cssSelector("#submit")).click();

        //显式等待,等待注销按钮的出现
        WebDriverWait wait = new WebDriverWait(webDriver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#toolbar > a:nth-child(3) > span > span.l-btn-text")));

        //判断url
        String cur_url = webDriver.getCurrentUrl();
        Assertions.assertEquals(WebVariable.LIST,cur_url);//断言,判断是否跳转到博客列表页
    }

}
