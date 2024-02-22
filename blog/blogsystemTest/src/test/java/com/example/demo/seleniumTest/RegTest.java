package com.example.demo.seleniumTest;

import com.example.demo.common.InitAndEnd;
import com.example.demo.common.MenuBar;
import com.example.demo.common.WebVariable;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.beans.Transient;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-01-02
 * Time: 20:37
 */
public class RegTest extends InitAndEnd{

    @Test
    @BeforeEach
    void init(){
        //打开博客注册页
        webDriver.get(WebVariable.BLOG_REG);
    }

    //注册成功
    @Disabled
    @Order(1)
    @ParameterizedTest
    @CsvSource(value = {"tenjutest,123456789,12345,12345,12345"})
    void regSuccess(String username ,String password,String captcha) {

        //输入账号密码,并点击提交按钮
        webDriver.findElement(By.cssSelector("#username")).sendKeys(username);
        webDriver.findElement(By.cssSelector("#password")).sendKeys(password);
        webDriver.findElement(By.cssSelector("#confirmPassword")).sendKeys(password);
        webDriver.findElement(By.cssSelector("#captchatext")).sendKeys(captcha);
        webDriver.findElement(By.cssSelector("#submit")).click();
        //隐式等待,等待弹窗的出现
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        wait.until(ExpectedConditions.alertIsPresent());
        //判断弹窗,提示框文本
        String text = webDriver.switchTo().alert().getText();
        Assertions.assertEquals("注册成功!",text);
        //弹窗确认
        webDriver.switchTo().alert().accept();
        //跳转到博客个人中心页页,获取当前页面的url,如果url是/blog/blog-center.html,测试通过,否则不通过
        String cur_url = webDriver.getCurrentUrl();
        Assertions.assertEquals(WebVariable.BLOG_CENTER,cur_url);//断言
    }


    //注册失败
    @Order(1)
    @ParameterizedTest
    @CsvFileSource(resources = "regFail.csv")
    void regFail(String username ,String password,String confirmPassword,String captcha,String msg) throws IOException, InterruptedException {
        //输入账号密码,并点击提交按钮
        webDriver.findElement(By.cssSelector("#username")).sendKeys(username);
        webDriver.findElement(By.cssSelector("#password")).sendKeys(password);
        webDriver.findElement(By.cssSelector("#confirmPassword")).sendKeys(confirmPassword);
        webDriver.findElement(By.cssSelector("#captchatext")).sendKeys(captcha);
        webDriver.findElement(By.cssSelector("#submit")).click();
        //隐式等待,等待弹窗的出现
        WebDriverWait wait = new WebDriverWait(webDriver, 30);
        wait.until(ExpectedConditions.alertIsPresent());
        //判断弹窗,提示框文本
        String text = webDriver.switchTo().alert().getText();
        Assertions.assertEquals(msg,text);
        //弹窗确认
        webDriver.switchTo().alert().accept();

    }


    //菜单栏显示
    @Order(2)
    @ParameterizedTest
    @CsvSource(value = {"主页","写博客","登陆"})
    void menuBar(String element ) {
        MenuBar.menuBarUnlogin(element);
    }

}
