package com.example.demo.seleniumTest;

import com.example.demo.common.*;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-01-05
 * Time: 19:31
 */
//博客详情页,正常登录状态
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)//人工控制执行顺序
public class DetailTest extends LoginInit {

    //到指定详情页
    @BeforeEach
    void initget(){
        String blogUrl = WebVariable.BLOG_DETAIL;
        blogUrl = blogUrl + "?id=32";
        webDriver.get(blogUrl);
        WebDriverWait waitArticle = new WebDriverWait(webDriver,10);
        waitArticle.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#ifDiv"))); //等待子页面可以被点击
    }


    //判断阅读量
    @Test
    @Disabled
    @Order(1)
    void readCount1() throws InterruptedException {
        //进入页面+1
        //webDriver.navigate().back();//回退
        String blogUrl = WebVariable.BLOG_DETAIL;
        blogUrl = blogUrl + "?id=119";
        webDriver.get(blogUrl);
        WebDriverWait wait = new WebDriverWait(webDriver,10);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#ifDiv")));
        ((JavascriptExecutor)webDriver).executeScript("location.reload()");//刷新
        WebDriverWait waitReload = new WebDriverWait(webDriver,10);
        waitReload.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#ifDiv")));
        //记录原来阅读量
        int rcount = Integer.parseInt(webDriver.findElement(By.cssSelector("#rcount")).getText());
        String rCount = webDriver.findElement(By.cssSelector("#rcount")).getText();
        System.out.println(rCount);
        System.out.println(rcount);


        //进入页面,阅读量+1
        //int inRC = Integer.parseInt(webDriver.findElement(By.cssSelector("#rcount")).getText());
        //Assertions.assertEquals(rcount+1,inRC);
        //刷新+1
        //webDriver.navigate().refresh();
        ((JavascriptExecutor)webDriver).executeScript("location.reload()");
        // Thread.sleep(1000);

        //防止获取到原来的默认数字
        WebDriverWait waitArtTotal = new WebDriverWait(webDriver,5);
        waitArtTotal.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#ifDiv")));
        //waitArtTotal.until(ExpectedConditions.not(ExpectedConditions.textToBe(By.cssSelector("#rcount"),rCount)));

//        //等到阅读量加载到页面上
//        WebDriverWait waitRC = new WebDriverWait(webDriver,10);
//        waitRC.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#rcount")));
        int refreshRC = Integer.parseInt(webDriver.findElement(By.cssSelector("#rcount")).getText());
        Assertions.assertEquals(rcount+1,refreshRC);
    }

    //判断阅读量
    @Test
    @Order(1)
    void readCount() throws InterruptedException {
        int oldCount = Article.toArtDetail();
        int newCount = Article.toArtDetail();
        Assertions.assertEquals(oldCount+1,newCount);

    }

    //博客详情
    @ParameterizedTest
    @CsvSource(value = {"反脆弱,2023-12-29 22:07:49"})
    @Order(2)
    void getDetail(String title,String time) throws IOException, InterruptedException {

//        //截图
//        File file = ((TakesScreenshot)webDriver).getScreenshotAs(OutputType.FILE);
//        String name = UUID.randomUUID() +".png";
//        name = name.replace("-","");
//        File filename = new File("./src/main/resources/photo/getDetail"+name);
//        FileUtils.copyFile(file,filename);

        WebDriverWait waitTitle = new WebDriverWait(webDriver,10);
        waitTitle.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#title")));
        waitTitle.until(ExpectedConditions.and(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#content"))));

        //校验标题
        String cur_title = webDriver.findElement(By.cssSelector("#title")).getText();
        Assertions.assertEquals(title,cur_title);
        //校验时间
        String cur_time = webDriver.findElement(By.cssSelector("#updatetime")).getText();
        Assertions.assertEquals(time,cur_time);
        //校验文章详情
        String cur_content = webDriver.findElement(By.cssSelector("#content")).getText();
        Assertions.assertNotNull(cur_content);
    }
    //判断博客作者信息
    @ParameterizedTest
    @Order(3)
    @CsvSource(value = {"peach11"})
    void getAuthor(String author){
        WebDriverWait waitUser = new WebDriverWait(webDriver,10);
        waitUser.until(ExpectedConditions.not(ExpectedConditions.textToBe(By.cssSelector("#art"),"-1")));
        //判断作者名
        String cur_author = webDriver.findElement(By.cssSelector("#user")).getText();
        Assertions.assertEquals(author,cur_author);
        //判断文章数量
        String cur_artNumber = webDriver.findElement(By.cssSelector("#art")).getText();
        //Assertions.assertEquals(artNumber,cur_artNumber);
        Assertions.assertNotEquals("-1",cur_artNumber);
        //判断头像
        String img = webDriver.findElement(By.cssSelector("#headimg")).getAttribute("src");
        String width = webDriver.findElement(By.cssSelector("#headimg")).getAttribute("width");
        img = img.substring(img.lastIndexOf(".")+1);;
        Assertions.assertEquals("jpeg",img);
        Assertions.assertEquals("140",width);
    }





    //评论的显示
    @Order(4)
    //@Test
    @ParameterizedTest
    @CsvSource(value = {"3,2"})
//    @CsvFileSource(resources = "commentShow.csv")
    void getComment(int comments,int replies) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(webDriver,10);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#ifDiv")));


        //进入子页面
        webDriver.switchTo().frame(webDriver.findElement(By.cssSelector("#ifDiv")));
        WebDriverWait waitComment = new WebDriverWait(webDriver,10);
        waitComment.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(".comment-info"),0));
        //判断一级评论的数量
        int cur_comments = webDriver.findElements(By.cssSelector(".comment-info")).size();
        //Assertions.assertEquals(comments,cur_comments);
        Assertions.assertNotEquals(0,cur_comments);
        //判断二级评论的数量
        int cur_replies = webDriver.findElements(By.cssSelector(".reply")).size();
        //Assertions.assertEquals(replies,cur_replies);
        Assertions.assertNotEquals(0,cur_replies);
        //此时还没有跳出子页面
    }

    //评论的添加,失败
    //回复自己,回复为空,回复全是换行符空格
    @Order(5)
    @ParameterizedTest
    @CsvSource(value = {"'',文本为空!","'          ',文本为空!","换行符,文本为空!"})
   // @CsvFileSource(resources = "addComment.csv")
    void addCommentFail(String content,String msg){

        WebDriverWait wait = new WebDriverWait(webDriver,10);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#ifDiv")));
        //进入子页面
        webDriver.switchTo().frame(webDriver.findElement(By.cssSelector("#ifDiv")));

        WebDriverWait waitComment = new WebDriverWait(webDriver,10);
        waitComment.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#content1")));
        //添加评论
        if(content.equals("换行符")){
            //键盘输入,换行符
            webDriver.findElement(By.cssSelector("#content1")).sendKeys(Keys.ENTER);
        }else {
            webDriver.findElement(By.cssSelector("#content1")).sendKeys(content);
        }
        //webDriver.findElement(By.cssSelector("#content1")).sendKeys(content);
        webDriver.findElement(By.cssSelector("#comment1")).click();

        //等待弹窗出现
        WebDriverWait waitAlert = new WebDriverWait(webDriver,10);
        waitAlert.until(ExpectedConditions.alertIsPresent());

        //判断弹窗提示内容
        String alert = webDriver.switchTo().alert().getText();//获取提示
        Assertions.assertEquals(msg,alert);
        webDriver.switchTo().alert().accept();//点击确认,弹窗消失
    }

    //添加回复,失败
    @ParameterizedTest
    @Order(6)
    @CsvSource(value = {"'       ',文本为空!","'',文本为空!","换行符,文本为空!"})
    void addReplyFail(String content,String msg){

        WebDriverWait waitF = new WebDriverWait(webDriver,10);
        waitF.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#ifDiv")));
        //进入子页面
        webDriver.switchTo().frame(webDriver.findElement(By.cssSelector("#ifDiv")));

        WebDriverWait wait = new WebDriverWait(webDriver,10);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".reply-btn")));

        //找到回复按钮
        List<WebElement> webElements = webDriver.findElements(By.cssSelector(".comment-info"));
        WebElement cur_webElement = webElements.get(2);

        //点击回复
        cur_webElement.findElement(By.cssSelector(".reply-btn")).click();

        //添加回复
        if(content.equals("换行符")){
            WebDriverWait waitText = new WebDriverWait(webDriver,10);
            waitText.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".replybox textarea")));
            //键盘输入,换行符
            cur_webElement.findElement(By.cssSelector(".mytextarea")).sendKeys(Keys.ENTER);
        }else {
            cur_webElement.findElement(By.cssSelector(".mytextarea")).sendKeys(content);
        }
        cur_webElement.findElement(By.cssSelector(".send")).click();

        //等待弹窗出现
        WebDriverWait waitAlert = new WebDriverWait(webDriver,5);
        waitAlert.until(ExpectedConditions.alertIsPresent());

        //判断弹窗提示内容
        String alert = webDriver.switchTo().alert().getText();//获取提示
        Assertions.assertEquals(msg,alert);
        webDriver.switchTo().alert().accept();//点击确认,弹窗消失
    }

    //回复自己,失败
    @ParameterizedTest
    @CsvSource(value = {"body > div.container > div > div:nth-child(1) > div > div.comment-content-footer > div > div.col-md-2 > span.reply-btn","#\\31 3 > div:nth-child(1) > p > span.reply-list-btn"})
    @Order(7)
    void replyMyself(String element){

        WebDriverWait wait = new WebDriverWait(webDriver,10);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#ifDiv")));
        //进入子页面
        webDriver.switchTo().frame(webDriver.findElement(By.cssSelector("#ifDiv")));

        //回复自己
        webDriver.findElement(By.cssSelector(element)).click();

        //等待弹窗出现
        WebDriverWait waitAlert = new WebDriverWait(webDriver,10);
        waitAlert.until(ExpectedConditions.alertIsPresent());

        //判断弹窗提示内容
        String alert = webDriver.switchTo().alert().getText();//获取提示
        Assertions.assertEquals("不能回复自己!",alert);
        webDriver.switchTo().alert().accept();//点击确认,弹窗消失

    }

    //回复他人,成功
    //@ParameterizedTest
    //@CsvFileSource(resources = "")
    //@Test
    @ParameterizedTest
    @CsvSource(value = {"在下名侦探小桃子"})
    @Order(8)
    //void addCommentSuccess(String content,String element,String elReply,String elContent,String elSend){
    void addReplySuccess(String content) throws InterruptedException {

        WebDriverWait wait = new WebDriverWait(webDriver,10);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#ifDiv")));
        //进入子页面
        webDriver.switchTo().frame(webDriver.findElement(By.cssSelector("#ifDiv")));


        WebDriverWait waitReply = new WebDriverWait(webDriver,10);
        waitReply.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".reply-btn")));
        //找到回复按钮
        List<WebElement> webElements = webDriver.findElements(By.cssSelector(".comment-info"));
        WebElement cur_webElement = webElements.get(2);

        //点击回复
        cur_webElement.findElement(By.cssSelector(".reply-btn")).click();
        //添加回复
        cur_webElement.findElement(By.cssSelector(".mytextarea")).sendKeys(content);
        cur_webElement.findElement(By.cssSelector(".send")).click();//点击发送

        //等待刷新
        WebDriverWait waitRefresh = new WebDriverWait(webDriver,10);
        int size = webDriver.findElements(By.cssSelector(".reply-list-btn")).size();
        waitRefresh.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(".reply-list-btn"),size)); //回复按钮,比之前多,说明刷新出来了
        WebDriverWait waitRefreshC = new WebDriverWait(webDriver,10);
        waitRefreshC.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".comment-info")));//


        //元素刷新了,得重新找到
        //找到回复按钮
        List<WebElement> verify_webElements = webDriver.findElements(By.cssSelector(".comment-right"));
        cur_webElement = verify_webElements.get(2);

        //验证回复
        //验证回复人名字
        List<WebElement> reply_Elements = cur_webElement.findElements(By.cssSelector(".reply"));
        WebElement reply_Element  = reply_Elements.get(0); //回复列表的第一个回复

        String cur_username = reply_Element.findElements(By.cssSelector("a")).get(0).getText();//回复人
        Assertions.assertEquals("tenjutest",cur_username);
        //验证被回复人名字
        String beReply =  cur_webElement.findElement(By.cssSelector("h3 a")).getText(); //被回复人,一级评论
        String cur_beReply = reply_Element.findElements(By.cssSelector("a")).get(1).getText(); //被回复人,二级评论
        beReply = "@"+beReply;
        Assertions.assertEquals(beReply,cur_beReply);
        //验证内容
        String cur_content = reply_Element.findElement(By.cssSelector("div span")).getText();//回复内容
        Assertions.assertEquals(content,cur_content);
        //验证时间
        String cur_time = reply_Element.findElements(By.cssSelector("p span")).get(0).getText();//回复时间
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");//指定格式
        LocalDateTime localDate = LocalDateTime.parse(cur_time, format);//把页面获取的String时间转换成LocalDateTime
        LocalDateTime time = LocalDateTime.now();//获取当前时间
        LocalDateTime timeAfter = time.plusSeconds(5);//获取当前时间,并增加5秒
        LocalDateTime timeBefore = time.minusSeconds(5);//获取当前的时间,并减去5秒
        String result = "";
        if( timeBefore.isBefore(localDate) && timeAfter.isAfter(localDate)){
            result = "时间测试通过";
            System.out.println("时间测试通过");
        }else{
            result = "时间测试不通过";
            System.out.println("local = "+ localDate);
            System.out.println("timeN = "+time);
            System.out.println("timeB = "+timeBefore);
            System.out.println("timeA = " + timeAfter);
            System.out.println("时间测试不通过");
        }
        Assertions.assertEquals("时间测试通过",result);



    }

    //添加文章评论,成功
    @ParameterizedTest
    @CsvSource(value = {"在下名侦探小桃子"})
    @Order(9)
    void addCommentSuccess(String content) {

        WebDriverWait wait = new WebDriverWait(webDriver,10);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#ifDiv")));
        //进入子页面
        webDriver.switchTo().frame(webDriver.findElement(By.cssSelector("#ifDiv")));

        //评论成功
        //输入内容
        webDriver.findElement(By.cssSelector("#content1")).sendKeys(content); //输入内容
        //点击发送按钮
        webDriver.findElement(By.cssSelector("#comment1")).click();//点击评论

        //等待刷新
        WebDriverWait waitRefresh = new WebDriverWait(webDriver,10);
        int size = webDriver.findElements(By.cssSelector(".reply-btn")).size();
        waitRefresh.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(".reply-btn"),size)); //回复按钮,比之前多,说明刷新出来了
        //验证回复
        String cur_username = webDriver.findElement(By.cssSelector("body > div.container > div > div:nth-child(1) > div > h3 > a")).getText();//回复人
        Assertions.assertEquals("tenjutest",cur_username);
        //验证内容
        String cur_content = webDriver.findElement(By.cssSelector("body > div.container > div > div:nth-child(1) > div > div.content")).getText();//回复内容
        Assertions.assertEquals(content,cur_content);
        //验证时间
        String cur_time = webDriver.findElement(By.cssSelector("body > div.container > div > div:nth-child(1) > div > div.comment-content-footer > div > div.col-md-2 > span:nth-child(1)")).getText();//回复时间
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
    }

    //判断菜单栏
    @ParameterizedTest
    @Order(10)
    @CsvSource(value = {"主页,http://62.234.10.69/blog/blog-list.html","写博客,http://62.234.10.69/blog/blog-add.html","个人中心,http://62.234.10.69/blog/blog-center.html"})
    void getMenuBar(String element,String url){
        MenuBar.menuBarLogin(element, url);
//        webDriver.switchTo().defaultContent();//回到父页面
//        String link = webDriver.findElement(By.linkText(element)).getAttribute("href");
//        Assertions.assertEquals(msg,link);
    }






}
