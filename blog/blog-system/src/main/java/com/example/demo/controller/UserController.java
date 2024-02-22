package com.example.demo.controller;

import com.example.demo.common.*;
import com.example.demo.config.AccessLimit;
import com.example.demo.config.DedupLimit;
import com.example.demo.entity.Userinfo;
import com.example.demo.entity.vo.UserinfoVO;
import com.example.demo.service.ArticleService;
import com.example.demo.service.UserService;
// import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.Duration;
import java.util.Iterator;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2023-12-07
 * Time: 21:35
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;


    @Autowired
    private ArticleService articleService;

    @Resource
    private RedisUtils redisUtils;

    //注册,1h内只允许用户注册一次
    @AccessLimit(maxCount = 500,seconds = 5) //接口访问限流
    @DedupLimit(expireTime = 5000) //去除重复请求
    @PostMapping("/reg")
    public AjaxResult reg(HttpServletRequest request,Userinfo userinfo,String captchatext,String uuid){
        //进行非空校验,长度校验,有效性校验
        if (userinfo == null || !StringUtils.hasLength(userinfo.getUsername()) ||
                !StringUtils.hasLength(userinfo.getPassword()) || !StringUtils.hasLength(captchatext) || !StringUtils.hasLength(uuid)){
            return AjaxResult.fail(-1,"参数非法!");
        }
        //判断用户1h是否重复注册
        String ipUser =  request.getRemoteAddr(); //用户ip
        String method = request.getRequestURI(); //获取web项目的相对路径
        String redisKey = "reg:U=" + ipUser + "M=" + method;
        if(redisUtils.get(redisKey)!=null){
            return AjaxResult.fail(-1,"重复注册!"); //重复注册
        }
        //判断验证码
        if(redisUtils.get(uuid)==null){
            return AjaxResult.fail(-1,"验证码过期!");
        }
        String getCode = redisUtils.get(uuid); //获取redis上存储的验证码
        redisUtils.del(uuid); //获取里面的验证码后,删除此key,防止3分钟内重复使用该验证码
//        if(captchatext.length()!=5 || !getCode.equals(captchatext)){
//            return AjaxResult.fail(-1,"验证码错误!");
//        }
        if(captchatext.length()!=5 || !getCode.equals(captchatext)){
            if(!userinfo.getUsername().equals("tenjutest")){ //测试账号tenjutest,验证码12345
                return AjaxResult.fail(-1,"验证码错误!");
            }else {
                if(!captchatext.equals("12345")){
                    return AjaxResult.fail(-1,"验证码错误!");
                }
            }
        }
        //用户名长度判断,密码长度判断
        int usernameLength = userinfo.getUsername().length();
        int passwordLentth = userinfo.getPassword().length();
        if(usernameLength>16 || usernameLength<6 || passwordLentth>16 || passwordLentth<6){
            return AjaxResult.fail(-1,"用户名或密码长度不符要求!");
        }
        //判断用户名是否已存在
        if(userService.getUserByName(userinfo.getUsername())!=null){
            return AjaxResult.fail(-1,"'"+userinfo.getUsername()+"'用户名已存在!");
        }

        //给密码加盐
        userinfo.setPassword(PasswordUtils.encrypt(userinfo.getPassword()));
        int result = userService.reg(userinfo); //注册用户
        if(result==1){
            //查询数据库
            Userinfo user = userService.getUserByName(userinfo.getUsername());
            //注册成功,生成session
            HttpSession session = request.getSession();//没有,就创建
            session.setAttribute(AppVariable.USER_SESSION_KEY,user);
            //存储注册信息到redis,1h之内不允许重复注册
            redisUtils.set(redisKey,"",Duration.ofHours(1));
        }
        //用户的头像也还没设置,~~~
        //windows设置
        //userinfo.setPhoto("./image/00.png");

        return AjaxResult.success(result);


    }

    //登陆之前获取验证码
    @AccessLimit(maxCount = 500,seconds = 5) //接口访问限流
    @PostMapping("/getcaptcha")
    public AjaxResult getCaptcha(){
        String captchapath = "/blog/upload/line.png";
        String code = CaptchaUtils.createCaptch(); //生成验证码图片,获取验证码文本
        String codeuuid = UUID.randomUUID().toString().replace("-",""); // 32位
        //存储到redis,设置过期时间3分钟
        redisUtils.set(codeuuid,code,Duration.ofMinutes(3));
        //返回图片地址
        captchapath = captchapath+"?res="+codeuuid;
        return AjaxResult.success(captchapath);
    }

    //登陆
    @AccessLimit(maxCount = 500,seconds = 5) //接口访问限流
    @DedupLimit(expireTime = 5000) //去除重复请求,5秒之内
    @PostMapping("/login")
    //@Cacheable(value = "userinfo.date",key = "#userinfo")
    public AjaxResult login(HttpServletRequest request, Userinfo userinfo,String captchatext,String uuid){
        //参数校验,非空判断,用户名字,用户密码,用户验证码
        if(!StringUtils.hasLength(userinfo.getUsername()) || !StringUtils.hasLength(userinfo.getPassword()) ||
          !StringUtils.hasLength(captchatext) || !StringUtils.hasLength(uuid)){
            return AjaxResult.fail(-1,"参数非法!");
        }
        //判断用户名或密码长度
        int usernameLength = userinfo.getUsername().length();
        int passwordLength = userinfo.getPassword().length();
        if(usernameLength<6||usernameLength>16||passwordLength<6||passwordLength>16){
            return AjaxResult.fail(-1,"用户名或密码错误!");
        }
        //判断验证码
        if(redisUtils.get(uuid)==null){
            return AjaxResult.fail(-1,"验证码过期!");
        }
        String getCode = redisUtils.get(uuid); //获取redis上存储的验证码
        redisUtils.del(uuid); //获取里面的验证码后,删除此key,防止3分钟内重复使用该验证码
        if(captchatext.length()!=5 || !getCode.equals(captchatext)){
            if(!userinfo.getUsername().equals("tenjutest")){ //测试账号tenjutest,验证码12345
                return AjaxResult.fail(-1,"验证码错误!");
            }else {
                if(!captchatext.equals("12345")){
                    return AjaxResult.fail(-1,"验证码错误!");
                }
            }
        }
        //判断用户此次登录次数,1小时内超过6次直接返回
        //"access:U=" + userId + "M=" + method;
        String userId = request.getRemoteAddr(); //用户ip
        String loignFalse = "loginFalse:U=" +userId+"N="+ userinfo.getUsername();
        int count =0;
        if(redisUtils.get(userinfo.getUsername())!=null){
            //count = Integer.parseInt(redisUtils.get(userinfo.getUsername()));
            count = Integer.parseInt(redisUtils.get(loignFalse));
            if(count>=6){
                // long time = redisUtils.getExpireSeconds(userinfo.getUsername());//得到的是时间,是秒数
                // System.out.println(time);
                //String timeout = redisUtils.getExpire(userinfo.getUsername());
                String timeout = redisUtils.getExpire(loignFalse);
                System.out.println(timeout);
                //用户登录次数超过6
                return AjaxResult.fail(-1,"登录操作频繁! "+ timeout +" 时间后重试!");
            }
        }

        //判断有没有多地登陆
        // del spring:session:sessions:8d043ae0-350a-4968-9ab0-e424aa44f981删除它session就不存在了

        //查询数据库
        Userinfo user = userService.getUserByName(userinfo.getUsername());
        if(user!=null && user.getId()>0){
            //用户存在
            //再判断密码是否正确
            if(PasswordUtils.check(userinfo.getPassword(),user.getPassword())){
                //登录成功
                //存储session
                HttpSession session = request.getSession();//没有,就创建
                session.setAttribute(AppVariable.USER_SESSION_KEY,user);
                user.setPassword("");//返回之前,将密码隐藏

                //判断之前有无存储session到redis
                String keyuser = user.getUsername() +":session";
                if(redisUtils.get(keyuser)!=null && !redisUtils.get(keyuser).equals(session.getId())){
                    String sessionid = redisUtils.get(keyuser);
                    //移除session
                    String delsession = "spring:session:sessions:" + sessionid;
                    redisUtils.del(delsession);
                }
                //将session存储到redis上
                redisUtils.setSession(keyuser, session.getId());

                //如果之前有记录用户登录次数
                //移除redis记录的用户登录次数
                redisUtils.del(loignFalse);
                return AjaxResult.success(user);
            }

        }

        //用户不存在或用户存在但密码错误,redis记录次数增加+1
        count = count+1;
        redisUtils.set(loignFalse, String.valueOf(count), Duration.ofHours(1));//过期时间1h

        return AjaxResult.fail(-1,"用户名或密码错误!");
    }

    //获取用户文章数量
    @AccessLimit(maxCount = 500,seconds = 5) //接口访问限流
    @PostMapping("showData")
    public AjaxResult showData(HttpServletRequest request){
        UserinfoVO userinfoVO = new UserinfoVO();
        //得到当前登录用户username,photo,从session中获得
        Userinfo userinfo = UserSessionUtils.getUser(request);
        if(userinfo == null){
            return AjaxResult.fail(-1,"请求非法!");
        }
        //深克隆
        BeanUtils.copyProperties(userinfo,userinfoVO);//userinfo克隆给userinfoVO
        //得到用户发表文章总数
        userinfoVO.setArtTotal(articleService.getArtCountByUid(userinfo.getId()));
        return AjaxResult.success(userinfoVO);
    }

    //注销
    @AccessLimit(maxCount = 500,seconds = 5) //接口访问限流
    @DedupLimit(expireTime = 5000) //去除重复请求,5秒之内
    @PostMapping("/logout")
    public AjaxResult logout(HttpSession session){
        session.removeAttribute(AppVariable.USER_SESSION_KEY);
        return AjaxResult.success(1);
    }

    //根据id查询用户
    @AccessLimit(maxCount = 500,seconds = 5) //接口访问限流
    @PostMapping("/getuserbyid")
    public AjaxResult getUserById(Integer id){
        if(id==null || id<=0){
            return AjaxResult.fail(-1,"参数非法!");
        }
        Userinfo userinfo = userService.getUserById(id);
        if(userinfo==null || userinfo.getId()<=0){
            return AjaxResult.fail(-1,"参数非法!");
        }
        //去除密码
        userinfo.setPassword("");
        UserinfoVO userinfoVO = new UserinfoVO();
        BeanUtils.copyProperties(userinfo,userinfoVO);
        //查询当前作者的文章数
        userinfoVO.setArtTotal(articleService.getArtCountByUid(id));
        return AjaxResult.success(userinfoVO);
    }

    //修改用户头像,linux
    @AccessLimit(maxCount = 500,seconds = 5) //接口访问限流
    @DedupLimit(expireTime = 5000) //去除重复请求,5秒之内
    @PostMapping("/uploadimg")
    @SneakyThrows
    public AjaxResult uploadimg(@RequestPart("")MultipartFile file ,HttpServletRequest request){
        //非空判断
        if(file==null || file.getOriginalFilename()==null || file.getContentType()==null){
            return AjaxResult.fail(-1,"参数非法!");
        }
        //判断类型
        if(!file.getContentType().contains("image/")){
            return AjaxResult.fail(-1,"参数非法!");
        }

        //判断文件大小,不允许超过2m
        long size = file.getSize(); //单位b(字节)
        double sizeKb = size/1024.0; //单位kb
        if(sizeKb>(1024*2)){
            return AjaxResult.fail(-1,"文件超过2MB!");
        }else if(sizeKb<=0){
            return AjaxResult.fail(-1,"文件为空!");
        }

        // 获取⽂件后缀名
        String fileName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        //判断上传的是否是图片
        if(!(fileName.equalsIgnoreCase(".PNG") || fileName.equalsIgnoreCase(".JPG")
                || fileName.equalsIgnoreCase(".JPEG")|| fileName.equalsIgnoreCase(".bmp")
                || fileName.equalsIgnoreCase(".GIF"))){
            return AjaxResult.fail(-1,"文件非法!");
        }

        //判断图片内容
        BufferedImage read = ImageIO.read(file.getInputStream());
        if(read == null){
            return AjaxResult.fail(-1,"图片非法!"); //虽然后缀名是图片标识,但内容不是图片
        }

        //判断用户是否登录
        Userinfo userinfo = UserSessionUtils.getUser(request);
        if(userinfo==null || userinfo.getId()==null){
            return AjaxResult.fail(-1,"用户未登录!");
        }

        String name = UUID.randomUUID() + fileName;
        String filename = name.replace("-","");

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
        File dest = new File(dir, filename);
        System.out.println(dest);
        file.transferTo(dest); //保存图片
        // 路径
        String avatar ="/blog"+ "/upload/" +filename;


        //删除原来的图片
        if(!userinfo.getPhoto().equals("./image/00.png")){
            //如果图片路径不是默认头像的路径,就删除原来用户图片
            File oldimg = new File(dir,userinfo.getPhoto().split("upload/")[1]);
            oldimg.delete();
        }



        //加入数据库
        userinfo.setPhoto(avatar);
        userService.updatePhoto(userinfo);

        //更新session里的userinfo
        UserSessionUtils.reflushUser(request,userinfo);



        return AjaxResult.success(avatar);

    }


//    //修改用户头像,windows
//    @RequestMapping("/uploadimg")
//    @SneakyThrows
//    public AjaxResult uploadimg1(@RequestPart("")MultipartFile file ,HttpServletRequest request){
//        //非空判断
//        if(file==null || file.getOriginalFilename()==null || file.getContentType()==null){
//            return AjaxResult.fail(-1,"参数非法");
//        }
//        //判断类型
//        if(!file.getContentType().contains("image/")){
//            return AjaxResult.fail(-1,"参数非法");
//        }
//        // 获取⽂件后缀名
//        String fileName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
//        //判断上传的是否是图片
//        if(!(fileName.equalsIgnoreCase(".PNG") || fileName.equalsIgnoreCase(".JPG")
//                || fileName.equalsIgnoreCase(".JPEG")|| fileName.equalsIgnoreCase(".bmp")
//                || fileName.equalsIgnoreCase(".GIF"))){
//            return AjaxResult.fail(-1,"文件非法");
//        }
//        //判断用户是否登录
//        Userinfo userinfo = UserSessionUtils.getUser(request);
//        if(userinfo==null || userinfo.getId()==null){
//            return AjaxResult.fail(-1,"用户未登录");
//        }
//        // ⽂件保存地址
//        //String filePath = ClassUtils.getDefaultClassLoader().getResource("static").getPath() + "/" + UUID.randomUUID() + fileName;
//        String name = UUID.randomUUID() + fileName;
//        name = name.replace("-","");
//        //获取文件存放位置的绝对路径
//        ApplicationHome applicationHome = new ApplicationHome(this.getClass());
//        //保存到src/main/resources/static目录下
//        String filePath = applicationHome.getDir().getParentFile().getParentFile().getAbsolutePath() + "/src/main/resources/static/upload/";
//        String filePathName =  filePath + name;
//        //保存到taget目录下的static,String filePath1 = ClassUtils.getDefaultClassLoader().getResource("static").getPath() + "/upload/" + name ;
//        String filePathTarget = applicationHome.getDir().getParentFile().getParentFile().getAbsolutePath() + "/target/classes/static/upload/";
//        String filePathTargetName = filePathTarget + name;
//
//
//        //删除原来的图片
//        File oldimg = new File(filePathTarget+userinfo.getPhoto().split("upload/")[1]);
//        System.out.println("----");
//        System.out.println(oldimg);
//        oldimg.delete();
//
//
//        //加入数据库
//        String path = "./upload/" + name;
//        userinfo.setPhoto(path);
//        userService.updatePhoto(userinfo);
//
//        //更新session里的userinfo
//        UserSessionUtils.reflushUser(request,userinfo);
//
//        // 保存⽂件,file.transferTo(new File(filePath));//transferTo转换一次,file就不存在了
//        file.transferTo(new File(filePathTargetName));//写文件,到target目录下
//        Files.copy(Paths.get(filePathTargetName), Paths.get(filePathName));//复制,taget目录下的文件,复制到src目录下,这样重启项目,图片不会都丢失
//        return AjaxResult.success(path);
//    }

}
