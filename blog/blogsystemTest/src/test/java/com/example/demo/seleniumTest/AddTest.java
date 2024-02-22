package com.example.demo.seleniumTest;

import com.example.demo.common.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-01-03
 * Time: 21:01
 */
//编辑页,登录状态
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)//人工控制执行顺序
public class AddTest extends LoginInit {

    @BeforeEach
    void initget(){
        webDriver.get(WebVariable.BLOG_ADD);
    }

    //判断用户信息,列表页也有
    @Test
    @Order(1)
    void getAuthor() throws InterruptedException {
        String username = "tenjutest";
        String element = "#artTotal";
        GetAuthor.getAuthor(username,element);
    }

    //保存新博客,异常填写
    @ParameterizedTest
    @Order(2)
    @CsvFileSource(resources = "saveArt.csv")
    void saveArtFail(String title,String content,String msg) throws InterruptedException {
        String element = "body > div.container > div.container-right > div.title > button.mysave";
        Article.artFail(title, content, msg, element);
    }

    //发布新博客,异常发布
    @ParameterizedTest
    @Order(3)
    @CsvFileSource(resources = "addArt.csv")
    void addArtFail(String title,String content,String msg) throws InterruptedException {
        String element = "#submit";
        Article.artFail(title, content, msg, element);
    }

    //保存文章,正常操作,
    @ParameterizedTest
    @Order(4)
    @CsvSource(value = "今天天气很好呀-测试我的保存草稿,重游就地,保存草稿成功!")
    void saveArtSuccess(String title,String content,String msg) throws InterruptedException {
        //保存按钮可以被点击
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("body > div.container > div.container-right > div.title > button.mysave")));//等待保存按钮可以被点击

        //填写标题
        webDriver.findElement(By.cssSelector("#title")).sendKeys(title);
        //填写正文
        webDriver.findElement(By.cssSelector("#editor > div.editormd-toolbar > div > ul > li:nth-child(21) > a > i")).click();

        //点击保存按钮
        webDriver.findElement(By.cssSelector("body > div.container > div.container-right > div.title > button.mysave")).click();//点击保存

        //获取文章数量
        String oldnumber = webDriver.findElement(By.cssSelector("#artTotal")).getText();

        //隐式等待,等待弹窗的出现
        WebDriverWait waitAlert = new WebDriverWait(webDriver, 10);
        waitAlert.until(ExpectedConditions.alertIsPresent());
        String text = webDriver.switchTo().alert().getText(); //判断弹窗提示信息
        Assertions.assertEquals(msg,text);
        webDriver.switchTo().alert().accept();//点击

        //判断保存
        webDriver.get(WebVariable.BLOG_CENTER);//到个人中心页
        WebDriverWait waitDraft = new WebDriverWait(webDriver, 10);
        waitDraft.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#ifDiv"))); //等待frame可以被点击

        //判断文章数量
        String newnumber = webDriver.findElement(By.cssSelector("#artTotal")).getText();
        Assertions.assertEquals(newnumber,oldnumber); //保存文章草稿,文章数量不会变

        webDriver.switchTo().frame(webDriver.findElement(By.cssSelector("#ifDiv")));//切换到子页面
        webDriver.findElement(By.cssSelector("#mydraftlist")).click();//点击我的草稿
        webDriver.switchTo().defaultContent();//跳出frame,默认是最外面默认页面
        webDriver.switchTo().frame(webDriver.findElement(By.cssSelector("#ifDiv")));//切换到子页面

        //等待,标题可以被点击
        WebDriverWait waitTitle = new WebDriverWait(webDriver, 10);
        waitTitle.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#artList > div:nth-child(1) > a")));

        //判断第一篇,标题
        String cur_title = webDriver.findElement(By.cssSelector("#artList > div:nth-child(1) > a")).getText(); //查找第一篇文章
        Assertions.assertEquals(title,cur_title);

    }

    //发布文章,正常操作,
    @ParameterizedTest
    @Order(5)
    @CsvSource(value = "今天天气很好呀-测试我的文章发布,重游就地,发布文章成功! 是否继续添加新文章?")
    void addArtSuccess(String title,String content,String msg) throws InterruptedException {
        Article.artSuccess(title,content,msg);
    }

    //判断菜单栏
    @Order(6)
    @ParameterizedTest
    @CsvFileSource(resources = "listMenuBar.csv")
    void getMenu(String element,String url) throws InterruptedException {
        MenuBar.menuBarLogin(element,url);
    }

}
