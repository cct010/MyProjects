package com.example.demo.common;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-03-03
 * Time: 0:31
 */
//定时删除验证码图片
@Component
@EnableScheduling //开启定时任务支持
@EnableAsync //开启对异步的支持
public class ImageScanner {
    private static final String FORMAT_TYPE = "yyyy-MM-dd HH:mm:ss";

    @Scheduled(cron = "0 0 1 * * ?")  //每天凌晨1点执行一次："0 0 1 * * ?"
    //@Scheduled(cron = "*/5 * * * * ?") //每隔5秒执行一次 //cron = "0 0 */1 * * ?",1小时执行一次,"0 */30 * * * ?",30分钟执行一次
    public void scanAndDeleteImages() throws IOException {
        //获取当前时间
        DateTimeFormatter format = DateTimeFormatter.ofPattern(FORMAT_TYPE);//指定格式
        LocalDateTime time = LocalDateTime.now();//获取当前时间
        LocalDateTime timeBefore = time.minusMinutes(10);//获取当前时间,并减去10分钟

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

        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                // 判断文件是否为图片格式,验证码
                if (file.isFile() && file.getName().contains(".png") ) {
                    // System.out.println("Found image: " + file.getName());
                    String timeStr = getFileLastModifiedTime(file); //获取时间
                    LocalDateTime localDate = LocalDateTime.parse(timeStr, format);//把String时间转换成LocalDateTime
                    if( timeBefore.isAfter(localDate)){
                        System.out.println("Found image: " + file.getName());
//                        System.out.println(timeBefore);
//                        System.out.println(timeStr);
                        //删除图片,验证码图片明显过期了
                        file.delete();
                    }

                }
            }
        }

    }


    // private static final String mformatType = "yyyy/MM/dd HH:mm:ss";

    //获取文件创建时间
    public static String getFileLastModifiedTime(File file) {
        Calendar cal = Calendar.getInstance();
        long time = file.lastModified();
        SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_TYPE);
        cal.setTimeInMillis(time);
        return formatter.format(cal.getTime());
    }
}
