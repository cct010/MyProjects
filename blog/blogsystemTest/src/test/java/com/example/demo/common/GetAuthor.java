package com.example.demo.common;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-02-18
 * Time: 20:56
 */
public class GetAuthor extends InitAndEnd{
    //获取作者/用户信息
    public static void getAuthor(String username,String element){
        WebDriverWait waitUser = new WebDriverWait(webDriver,10);
        waitUser.until(ExpectedConditions.not(ExpectedConditions.textToBe(By.cssSelector(element),"-1")));
        //隐式等待,等待用户名的出现
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#username")));
        //wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#artList > div:nth-child(1) > a")));
        //获取用户名
        String name = webDriver.findElement(By.cssSelector("#username")).getText();
        Assertions.assertEquals(username,name);

        //获取用户头像
        //String img = webDriver.findElement(By.cssSelector("#headimg")).getAttribute("src");
        //img = img.substring(img.lastIndexOf(".")+1);
        //Assertions.assertEquals("http://62.234.10.69:8080/image/00.png",img);
        //Assertions.assertEquals("jpeg",img);
        String width = webDriver.findElement(By.cssSelector("#headimg")).getAttribute("width");
        Assertions.assertEquals("140",width);

        //获取用户文章数
        String number = webDriver.findElement(By.cssSelector(element)).getText();
        Assertions.assertNotEquals("-1",number);
    }
}
