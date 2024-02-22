package com.example.demo.seleniumTest;

import com.example.demo.common.GetAuthor;
import com.example.demo.common.LoginInit;
import com.example.demo.common.MenuBar;
import com.example.demo.common.WebVariable;
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
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-01-05
 * Time: 23:11
 */
//个人中心页,正常登陆
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)//人工控制执行顺序
public class CenterTest extends LoginInit {
    @BeforeEach
    void initCenter(){
        webDriver.get(WebVariable.BLOG_CENTER);
        WebDriverWait wait = new WebDriverWait(webDriver,10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#username")));
    }

    //判断用户信息
    @Order(1)
    @Test
    void getAuthor(){
        String username = "tenjutest";
        String element = "#artTotal";
        GetAuthor.getAuthor(username,element);
    }

    //修改头像,失败
    //直接点击上传,上传不是图片,上传后缀名是图片但不是图片
    //@Test
    @Order(3)
    @ParameterizedTest
    @CsvFileSource(resources = "uploadPhoto.csv")
    void uploadPhotoFail(String path,String msg) throws IOException, InterruptedException {

        webDriver.switchTo().defaultContent();
        //点击头像
        webDriver.findElement(By.cssSelector("#headimg")).click();
        //到子页面
        webDriver.switchTo().frame(webDriver.findElement(By.cssSelector("#ifDiv")));
        //选择文件,上传
        //webDriver.findElement(By.cssSelector("body > div > div > div:nth-child(1) > label")).click();
        String namePhoto = System.getProperty("user.dir");
        namePhoto = namePhoto+path;
        System.out.println(namePhoto);
        webDriver.findElement(By.cssSelector("#f1")).sendKeys(namePhoto);

        //点击上传
        webDriver.findElement(By.cssSelector("body > div > div > div.check > input:nth-child(2)")).click();


        //等待弹窗
        //等待弹窗出现
        WebDriverWait waitAlert = new WebDriverWait(webDriver,30);
        waitAlert.until(ExpectedConditions.alertIsPresent());

        String alertText = webDriver.switchTo().alert().getText();//判断弹窗提示内容
        Assertions.assertEquals(msg,alertText);
        webDriver.switchTo().alert().accept(); //关闭弹窗

//        //截图
//        File file = ((TakesScreenshot)webDriver).getScreenshotAs(OutputType.FILE);
//        String name = UUID.randomUUID() +".png";
//        name = name.replace("-","");
//        File filename = new File("./src/main/resources/photo/"+name);
//        FileUtils.copyFile(file,filename);

    }

    //获取我的文章
    @Order(2)
    @Test
    void getArt() throws IOException {
        //获取文章
        //回到父页面
        webDriver.switchTo().defaultContent();//回到父页面

        //防止获取到原来的默认数字
        WebDriverWait waitArtTotal = new WebDriverWait(webDriver,10);
        //waitArtTotal.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#ifDiv")));
        waitArtTotal.until(ExpectedConditions.not(ExpectedConditions.textToBe(By.cssSelector("#artTotal"),"-1")));

        //父页面的文章数
        int artNumbers = Integer.parseInt(webDriver.findElement(By.cssSelector("#artTotal")).getText());

        //到子页面
        webDriver.switchTo().frame(webDriver.findElement(By.cssSelector("#ifDiv")));
        WebDriverWait waitDraftList = new WebDriverWait(webDriver,10);//等待获取文章列表
        waitDraftList.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".articleList")));

//        //截图
//        File file = ((TakesScreenshot)webDriver).getScreenshotAs(OutputType.FILE);
//        String name = UUID.randomUUID() +".png";
//        name = name.replace("-","");
//        File filename = new File("./src/main/resources/photo/getArt"+name);
//        FileUtils.copyFile(file,filename);

        //获取文章
        //文章数量>0
        int cur_number = webDriver.findElements(By.cssSelector(".articleList")).size();
        Assertions.assertNotEquals(0,cur_number);//不为0
        Assertions.assertEquals(artNumbers,cur_number);

    }

    //修改头像成功
    @ParameterizedTest
    @Order(11)
    @CsvSource(value = {"/src/main/resources/photo/upload/正常图片.jpeg,修改头像成功!"})
    void uploadPhotoSuccess(String path,String msg) throws InterruptedException {

        //回到父页面
        webDriver.switchTo().defaultContent();//回到父页面

        //获取头像属性
        String oldPhoto = webDriver.findElement(By.cssSelector("#headimg")).getAttribute("src");

        //点击头像
        webDriver.findElement(By.cssSelector("#headimg")).click();
        //到子页面
        webDriver.switchTo().frame(webDriver.findElement(By.cssSelector("#ifDiv")));
        //选择文件,上传
        String namePhoto = System.getProperty("user.dir");
        namePhoto = namePhoto+path;
        webDriver.findElement(By.cssSelector("#f1")).sendKeys(namePhoto);
        //点击上传
        webDriver.findElement(By.cssSelector("body > div > div > div.check > input:nth-child(2)")).click();

        //判断上传
        webDriver.switchTo().defaultContent();//回到父页面

        //等待弹窗
        //等待弹窗出现
        WebDriverWait waitAlert = new WebDriverWait(webDriver,30);
        waitAlert.until(ExpectedConditions.alertIsPresent());

        String alertText = webDriver.switchTo().alert().getText();//判断弹窗提示内容
        Assertions.assertEquals(msg,alertText);
        webDriver.switchTo().alert().accept(); //关闭弹窗

        //等待刷新
        //防止获取到原来的图片路径,当src属性内容变了
        WebDriverWait waitPhoto = new WebDriverWait(webDriver,10);
        waitPhoto.until(ExpectedConditions.not(ExpectedConditions.attributeToBe(webDriver.findElement(By.cssSelector("#headimg")),"src",oldPhoto)));


        String newPhoto = webDriver.findElement(By.cssSelector("#headimg")).getAttribute("src");
        Assertions.assertNotEquals(newPhoto,oldPhoto);


    }



    //我的文章,功能,浏览
    @Order(5)
    @ParameterizedTest
    @CsvSource(value = {"我的文章","我的草稿"})
    void artDetail (String choice) throws InterruptedException {
        //回到父页面
        webDriver.switchTo().defaultContent();//回到父页面

        //父页面的文章数
        int artNumbers = Integer.parseInt(webDriver.findElement(By.cssSelector("#artTotal")).getText());

        //到子页面
        webDriver.switchTo().frame(webDriver.findElement(By.cssSelector("#ifDiv")));

        if(choice.equals("我的草稿")){
            WebDriverWait waitDraft = new WebDriverWait(webDriver,10);
            waitDraft.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#mydraftlist")));
            webDriver.findElement(By.cssSelector("#mydraftlist")).click(); //点击草稿
            webDriver.switchTo().defaultContent();//跳出frame,默认是最外面默认页面
            webDriver.switchTo().frame(webDriver.findElement(By.cssSelector("#ifDiv")));//切换到子页面

        }
        WebDriverWait waitDraftList = new WebDriverWait(webDriver,10);//等待获取文章列表
        waitDraftList.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".articleList")));

        //获取文章详情
        //文章浏览
        //获取文章详情
        List<WebElement> webElements = webDriver.findElements(By.cssSelector(".articleList"));
        WebElement webElement = webElements.get(0);
        String title = webElement.findElement(By.cssSelector(".articleName")).getText();
        String content = webElement.findElement(By.cssSelector(".articleContent1")).getText();//获取正文
        String time = webElement.findElement(By.cssSelector(".articleContent")).getText();//获取时间
        //判断详情不为空
        Assertions.assertNotNull(title); //判断标题不为空
        Assertions.assertNotNull(content);//判断正文不为空
        Assertions.assertNotNull(time); //判断时间不为空
        webElement.findElement(By.linkText("浏览")).click();//点击编辑按钮

        //等待详情页刷新出来
        WebDriverWait waitDetail = new WebDriverWait(webDriver,10);
        waitDetail.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#content")));

        //获取文章详情
        String cur_title = webDriver.findElement(By.cssSelector("#title")).getText();
        Assertions.assertEquals(title,cur_title);//文章标题
        String cur_content = webDriver.findElement(By.cssSelector("#content")).getText();
        Assertions.assertNotNull(cur_content);//文章正文
        String cur_time = webDriver.findElement(By.cssSelector("#updatetime")).getText();
        Assertions.assertEquals(time,cur_time);//文章时间

        //可以找得到评论
        WebDriverWait waitComment = new WebDriverWait(webDriver,10);
        if(choice.equals("我的草稿")){
            waitComment.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("#ifDiv")));//找不到评论
        }else {
            waitComment.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#ifDiv"))); //可以找到评论
        }

    }

    //我的文章,编辑
    @Order(6)
    @ParameterizedTest
    @CsvSource(value = {"我的文章","我的草稿"})
    void artEdit (String choice) throws InterruptedException, IOException {
        //回到父页面
        webDriver.switchTo().defaultContent();//回到父页面

        //父页面的文章数
        int artNumbers = Integer.parseInt(webDriver.findElement(By.cssSelector("#artTotal")).getText());

        //到子页面
        webDriver.switchTo().frame(webDriver.findElement(By.cssSelector("#ifDiv")));

        if(choice.equals("我的草稿")){
            WebDriverWait waitDraft = new WebDriverWait(webDriver,10);
            waitDraft.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#mydraftlist")));
            webDriver.findElement(By.cssSelector("#mydraftlist")).click(); //点击草稿
            webDriver.switchTo().defaultContent();//跳出frame,默认是最外面默认页面
            webDriver.switchTo().frame(webDriver.findElement(By.cssSelector("#ifDiv")));//切换到子页面

        }

        WebDriverWait waitDraftList = new WebDriverWait(webDriver,10);//等待获取文章列表
        waitDraftList.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".articleList")));

        //文章浏览
        //获取文章详情
        List<WebElement> webElements = webDriver.findElements(By.cssSelector(".articleList"));
        WebElement webElement = webElements.get(0);
        String title = webElement.findElement(By.cssSelector(".articleName")).getText();
        String content = webElement.findElement(By.cssSelector(".articleContent1")).getText();
        webElement.findElement(By.linkText("编辑")).click();//点击编辑按钮
        //判断详情不为空
        Assertions.assertNotNull(title); //判断标题不为空
        Assertions.assertNotNull(content);//判断正文不为空

        //等待详情页刷新出来
        //等待,提交按钮,可以被点击
        WebDriverWait waitSubmit = new WebDriverWait(webDriver, 30);
        //waitSubmit.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#editor")));
        //presenceOfElementLocated(By locator)：判断某个元素是否被加到了dom树里，并不代表该元素一定可见；
        waitSubmit.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#editor .editormd-markdown-textarea")));
        waitSubmit.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".CodeMirror-scroll")));
        WebDriverWait waitTitle = new WebDriverWait(webDriver, 10);
        waitTitle.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#title")));


        //点击获取正文
        //获取文本editor.getValue(),清空文本editor.setValue("")
        String editContent = "return editor.getValue()";
        String cur_contnet = (String) ((JavascriptExecutor)webDriver).executeScript(editContent);

        //获取文章详情
        String cur_title = webDriver.findElement(By.cssSelector("#title")).getAttribute("value");
        Assertions.assertEquals(title,cur_title);//文章标题

//        //截图
//        File file = ((TakesScreenshot)webDriver).getScreenshotAs(OutputType.FILE);
//        String name = UUID.randomUUID() +".png";
//        name = name.replace("-","");
//        File filename = new File("./src/main/resources/photo/artEdit"+name);
//        FileUtils.copyFile(file,filename);

        System.out.println("c"+content);
        System.out.println(cur_contnet);
        //Assertions.assertEquals(content, cur_contnet);
        Assertions.assertNotEquals("",cur_contnet); //判断正文不为空


    }

    //我的文章,删除
    @Order(7)
    @ParameterizedTest
    @CsvSource(value = {"我的文章,是否要删除文章: ","我的草稿"})
    void artDelete(String choice) throws IOException {

        //回到父页面
        webDriver.switchTo().defaultContent();//回到父页面

        //防止获取到原来的默认数字
        WebDriverWait waitArtTotal = new WebDriverWait(webDriver,10);
        //waitArtTotal.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#ifDiv")));
        waitArtTotal.until(ExpectedConditions.not(ExpectedConditions.textToBe(By.cssSelector("#artTotal"),"-1")));
        //父页面的文章数
        int artNumbers = Integer.parseInt(webDriver.findElement(By.cssSelector("#artTotal")).getText());

        //到子页面
        webDriver.switchTo().frame(webDriver.findElement(By.cssSelector("#ifDiv")));

        if(choice.equals("我的草稿")){
            WebDriverWait waitDraft = new WebDriverWait(webDriver,10);
            waitDraft.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#mydraftlist")));
            webDriver.findElement(By.cssSelector("#mydraftlist")).click(); //点击草稿
            webDriver.switchTo().defaultContent();//跳出frame,默认是最外面默认页面
            webDriver.switchTo().frame(webDriver.findElement(By.cssSelector("#ifDiv")));//切换到子页面

        }
        WebDriverWait waitDraftList = new WebDriverWait(webDriver,10);//等待获取文章列表
        waitDraftList.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".articleList")));

        //文章删除
        //获取文章详情
        List<WebElement> webElements = webDriver.findElements(By.cssSelector(".articleList"));
        int size = webElements.size();//控件数量
        WebElement webElement = webElements.get(0); //选取第一篇文章
        String title = webElement.findElement(By.cssSelector(".articleName")).getText();//文章标题
        String time = webElement.findElement(By.cssSelector(".articleContent")).getText();//获取时间
        Assertions.assertNotNull(title); //判断标题不为空
        String msg = "是否要删除文章: " + title+" ?"; //弹窗提示信息

        if(choice.equals("我的文章")){
//            //截图
//            File file = ((TakesScreenshot)webDriver).getScreenshotAs(OutputType.FILE);
//            String name = UUID.randomUUID() +".png";
//            name = name.replace("-","");
//            File filename = new File("./src/main/resources/photo/artDelete"+name);
//            FileUtils.copyFile(file,filename);

            //如果是文章页,父页面文章数,和当前列表控件数量一致
            Assertions.assertEquals(artNumbers,size);
        }

        webElement.findElement(By.linkText("删除")).click();//点击删除按钮
        //等待弹窗出现
        WebDriverWait waitAlert = new WebDriverWait(webDriver,20);
        waitAlert.until(ExpectedConditions.alertIsPresent());

        //判断弹窗提示内容
        String alert = webDriver.switchTo().alert().getText();//获取提示
        Assertions.assertEquals(msg,alert); //判断弹窗提示信息
        webDriver.switchTo().alert().accept();//点击确认,弹窗消失

        //等待文章完成删除文章操作
        //显式等待,文章数量少于之前的
        WebDriverWait waitSubmit = new WebDriverWait(webDriver, 10);
        waitSubmit.until(ExpectedConditions.numberOfElementsToBeLessThan(By.cssSelector(".articleList"),size));

        //我的草稿,文章数量不会变
        List<WebElement> newWebElements = webDriver.findElements(By.cssSelector(".articleList"));
        int cur_size = newWebElements.size();//控件数量
        Assertions.assertEquals(size-1,cur_size); //控件数量会减少一个

        //删除文章之后,还有文章的话
        if(cur_size!=0){
            WebElement cur_ebElement = newWebElements.get(0); //选取第一篇文章
            //文章标题可能一致,但时间不太可能一致
            // String cur_title = cur_ebElement.findElement(By.cssSelector(".articleName")).getText();//文章标题
            String cur_time = cur_ebElement.findElement(By.cssSelector(".articleContent")).getText();//获取时间
            Assertions.assertNotNull(cur_time); //判断时间不为空
            System.out.println(time);
            System.out.println(cur_time);
            Assertions.assertNotEquals(time,cur_time); //两次时间不会一致

        }

        //判断主页文章数量的变化
        webDriver.switchTo().defaultContent();//回到父页面
        int cur_artNumbers = Integer.parseInt(webDriver.findElement(By.cssSelector("#artTotal")).getText());
        if(choice.equals("我的文章")){
            //删除已发布的文章,文章数量会发生改变
            int deleteAfter = artNumbers-1;
            Assertions.assertEquals(deleteAfter,cur_artNumbers); //文章数量会减少1
        }
    }

    //我的评论,显示
    @Order(8)
    @Test
    void getComment(){

        //回到父页面
        webDriver.switchTo().defaultContent();//回到父页面

        //点击评论
        webDriver.findElement(By.linkText("评论")).click(); //点击评论

        //到子页面
        webDriver.switchTo().frame(webDriver.findElement(By.cssSelector("#ifDiv")));

        WebDriverWait waitButton = new WebDriverWait(webDriver,10);
        waitButton.until(ExpectedConditions.elementToBeClickable(By.linkText("删除")));

        //获取评论
        List<WebElement> webElements = webDriver.findElements(By.linkText("删除"));
        int size = webElements.size();
        Assertions.assertNotEquals(0,size); //评论数量不为0

    }

    //我的评论,删除
    @Order(9)
    //@Test
    @ParameterizedTest
    @CsvSource(value = {"1","2"})
    void deleteComment(String element){

        //回到父页面
        webDriver.switchTo().defaultContent();//回到父页面

        //点击评论
        webDriver.findElement(By.linkText("评论")).click(); //点击评论

        //到子页面
        webDriver.switchTo().frame(webDriver.findElement(By.cssSelector("#ifDiv")));

        //等待删除按钮可以被点击
        WebDriverWait waitButton = new WebDriverWait(webDriver,10);
        waitButton.until(ExpectedConditions.elementToBeClickable(By.linkText("删除")));

        //获取评论数量
        List<WebElement> webElements = webDriver.findElements(By.linkText("删除"));
        int size = webElements.size();
        Assertions.assertNotEquals(0,size); //评论数量不为0

        //删除第一条评论
        webDriver.findElement(By.cssSelector("body > div > div.container > div > div:nth-child(1) > div.daohang > a.delect")).click();

        //等待弹窗出现
        WebDriverWait waitAlert = new WebDriverWait(webDriver,10);
        waitAlert.until(ExpectedConditions.alertIsPresent());

        //判断弹窗提示内容
        String alert = webDriver.switchTo().alert().getText();//获取提示
        Assertions.assertEquals("是否要删除评论 ?",alert); //判断弹窗提示信息
        webDriver.switchTo().alert().accept();//点击确认,弹窗消失

        //等待完成删除操作
        //显式等待,删除数量少于之前的
        WebDriverWait waitSubmit = new WebDriverWait(webDriver, 10);
        waitSubmit.until(ExpectedConditions.numberOfElementsToBeLessThan(By.linkText("删除"),size));
        waitSubmit.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.linkText("删除"),size-2));

        //控件数量-1
        List<WebElement> cur_WebElements = webDriver.findElements(By.linkText("删除"));
        int cur_size = cur_WebElements.size();
        Assertions.assertEquals(size-1,cur_size);


    }



    //判断菜单栏
    @ParameterizedTest
    @Order(10)
    @CsvSource(value = {"主页,http://62.234.10.69/blog/blog-list.html","写博客,http://62.234.10.69/blog/blog-add.html"})
    void getMenuBar(String element,String url){
        MenuBar.menuBarLogin(element,url);
    }



}
