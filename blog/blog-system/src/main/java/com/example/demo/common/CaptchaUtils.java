package com.example.demo.common;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.lang.Console;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.channels.FileChannel;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2023-12-14
 * Time: 16:17
 */
//验证码
@Data
public class CaptchaUtils {
    private static final String path1 = "./upload/line.png";
    @SneakyThrows
    public static String createCaptch() {
        //定义图形验证码的长和宽
        //LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100);
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100);


        File path = new File(ResourceUtils.getURL("classpath:").getPath());
        if(!path.exists()) {
            path = new File("");
        }
        File upload = new File(path.getAbsolutePath(),"static/upload/");
        if(!upload.exists()) {
            upload.mkdirs();
        }
        String parent= upload.getPath();
        File dir = new File(parent);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filename = "line.png";
        File dest = new File(dir, filename);
        //File file = new File(String.valueOf(dest));
        //file.transferTo(dest);
        // 路径
        //String avatar = "/upload/" +"line.png";

        //图形验证码写出，可以写出到文件，也可以写出到流
        //lineCaptcha.write("./static/upload/line.png");//target目录下
        lineCaptcha.write(dest);
        Console.log(lineCaptcha.getCode());//获取验证码内容
        //AppVariable.captcha = lineCaptcha.getCode(); //设置进全局变量

        //存储进redis里,生成uuid

        //return path1;
        //return avatar;
        return lineCaptcha.getCode();
    }

//    //验证验证码对不对
//    public static boolean verify(String inputcode){
//        return AppVariable.captcha.equals(inputcode);
//    }

}
