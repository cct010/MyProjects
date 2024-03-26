package com.example.demo.seleniumTest;

import com.example.demo.common.InitAndEnd;
import com.example.demo.common.LoginUtils;
import com.example.demo.common.MenuBar;
import com.example.demo.common.WebVariable;
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
 * Date: 2024-01-02
 * Time: 23:59
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)//人工控制执行顺序
public class LoginTest extends InitAndEnd {

    @Test
    @BeforeEach
    void init(){
        //打开博客登录页
        webDriver.get(WebVariable.BLOG_LOGIN);
    }

    //登陆失败
    @Order(1)
    @ParameterizedTest
    @CsvFileSource(resources = "loginFail.csv")
    void loginFail(String username ,String password,String captchatext,String msg){
        LoginUtils.login(username,password,captchatext,msg);
    }

    //登陆成功
    @Order(3)
    @ParameterizedTest
    @CsvSource(value = {"tenjutest,123456789,12345"})
    void loginSuccess(String username,String password,String captchatext) throws InterruptedException, IOException {
        //输入账号密码,并点击提交按钮
        webDriver.findElement(By.cssSelector("#username")).sendKeys(username);
        webDriver.findElement(By.cssSelector("#password")).sendKeys(password);
        webDriver.findElement(By.cssSelector("#captchatext")).sendKeys(captchatext); //验证码
        webDriver.findElement(By.cssSelector("#submit")).click();

        //显示等待,等待用户名的出现
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#username")));
        WebDriverWait waitLink = new WebDriverWait(webDriver,10);
        waitLink.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#artList > div:nth-child(1) > a")));
        //验证用户名字
        String curName = webDriver.findElement(By.cssSelector("#username")).getText();
        Assertions.assertEquals(username,curName); // 校验username
        //判断url
        String cur_url = webDriver.getCurrentUrl();
        Assertions.assertEquals(WebVariable.BLOG_LIST,cur_url);//断言,判断是否跳转到博客列表页
    }

    //菜单栏显示
    @Order(2)
    @ParameterizedTest
    @CsvFileSource(resources = "loginMenuBar.csv")
    void menuBar(String element ,String url) {
        MenuBar.menuBarLogin(element,url);
    }
}
