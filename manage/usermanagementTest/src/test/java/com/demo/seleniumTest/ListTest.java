package com.demo.seleniumTest;

import com.demo.common.InitAndEnd;
import com.demo.common.WebVariable;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-02-23
 * Time: 18:32
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) //人工控制执行顺序
public class ListTest extends InitAndEnd {

//    @BeforeAll
//    static void init(){
//        //到达登录页面
//        webDriver.get(WebVariable.LOGIN);
//        WebDriverWait waitCaptcha = new WebDriverWait(webDriver,20);
//        waitCaptcha.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#captcha"))); //等待验证码可以被点击
//
//        //输入账号密码,并点击提交按钮
//        webDriver.findElement(By.cssSelector("#loginname")).sendKeys("tenjutest");
//        webDriver.findElement(By.cssSelector("#password")).sendKeys("123456789");
//        webDriver.findElement(By.cssSelector("#captchatext")).sendKeys("12345"); //验证码
//        webDriver.findElement(By.cssSelector("#submit")).click();
//
//        //显式等待,等待注销按钮的出现
//        WebDriverWait wait = new WebDriverWait(webDriver, 30);
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#toolbar > a:nth-child(3) > span > span.l-btn-text")));
//
//        //判断url
//        String cur_url = webDriver.getCurrentUrl();
//        Assertions.assertEquals(WebVariable.LIST,cur_url);//断言,判断是否跳转到博客列表页
//    }


    //登陆成功
    @Order(1)
    @ParameterizedTest
    @CsvSource(value = {"tenjutest,123456789,12345"})
    void loginSuccess(String username,String password,String captchatext) throws InterruptedException, IOException {
        //到达登录页面
        webDriver.get(WebVariable.LOGIN);
        WebDriverWait waitCaptcha = new WebDriverWait(webDriver,20);
        waitCaptcha.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#captcha"))); //等待验证码可以被点击

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

    //判断列表
    @Order(2)
    @Test
    void getList(){
        int size = webDriver.findElements(By.cssSelector("tr th input")).size();
        Assertions.assertNotEquals(0,size);
    }


    //分页功能
    @Order(3)
    @ParameterizedTest
    @CsvFileSource(resources = "paging.csv")
    void paging(String element,String msg){

        //获取分页提示
        String cur_text = webDriver.findElement(By.cssSelector("#pageinfo")).getText();

        webDriver.findElement(By.linkText(element)).click(); //点击分页按钮

        if(msg.equals("")){
            //跳转到了下一页,判断页码
            //等待列表出现
            WebDriverWait waitUpdate = new WebDriverWait(webDriver,10);
            waitUpdate.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#info tr")));

            String new_text = webDriver.findElement(By.cssSelector("#pageinfo")).getText();
            Assertions.assertNotEquals(cur_text,new_text);

        }else{
            //出现提示
            //显式等待,等待弹窗的出现
            WebDriverWait waitAlert = new WebDriverWait(webDriver, 30);
            waitAlert.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("body > div.panel.window.panel-htop.messager-window")));
            //判断弹窗,提示框文本
            String text = webDriver.findElement(By.cssSelector("body > div.panel.window.panel-htop.messager-window > div.messager-body.panel-body.panel-body-noborder.window-body > div:nth-child(2)")).getText();
            Assertions.assertEquals(msg,text);
            //弹窗确认
            webDriver.findElement(By.cssSelector("body > div.panel.window.panel-htop.messager-window > div.dialog-button.messager-button > a > span")).click();
        }

    }


    //新增成功
    @Order(4)
    @ParameterizedTest
    //@CsvSource(value = {"pppsss1,pppsss1,password,您是否要继续添加新用户?"})
    @CsvFileSource(resources = "addUserSuccess.csv")
    void addUserSuccess(String username,String loginname,String password,String msg) throws InterruptedException {
       // webDriver.findElement(By.cssSelector("#toolbar > a:nth-child(1)")).click();
        webDriver.findElement(By.linkText("新增")).click();

        //等待新增弹窗出现
        WebDriverWait wait = new WebDriverWait(webDriver,10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#fmN")));

        //填写用户信息

        webDriver.findElement(By.cssSelector("#_easyui_textbox_input5")).sendKeys(username);
        webDriver.findElement(By.cssSelector("#_easyui_textbox_input6")).sendKeys(loginname);

        webDriver.findElement(By.cssSelector("#_easyui_textbox_input11")).sendKeys(password);
        Thread.sleep(500);
        webDriver.findElement(By.cssSelector("#_easyui_textbox_input12")).sendKeys(password);
        Thread.sleep(500);
        //点击保存
        webDriver.findElement(By.linkText("保存")).click();

        //显式等待,等待弹窗的出现
        WebDriverWait waitAlert = new WebDriverWait(webDriver, 30);
        waitAlert.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("body > div.panel.window.panel-htop.messager-window")));
        //判断弹窗,提示框文本
        String text = webDriver.findElement(By.cssSelector("body > div.panel.window.panel-htop.messager-window > div.messager-body.panel-body.panel-body-noborder.window-body > div:nth-child(2)")).getText();
        Assertions.assertEquals(msg,text);
        //弹窗确认
        webDriver.findElement(By.cssSelector("body > div.panel.window.panel-htop.messager-window > div.dialog-button.messager-button > a > span")).click();

        //关闭表单,点击表单的取消
        webDriver.findElement(By.cssSelector("#dg-buttons > a:nth-child(2)")).click();
    }

    //新增失败
    @Order(5)
    @ParameterizedTest
    @CsvFileSource(resources = "addUserFail.csv")
    void addUserFail(String username,String loginname,String password,String msg) throws InterruptedException{
        // webDriver.findElement(By.cssSelector("#toolbar > a:nth-child(1)")).click();
        webDriver.findElement(By.linkText("新增")).click();

        //等待新增弹窗出现
        WebDriverWait wait = new WebDriverWait(webDriver,10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#fmN")));

        //填写用户信息

        webDriver.findElement(By.cssSelector("#_easyui_textbox_input5")).sendKeys(username);
        webDriver.findElement(By.cssSelector("#_easyui_textbox_input6")).sendKeys(loginname);

        if(!password.equals("")){
            webDriver.findElement(By.cssSelector("#_easyui_textbox_input11")).sendKeys(password);
            Thread.sleep(500);
            if(password.equals("different")){
                password = password + "123";
            }
            webDriver.findElement(By.cssSelector("#_easyui_textbox_input12")).sendKeys(password);
            Thread.sleep(500);
        }

        //点击保存
        webDriver.findElement(By.linkText("保存")).click();

        //显式等待,等待弹窗的出现
        WebDriverWait waitAlert = new WebDriverWait(webDriver, 30);
        waitAlert.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("body > div.panel.window.panel-htop.messager-window")));
        //判断弹窗,提示框文本
        String text = webDriver.findElement(By.cssSelector("body > div.panel.window.panel-htop.messager-window > div.messager-body.panel-body.panel-body-noborder.window-body > div:nth-child(2)")).getText();
        Assertions.assertEquals(msg,text);
        //弹窗确认
        webDriver.findElement(By.cssSelector("body > div.panel.window.panel-htop.messager-window > div.dialog-button.messager-button > a > span")).click();

        //关闭表单,点击表单的取消
        webDriver.findElement(By.cssSelector("#dg-buttons > a:nth-child(2)")).click();

    }

    //修改用户失败
    @Order(6)
    @ParameterizedTest
    @CsvFileSource(resources = "editUserFail.csv")
    void editUserFail(String username,String password,String msg) throws InterruptedException{
        List<WebElement> allElements = webDriver.findElements(By.cssSelector("tr"));
        int size = allElements.size();
        WebElement w = allElements.get(size-1);
        w.findElement(By.linkText("修改")).click();

        //等待修改弹窗出现
        WebDriverWait wait = new WebDriverWait(webDriver,10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#fm")));

        //填写用户信息

        webDriver.findElement(By.cssSelector("#_easyui_textbox_input1")).clear();
        webDriver.findElement(By.cssSelector("#_easyui_textbox_input1")).sendKeys(username);
        Thread.sleep(2000);

        if(!password.equals("")){
            webDriver.findElement(By.cssSelector("#_easyui_textbox_input9")).sendKeys(password);
            Thread.sleep(500);
            if(password.equals("different")){
                password = password + "123";
            }
            webDriver.findElement(By.cssSelector("#_easyui_textbox_input10")).sendKeys(password);
            Thread.sleep(500);
        }

        //点击保存
        webDriver.findElement(By.linkText("保存")).click();

        //显式式等待,等待弹窗的出现
        WebDriverWait waitAlert = new WebDriverWait(webDriver, 30);
        waitAlert.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("body > div.panel.window.panel-htop.messager-window")));
        //判断弹窗,提示框文本
        String text = webDriver.findElement(By.cssSelector("body > div.panel.window.panel-htop.messager-window > div.messager-body.panel-body.panel-body-noborder.window-body > div:nth-child(2)")).getText();
        Assertions.assertEquals(msg,text);
        //弹窗确认
        webDriver.findElement(By.cssSelector("body > div.panel.window.panel-htop.messager-window > div.dialog-button.messager-button > a > span")).click();

        //关闭表单,点击表单的取消
        webDriver.findElement(By.cssSelector("#dlg-buttons > a:nth-child(2)")).click();

    }


    //修改成功
    @Order(7)
    @ParameterizedTest
    @CsvSource(value = {"小桃"})
    void editUserSuccess(String username)throws InterruptedException{
        //点击末页
        webDriver.findElement(By.cssSelector("#all > li:nth-child(4) > a")).click();

        //等待列表刷新
        WebDriverWait waitList = new WebDriverWait(webDriver,10);
        waitList.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#info tr")));

        List<WebElement> allElements = webDriver.findElements(By.cssSelector("tr"));
        int size = allElements.size();
        WebElement element = allElements.get(size-1);
        element.findElement(By.linkText("修改")).click();

        //等待修改弹窗出现
        WebDriverWait wait = new WebDriverWait(webDriver,10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#fm")));

        //填写用户信息
        webDriver.findElement(By.cssSelector("#_easyui_textbox_input1")).clear();
        webDriver.findElement(By.cssSelector("#_easyui_textbox_input1")).sendKeys(username);

        //点击保存
        webDriver.findElement(By.linkText("保存")).click();

        WebDriverWait waitUpdate = new WebDriverWait(webDriver,10);
        waitUpdate.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#info tr")));


        //等待刷新
        List<WebElement> allElementUpdate = webDriver.findElements(By.cssSelector("tr"));
        WebElement update = allElementUpdate.get(size-1);
        //String cur_username = update.findElements(By.cssSelector("th")).get(2).getText();
        String cur_username = update.findElement(By.name("username")).getText();
        Assertions.assertEquals(username,cur_username);
    }



    //查询
    @Order(8)
    @ParameterizedTest
    @CsvFileSource(resources = "searchSuccess.csv")
    void searchSuccess(String username,String address,String email){

        //输入框
        webDriver.findElement(By.cssSelector("#ipt_name")).sendKeys(username); //姓名
        webDriver.findElement(By.cssSelector("#ipt_address")).sendKeys(address); //地址
        webDriver.findElement(By.cssSelector("#ipt_email")).sendKeys(email); //邮箱
        webDriver.findElement(By.cssSelector("#submit1")).click(); //点击查询按钮

        //等待列表查询
        WebDriverWait waitUpdate = new WebDriverWait(webDriver,10);
        waitUpdate.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#info tr")));

        //判断列表数量
        List<WebElement> allElements = webDriver.findElements(By.cssSelector("#info tr"));
        int size = allElements.size();
        Assertions.assertNotEquals(0,size);

        //判断用户名字,是否包含预期字符串
        WebElement user = allElements.get(0);
        String cur_username = user.findElement(By.name("username")).getText();
        Boolean flag = cur_username.contains(username);
        Assertions.assertEquals(true,flag);

        //最后,刷新页面
        webDriver.navigate().refresh();
    }





    //删除按钮成功
    @Order(9)
    @ParameterizedTest
    @CsvSource(value = {"删除成功!"})
    void deleteSuccess(String msg){
        //点击末页
        webDriver.findElement(By.cssSelector("#all > li:nth-child(4) > a")).click();
        //点击末页
        //webDriver.findElement(By.linkText("末页")).click();

        //等待列表刷新
        WebDriverWait waitUpdate = new WebDriverWait(webDriver,10);
        waitUpdate.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#info tr")));

        List<WebElement> allElements = webDriver.findElements(By.cssSelector("tr"));
        int size = allElements.size();
        WebElement element = allElements.get(size-1);
        element.findElement(By.linkText("删除")).click();

        //等待弹窗出现
        WebDriverWait waitAlert = new WebDriverWait(webDriver, 30);
        waitAlert.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("body > div.panel.window.panel-htop.messager-window")));
        //判断弹窗,提示框文本
        String text = webDriver.findElement(By.cssSelector("body > div.panel.window.panel-htop.messager-window > div.messager-body.panel-body.panel-body-noborder.window-body > div:nth-child(2)")).getText();
        Assertions.assertEquals(msg,text);
        //弹窗确认
        webDriver.findElement(By.cssSelector("body > div.panel.window.panel-htop.messager-window > div.dialog-button.messager-button > a > span")).click();



    }

    //批量删除成功
    @Order(10)
    @ParameterizedTest
    @CsvSource(value = "myseleniumtest123")
    void deletesFail(String username) throws InterruptedException {
        //点击末页
       // webDriver.findElement(By.linkText("末页")).click();

        //刷新页面
        webDriver.navigate().refresh();

        //等待列表刷新
        WebDriverWait wait = new WebDriverWait(webDriver,10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#info tr")));

        //查找之前测试的时候,新增的用户
        webDriver.findElement(By.cssSelector("#ipt_name")).sendKeys(username);
        webDriver.findElement(By.cssSelector("#submit1")).click(); //点击查询按钮

        //等待列表刷新
        WebDriverWait waitUpdate = new WebDriverWait(webDriver,10);
        waitUpdate.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#info tr")));


        //选择多个
        List<WebElement> allElements = webDriver.findElements(By.cssSelector("#info tr"));
        int size = allElements.size();
        Assertions.assertEquals(2,size);

        for(int i = 0;i<2;i++){
            WebElement element = allElements.get(i);
            element.findElement(By.cssSelector("input")).click();
        }

        //点击批量删除
        webDriver.findElement(By.linkText("批量删除")).click();

        //等待弹窗出现
        //等待弹窗出现
        WebDriverWait waitDelete = new WebDriverWait(webDriver, 30);
        waitDelete.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("body > div.panel.window.panel-htop.messager-window")));
        //判断弹窗,提示框文本
        String textDelete = webDriver.findElement(By.cssSelector("body > div.panel.window.panel-htop.messager-window > div.messager-body.panel-body.panel-body-noborder.window-body > div:nth-child(2)")).getText();
        Boolean flag = textDelete.contains("您确定要删除下列用户吗？");
        Assertions.assertEquals(true,flag);
        //弹窗确认
        webDriver.findElement(By.cssSelector("body > div.panel.window.panel-htop.messager-window > div.dialog-button.messager-button > a > span")).click();


        //弹窗出现
        WebDriverWait waitAlert = new WebDriverWait(webDriver, 30);
        waitAlert.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("body > div.panel.window.panel-htop.messager-window")));
        //判断弹窗,提示框文本
        String text = webDriver.findElement(By.cssSelector("body > div.panel.window.panel-htop.messager-window > div.messager-body.panel-body.panel-body-noborder.window-body > div:nth-child(2)")).getText();
        Assertions.assertEquals("删除成功!",text);
        //弹窗确认
        webDriver.findElement(By.cssSelector("body > div.panel.window.panel-htop.messager-window > div.dialog-button.messager-button > a > span")).click();


    }


    //删除按钮失败
    @Order(11)
    @ParameterizedTest
    @CsvSource(value = {"tenjutest,无法删除自己!"})
    void deleteFail(String username,String msg){

        //刷新页面
        webDriver.navigate().refresh();

        //等待列表刷新
        WebDriverWait wait = new WebDriverWait(webDriver,10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#info tr")));


        //找到自己
        webDriver.findElement(By.cssSelector("#ipt_name")).sendKeys(username);
        webDriver.findElement(By.cssSelector("#submit1")).click(); //点击查询按钮

        //等待列表查询
        WebDriverWait waitUpdate = new WebDriverWait(webDriver,10);
        waitUpdate.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#info tr")));

        //点击删除
        List<WebElement> allElements = webDriver.findElements(By.cssSelector("#info tr"));
        int size = allElements.size();
        Assertions.assertEquals(1,size); //只查询出1个
        WebElement element = allElements.get(0);
        element.findElement(By.linkText("删除")).click();

        //等待弹窗出现
        WebDriverWait waitAlert = new WebDriverWait(webDriver, 30);
        waitAlert.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("body > div.panel.window.panel-htop.messager-window")));
        //判断弹窗,提示框文本
        String text = webDriver.findElement(By.cssSelector("body > div.panel.window.panel-htop.messager-window > div.messager-body.panel-body.panel-body-noborder.window-body > div:nth-child(2)")).getText();
        Assertions.assertEquals(msg,text);
        //弹窗确认
        webDriver.findElement(By.cssSelector("body > div.panel.window.panel-htop.messager-window > div.dialog-button.messager-button > a > span")).click();

    }


    //批量删除失败,未选择任何用户
    @Order(12)
    @Test
    void deleteNull(){
        //刷新页面
        webDriver.navigate().refresh();
        //((JavascriptExecutor)webDriver).executeScript("location.reload()");

        //等待列表查询
        WebDriverWait waitUpdate = new WebDriverWait(webDriver,10);
        waitUpdate.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#info tr")));

        //点击批量删除
        webDriver.findElement(By.linkText("批量删除")).click();

        //等待弹窗出现
        WebDriverWait waitDelete = new WebDriverWait(webDriver, 30);
        waitDelete.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("body > div.panel.window.panel-htop.messager-window")));
        //判断弹窗,提示框文本
        String textDelete = webDriver.findElement(By.cssSelector("body > div.panel.window.panel-htop.messager-window > div.messager-body.panel-body.panel-body-noborder.window-body > div:nth-child(2)")).getText();
        Assertions.assertEquals("您未选择所要删除的用户!",textDelete);
        //弹窗确认
        webDriver.findElement(By.cssSelector("body > div.panel.window.panel-htop.messager-window > div.dialog-button.messager-button > a > span")).click();

    }

    //批量删除失败,删除自己
    @Order(13)
    @ParameterizedTest
    @CsvSource(value = {"tenjutest,无法删除自己!"})
    void deletesFail(String username,String msg){
        //找到自己
        webDriver.findElement(By.cssSelector("#ipt_name")).sendKeys(username);
        webDriver.findElement(By.cssSelector("#submit1")).click(); //点击查询按钮

        //等待列表查询
        WebDriverWait waitUpdate = new WebDriverWait(webDriver,10);
        waitUpdate.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#info tr")));

        //获取自己,选取复选框
        List<WebElement> allElements = webDriver.findElements(By.cssSelector("tr"));
        int size = allElements.size();
        WebElement element = allElements.get(size-1);
        element.findElement(By.cssSelector("input")).click();

        //点击批量删除
        webDriver.findElement(By.linkText("批量删除")).click();

        //等待弹窗出现
        WebDriverWait waitDelete = new WebDriverWait(webDriver, 30);
        waitDelete.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("body > div.panel.window.panel-htop.messager-window")));
        //判断弹窗,提示框文本
        String textDelete = webDriver.findElement(By.cssSelector("body > div.panel.window.panel-htop.messager-window > div.messager-body.panel-body.panel-body-noborder.window-body > div:nth-child(2)")).getText();
        Boolean flag = textDelete.contains("您确定要删除下列用户吗？");
        Assertions.assertEquals(true,flag);
        //弹窗确认
        webDriver.findElement(By.cssSelector("body > div.panel.window.panel-htop.messager-window > div.dialog-button.messager-button > a > span")).click();


        //弹窗出现
        WebDriverWait waitAlert = new WebDriverWait(webDriver, 30);
        waitAlert.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("body > div.panel.window.panel-htop.messager-window")));
        //判断弹窗,提示框文本
        String text = webDriver.findElement(By.cssSelector("body > div.panel.window.panel-htop.messager-window > div.messager-body.panel-body.panel-body-noborder.window-body > div:nth-child(2)")).getText();
        Assertions.assertEquals("无法删除自己!",text);
        //弹窗确认
        webDriver.findElement(By.cssSelector("body > div.panel.window.panel-htop.messager-window > div.dialog-button.messager-button > a > span")).click();

    }


    //注销
    @Order(14)
    @Test
    void logout(){
        //点击注销
        webDriver.findElement(By.linkText("注销")).click();

        //等待弹窗出现
        WebDriverWait waitDelete = new WebDriverWait(webDriver, 30);
        waitDelete.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("body > div.panel.window.panel-htop.messager-window")));
        //判断弹窗,提示框文本
        String textDelete = webDriver.findElement(By.cssSelector("body > div.panel.window.panel-htop.messager-window > div.messager-body.panel-body.panel-body-noborder.window-body > div:nth-child(2)")).getText();
        Assertions.assertEquals("您确定要注销吗？",textDelete);
        //弹窗确认
        webDriver.findElement(By.cssSelector("body > div.panel.window.panel-htop.messager-window > div.dialog-button.messager-button > a > span")).click();

        //页面跳转,等待验证码可以被点击
        WebDriverWait wait = new WebDriverWait(webDriver,10);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#captcha")));

        //判断url,是否跳转到登录页面
        String cur_url = webDriver.getCurrentUrl();
        Assertions.assertEquals(WebVariable.LOGIN,cur_url);
    }


    //未登录
    @Order(15)
    @Test
    void unLogin(){
        webDriver.get(WebVariable.LIST); //未登录到达列表页

        webDriver.navigate().refresh();

        //页面跳转,等待验证码可以被点击
        WebDriverWait wait = new WebDriverWait(webDriver,20);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#captcha")));

        String cur_url = webDriver.getCurrentUrl();
        Assertions.assertEquals(WebVariable.LOGIN,cur_url);
    }

}
