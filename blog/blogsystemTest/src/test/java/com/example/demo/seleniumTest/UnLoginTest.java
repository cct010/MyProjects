package com.example.demo.seleniumTest;

import com.example.demo.common.InitAndEnd;
import com.example.demo.common.WebVariable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.platform.suite.api.Suite;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-01-08
 * Time: 23:31
 */
//未登录的测试
public class UnLoginTest extends InitAndEnd {
    @ParameterizedTest
    @Order(1)
    @CsvFileSource(resources = "unlogintest.csv")
    //Not Logged In
    void unLoggedTest(String url){
        webDriver.get(url);//跳转页面

        WebDriverWait wait = new WebDriverWait(webDriver,10);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#submit")));

        String cur_url = webDriver.getCurrentUrl();
        Assertions.assertEquals(WebVariable.BLOG_LOGIN,cur_url);
    }
}
