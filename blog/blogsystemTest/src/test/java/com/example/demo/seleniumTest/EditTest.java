package com.example.demo.seleniumTest;

import com.example.demo.common.*;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-01-04
 * Time: 1:05
 */
//编辑页,正常登陆
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)//测试顺序
public class EditTest extends LoginInit {
//    @BeforeEach
//    void initget(){
//        String blogUrl = WebVariable.BLOG_EDIT;
//        blogUrl = blogUrl + "?id=116";
//        webDriver.get(blogUrl);
//    }

    //跳转到编辑页,无id,会跳转到添加页
    @Test
    @Order(1)
    void getEdit() throws InterruptedException {
        //没有id,会跳转到blog-add.html
        webDriver.get(WebVariable.BLOG_EDIT); //才进行页面跳转
        //等到按钮可以被点击
        WebDriverWait wait = new WebDriverWait(webDriver,10);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("body > div.container > div.container-right > div.title > button.mysave")));
        String cur_url = webDriver.getCurrentUrl();
        Assertions.assertEquals(WebVariable.BLOG_ADD,cur_url);
    }

    //跳转到个人中心页,点击第一篇发布的文章
    @Test
    @Order(2)
    void getArtDetail() throws InterruptedException, IOException {
        //到个人中心页
        webDriver.get(WebVariable.BLOG_CENTER);

        WebDriverWait waitFrame = new WebDriverWait(webDriver, 10);
        waitFrame.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#ifDiv"))); //等待frame可以被点击
        //跳转子页面
        webDriver.switchTo().frame(webDriver.findElement(By.cssSelector("#ifDiv")));

        //等待,标题可以被点击
        WebDriverWait waitTitle = new WebDriverWait(webDriver, 10);
        waitTitle.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#artList > div:nth-child(1) > a")));

        //获取第一篇,标题
        String cur_title = webDriver.findElement(By.cssSelector("#artList > div:nth-child(1) > a")).getText(); //查找第一篇文章
        //点击第一篇文章,的编辑按钮
        webDriver.findElement(By.cssSelector("#artList > div > div.articleButton > a:nth-child(5)")).click();
        webDriver.switchTo().defaultContent();//跳出框架

        //等待,提交按钮,可以被点击
        WebDriverWait waitSubmit = new WebDriverWait(webDriver, 10);
        waitSubmit.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#title")));
        WebDriverWait waitContent = new WebDriverWait(webDriver,10);
        //等待第三方控件里的某个东西出现,不一定显示在页面上
        waitContent.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#editor .editormd-markdown-textarea")));

//        //截图
//        File file = ((TakesScreenshot)webDriver).getScreenshotAs(OutputType.FILE);
//        String name = UUID.randomUUID() +".png";
//        name = name.replace("-","");
//        File filename = new File("./src/main/resources/photo/getArtDetail"+name);
//        FileUtils.copyFile(file,filename);

        //获取标题
        String js = "return jQuery(\"#title\")[0].value";
        String cur_title_edit = (String) ((JavascriptExecutor)webDriver).executeScript(js);
        //String cur_title_edit = webDriver.findElement(By.cssSelector("#title")).getAttribute("value");
        Assertions.assertEquals(cur_title,cur_title_edit);
        //获取文章详情,jQuery("#editor .editormd-markdown-textarea")[0].innerText
        //String cur_content_edit = webDriver.findElement(By.cssSelector("#editor .editormd-markdown-textarea")).getAttribute("innerText");
        String editContent = "return editor.getValue()";
        String cur_content_edit = (String) ((JavascriptExecutor)webDriver).executeScript(editContent);
        Assertions.assertNotEquals("",cur_content_edit);//判断文章详情,不为空
    }

    //判断用户信息
    @Order(3)
    @Test
    void getAuthor(){
        String username = "tenjutest";
        String element = "#artTotal";
        GetAuthor.getAuthor(username,element);
    }

    //判断菜单栏
    @ParameterizedTest
    @Order(4)
    @CsvSource(value = {"主页,http://62.234.10.69/blog/blog-list.html","写博客,http://62.234.10.69/blog/blog-add.html","个人中心,http://62.234.10.69/blog/blog-center.html"})
    void getMenuBar(String element,String url){
        MenuBar.menuBarLogin(element, url);
        webDriver.navigate().back();//页面回退
    }


    //修改文章,异常
    @Order(5)
    @ParameterizedTest
    @CsvFileSource(resources = "editArt.csv")
    void EditFail(String title,String content,String msg) throws InterruptedException {

        WebDriverWait waitButton = new WebDriverWait(webDriver,30);
        waitButton.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#submit")));
        //清空标题
        webDriver.findElement(By.cssSelector("#title")).clear();
        String element = "#submit";
        Article.artFail(title, content, msg, element);

    }

    //修改文章,点击保存,点击确定,会跳转到个人中心页
    @Order(6)
    @ParameterizedTest
    @CsvSource(value = {"江山代有才人出-各领风骚数百年,江山代有才人出-各领风骚数百年,修改文章成功!"})
    void EditSuccess(String title ,String content,String msg) throws InterruptedException {

        WebDriverWait wait = new WebDriverWait(webDriver,10);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#submit")));

        //清空标题
        webDriver.findElement(By.cssSelector("#title")).clear();

        //此代码通用,修改文章,成功
        String element = "#submit";
        Article.artFail(title, content, msg, element);


        //判断url
        WebDriverWait waitFrame = new WebDriverWait(webDriver, 10);
        waitFrame.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#ifDiv"))); //等待frame可以被点击
        //跳转子页面
        webDriver.switchTo().frame(webDriver.findElement(By.cssSelector("#ifDiv")));

        //等待,标题可以被点击
        WebDriverWait waitTitle = new WebDriverWait(webDriver, 10);
        waitTitle.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#artList > div:nth-child(1) > a")));

        //获取第一篇,标题
        String cur_title = webDriver.findElement(By.cssSelector("#artList > div:nth-child(1) > a")).getText(); //查找第一篇文章
        Assertions.assertEquals(title,cur_title);

        //判断时间
        String cur_time = webDriver.findElement(By.cssSelector("#artList > div > div.articleButton > span:nth-child(1)")).getText(); //查找第一篇文章
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");//指定格式
        LocalDateTime localDate = LocalDateTime.parse(cur_time, format);//把页面获取的String时间转换成LocalDateTime
        LocalDateTime time = LocalDateTime.now();//获取当前时间
        LocalDateTime timeAfter = time.plusSeconds(5);//获取当前时间,并增加30秒
        LocalDateTime timeBefore = time.minusSeconds(5);//获取当前的时间,并减去30秒
        String result = "";
        if( timeBefore.isBefore(localDate) && timeAfter.isAfter(localDate)){
            result = "时间测试通过";
            System.out.println("时间测试通过");
        }else{
            result = "时间测试不通过";
            System.out.println("时间测试不通过");
        }
        Assertions.assertEquals("时间测试通过",result);
        //Assertions.assertEquals(time1,cur_time);

        //跳出子页面
        webDriver.switchTo().defaultContent();
        //回退
        webDriver.navigate().back();
    }



}
