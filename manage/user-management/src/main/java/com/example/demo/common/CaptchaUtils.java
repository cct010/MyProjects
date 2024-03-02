package com.example.demo.common;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.core.lang.Console;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

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
    public static String createCaptch(String name) {
        //定义图形验证码的长和宽
       // LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100,4,50);
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 80,5,50);
        while(containsKey(lineCaptcha.getCode())){
            lineCaptcha = CaptchaUtil.createLineCaptcha(200, 80,5,50);
        }

        File path = new File(ResourceUtils.getURL("classpath:").getPath());
        if(!path.exists()) {
            path = new File("");
        }
        File upload = new File(path.getAbsolutePath(),"static/captcha/");
        if(!upload.exists()) {
            upload.mkdirs();
        }
        String parent= upload.getPath();
        File dir = new File(parent);
        if (!dir.exists()) {
            dir.mkdirs();
        }
//        String filename = "line.png";
//        File dest = new File(dir, filename);
        String filename = name+".png";
        File dest = new File(dir, filename);
        lineCaptcha.write(dest);
        Console.log(lineCaptcha.getCode());//获取验证码内容
        return lineCaptcha.getCode();
    }

    //判断验证码是否正确,忽略大小写
    public static Boolean verify(String expected, String actual) {
        if( expected==null || actual==null || expected.equals("") || actual.equals("") )return false;
        boolean reslut = expected.equalsIgnoreCase(actual);
        return reslut;

    }

    //判断验证码里面是否有太难辨认的字母数字
    private static Boolean containsKey(String key){
        //防止 有一些字母太相似
        // I->l 总是像l,字母大写O 0 -> O/O0,l -> l/I,o -> o/0,数字0
        char[] value = {'I','0','o','O','l','C'};
        for (char c : value) {
            String tmp = String.valueOf(c);
            if (key.contains(tmp)) {
                return true;
            }
        }
        return false;
    }

}
