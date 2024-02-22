package com.example.demo.common;

import org.junit.jupiter.api.Assertions;
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
 * Date: 2024-02-18
 * Time: 21:41
 */
public class Article extends InitAndEnd{
    public static void artFail1(String title,String content,String msg,String element){
        //保存/发布按钮可以被点击
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#submit")));

        //填写标题
        webDriver.findElement(By.cssSelector("#title")).sendKeys(title);
        //填写正文
        if (!content.equals("")){
            webDriver.findElement(By.cssSelector("#editor > div.editormd-toolbar > div > ul > li:nth-child(21) > a > i")).click();
        }
        //webDriver.findElement(By.cssSelector("body > div.container > div.container-right > div.title > button.mysave")).click();
        webDriver.findElement(By.cssSelector(element)).click();
        //隐式等待,等待弹窗的出现
        WebDriverWait waitAlert = new WebDriverWait(webDriver, 10);
        waitAlert.until(ExpectedConditions.alertIsPresent());
        String text = webDriver.switchTo().alert().getText();
        Assertions.assertEquals(msg,text);
        webDriver.switchTo().alert().accept();
    }

    public static void artFail(String title,String content,String msg,String element){
        //保存/发布按钮可以被点击
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#submit")));
        //填写标题
        webDriver.findElement(By.cssSelector("#title")).sendKeys(title);
        //填写正文
        //点击修改正文
        //获取文本editor.getValue(),清空文本editor.setValue("")
        String editContent = "editor.setValue('"+content+"')";
        ((JavascriptExecutor)webDriver).executeScript(editContent);
        //点击修改按钮
        webDriver.findElement(By.cssSelector(element)).click();//点击修改按钮

        //等待弹窗出现
        WebDriverWait waitAlert = new WebDriverWait(webDriver,10);
        waitAlert.until(ExpectedConditions.alertIsPresent());

        String alertText = webDriver.switchTo().alert().getText();//判断弹窗提示内容
        Assertions.assertEquals(msg,alertText);
        webDriver.switchTo().alert().accept(); //关闭弹窗
    }


    public static void artSuccess(String title,String content,String msg){
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#submit")));//等待发布按钮可以被点击

        //填写标题
        webDriver.findElement(By.cssSelector("#title")).sendKeys(title);
        //填写正文
        webDriver.findElement(By.cssSelector("#editor > div.editormd-toolbar > div > ul > li:nth-child(21) > a > i")).click();

        webDriver.findElement(By.cssSelector("#submit")).click();//点击发布

        //防止获取到原来的默认数字
        WebDriverWait waitArtTotal = new WebDriverWait(webDriver,10);
        //waitArtTotal.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#ifDiv")));
        waitArtTotal.until(ExpectedConditions.not(ExpectedConditions.textToBe(By.cssSelector("#artTotal"),"2")));

        //获取文章数量
        int oldnumber = Integer.parseInt(webDriver.findElement(By.cssSelector("#artTotal")).getText());

        //隐式等待,等待弹窗的出现
        WebDriverWait waitAlert = new WebDriverWait(webDriver, 10);
        waitAlert.until(ExpectedConditions.alertIsPresent());
        String text = webDriver.switchTo().alert().getText(); //判断弹窗提示信息
        Assertions.assertEquals(msg,text);
        webDriver.switchTo().alert().dismiss();//点击取消

        //判断保存
        //webDriver.get("http://62.234.10.69:8080/blog-center.html");//到个人中心页
        WebDriverWait waitDraft = new WebDriverWait(webDriver, 10);
        waitDraft.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#ifDiv"))); //等待frame可以被点击

        //判断文章数量
        int newnumber = Integer.parseInt(webDriver.findElement(By.cssSelector("#artTotal")).getText());
        Assertions.assertEquals(newnumber,oldnumber+1); //发布文章,文章数量+1

        webDriver.switchTo().frame(webDriver.findElement(By.cssSelector("#ifDiv")));//切换到子页面

        //等待,标题可以被点击
        WebDriverWait waitTitle = new WebDriverWait(webDriver, 10);
        waitTitle.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#artList > div:nth-child(1) > a")));

        //判断第一篇,标题
        String cur_title = webDriver.findElement(By.cssSelector("#artList > div:nth-child(1) > a")).getText(); //查找第一篇文章
        Assertions.assertEquals(title,cur_title);

    }

    public static int toArtDetail(){
        webDriver.get(WebVariable.BLOG_CENTER); //个人中心页
        WebDriverWait waitArticle = new WebDriverWait(webDriver,10);
        waitArticle.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#ifDiv"))); //等待子页面可以被点击

        webDriver.switchTo().frame(webDriver.findElement(By.cssSelector("#ifDiv"))); //到子页面

        WebDriverWait waitDraftList = new WebDriverWait(webDriver,10);//等待获取文章列表
        waitDraftList.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".articleList")));
        List<WebElement> webElements = webDriver.findElements(By.cssSelector(".articleList"));
        WebElement webElement = webElements.get(0);
        webElement.findElement(By.linkText("浏览")).click();//点击浏览按钮

        //跳转到详情页
        WebDriverWait waitTitle = new WebDriverWait(webDriver,10);
        waitTitle.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#title")));
        WebDriverWait waitArtTotal = new WebDriverWait(webDriver,5);
        waitArtTotal.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#ifDiv")));
        //记录原来阅读量
        int rcount = Integer.parseInt(webDriver.findElement(By.cssSelector("#rcount")).getText());
        System.out.println(rcount);
        return rcount;
    }
}
