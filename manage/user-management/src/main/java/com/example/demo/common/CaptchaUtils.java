package com.example.demo.common;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.lang.Console;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.util.ResourceUtils;

import java.io.File;


/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-01-15
 * Time: 20:04
 */
@Data
public class CaptchaUtils {
    //开发
//    public static String createCaptch(){
//        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200,100);
//        lineCaptcha.write("./static/upload/line.png");
//        Console.log(lineCaptcha.getCode());
//        return lineCaptcha.getCode();
//    }

    //生产
    @SneakyThrows
    public static String createCaptch() {
        //定义图形验证码的长和宽
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
        lineCaptcha.write(dest);
        Console.log(lineCaptcha.getCode());//获取验证码内容
        return lineCaptcha.getCode();
    }

}
