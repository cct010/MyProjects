package com.example.demo.common;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.generator.RandomGenerator;
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
   // private static final String path1 = "./upload/line.png";

    // char[] value = {'I','0','o','O','l','C'}; // I->l 总是像l,字母大写O 0 -> O/O0,l -> l/I,o -> o/0,数字0
    //public static final String BASE_CHAR_NUMBER = "abcdefghijklmnopqrstuvwxyz".toUpperCase() + "abcdefghijklmnopqrstuvwxyz0123456789";
    public static final String BASE_CHAR_NUMBER = "abcdefghjklmnpqrstuvwxyz".toUpperCase() + "abcdefghijkmnpqrstuvwxyz123456789";


    @SneakyThrows
    public static String createCaptch(String name) {
        //自定义内容的验证码
        RandomGenerator randomGenerator = new RandomGenerator(BASE_CHAR_NUMBER, 5);
        //定义图形验证码的长和宽
        // LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 80,5,50);
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 80);
        lineCaptcha.setGenerator(randomGenerator);
        // 重新生成code
        lineCaptcha.createCode();

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
        String filename = name+".png";
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


        //return path1;
        //return avatar;
        return lineCaptcha.getCode();
    }

    //判断验证码是否正确,忽略大小写
    public static Boolean verify(String expected, String actual) {
        if( expected==null || actual==null || expected.equals("") || actual.equals("") )return false;
        boolean reslut = expected.equalsIgnoreCase(actual);
        return reslut;

    }



}
