package com.example.demo.seleniumTest;

import com.example.demo.common.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-01-03
 * Time: 0:56
 */
//登陆状态,测试列表页
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)//人工控制执行顺序
public class ListTest extends LoginInit {



    @BeforeEach
    void initget(){
        webDriver.get(WebVariable.BLOG_LIST);
    }

    //判断列表是否显示
    @Test
    @Order(1)
    void getList() {
        //隐式等待,等待标题的出现
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        //wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#username")));
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#artList > div:nth-child(1) > a")));
        //获取第一篇标题
        String title = webDriver.findElement(By.cssSelector("#artList > div:nth-child(1) > div.title")).getText();
        //Assertions.assertEquals("钱塘江上潮信来，今日方知我是我。",title);
        Assertions.assertNotNull(title);
        //获取时间
        String time = webDriver.findElement(By.cssSelector("#artList > div:nth-child(1) > div.date")).getText();
        //Assertions.assertEquals("2023-12-30 21:49:30",time);
        Assertions.assertNotNull(time);
        //获取链接
        String link = webDriver.findElement(By.cssSelector("#artList > div:nth-child(1) > a")).getText();
        String linkSrc = webDriver.findElement(By.cssSelector("#artList > div:nth-child(1) > a")).getAttribute("href");
        Assertions.assertEquals("查看全文 >>",link);
        Assertions.assertNotNull(linkSrc);

        //文章数量
        int number = webDriver.findElements(By.cssSelector(".title")).size();
        Assertions.assertNotEquals(0,number); // 校验博客数量,博客数量>0则测试通过
    }

    //判断用户信息是否显示
    @Test
    @Order(2)
    void getAuthor(){
        String username = "tenjutest";
        String element = "#artTotal";
        GetAuthor.getAuthor(username,element);
    }


    //判断查看全文链接是否可以跳转
    @Test
    @Order(3)
    void getLink(){
        //隐式等待,等待按钮可以被点击
        WebDriverWait waitLogout = new WebDriverWait(webDriver, 8);
        waitLogout.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#artList > div:nth-child(1) > a")));
        //点击查看全文
        String link = webDriver.findElement(By.cssSelector("#artList > div:nth-child(1) > a")).getAttribute("href");
        webDriver.findElement(By.cssSelector("#artList > div:nth-child(1) > a")).click();
        String cur_url = webDriver.getCurrentUrl();
        Assertions.assertEquals(link,cur_url);
        //浏览器回退,返回
        //webDriver.navigate().back();
    }

    //判断分页功能
    @ParameterizedTest
    @CsvFileSource(resources = "listPaging.csv")
    @Order(4)
    void paging(String url,String element,String msg){
        webDriver.get(url);
        //隐式等待,等待链接可以被点击
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#artList > div:nth-child(1) > a")));

        //获取页面所有文章列表
        List<WebElement> webElements = webDriver.findElements(By.cssSelector(".blog"));
        //获取第一篇文章
        String title = webElements.get(0).findElement(By.cssSelector(".title")).getText();
        Assertions.assertNotNull(title); //标题不为空


        //滚动条操作,滑到最底端
        ((JavascriptExecutor)webDriver).executeScript("document.querySelector(\"body > div.container > div.container-right\").scrollTop=1476");
        //翻页按钮,点击
        webDriver.findElement(By.cssSelector(element)).click();


        if(msg.equals("")){
            //没有弹窗信息,就判断url
            WebDriverWait waitPaging = new WebDriverWait(webDriver,10);
           // waitPaging.until(ExpectedConditions.urlContains("l?pindex=")); //url包含预期字符
            waitPaging.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#artList > div:nth-child(1) > a")));
            //获取第一篇文章
            List<WebElement> cur_webElements = webDriver.findElements(By.cssSelector(".blog"));
            //获取第一篇文章的标题
            String cur_title = cur_webElements.get(0).findElement(By.cssSelector(".title")).getText();
            Assertions.assertNotNull(title); //标题不为空
            Assertions.assertNotEquals(title,cur_title);//两次获取的标题不同
            Assertions.assertNotEquals(url,webDriver.getCurrentUrl()); //两次url不一致

        }else{
            //判断弹窗内容
            WebDriverWait waitAlert = new WebDriverWait(webDriver,10);
            waitAlert.until(ExpectedConditions.alertIsPresent());
            String alertmsg = webDriver.switchTo().alert().getText();
            Assertions.assertEquals(msg,alertmsg);
            webDriver.switchTo().alert().accept();//弹窗确认
        }


    }

    //判断菜单栏
    @Order(5)
    @ParameterizedTest
    @CsvFileSource(resources = "listMenuBar.csv")
    void getMenu(String element,String url){
        MenuBar.menuBarLogin(element,url);
    }

}
