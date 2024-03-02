package com.example.demo.controller;

import com.example.demo.common.*;
import com.example.demo.config.AccessLimit;
import com.example.demo.config.DedupLimit;
import com.example.demo.entity.Userinfo;
import com.example.demo.service.UserService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-01-15
 * Time: 18:00
 */
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Resource
    private DataSourceTransactionManager dataSourceTransactionManager; //jdbc事务管理器

    @Resource
    private TransactionDefinition transactionDefinition; //定义事务属性

    @Resource
    private RedisUtils redisUtils;

    //登陆之前获取验证码
    @AccessLimit(maxCount = 500,seconds = 5) //接口访问限流
    @PostMapping("/getcaptcha")
    public AjaxResult getCaptcha(){

        String codeuuid = UUID.randomUUID().toString().replace("-",""); // 32位
        String code = CaptchaUtils.createCaptch(codeuuid); //生成验证码图片,获取验证码文本
        String captchapath = "/manage/captcha/" + codeuuid + ".png"; //生成的图片路径
        //存储到redis,设置过期时间3分钟
        redisUtils.set(codeuuid,code, Duration.ofMinutes(3));
        //返回图片地址
        captchapath = captchapath+"?res="+codeuuid;
        return AjaxResult.success(captchapath);
    }

    //登陆
    @AccessLimit(maxCount = 500,seconds = 5) //接口访问限流
    @DedupLimit(expireTime = 5000) //去除重复请求,5秒之内
    @PostMapping("/login")
    public AjaxResult login(HttpServletRequest request, Userinfo userinfo, String captchatext, String uuid) throws IOException {

        //参数校验,非空判断,用户名字,用户密码,用户验证码
        if(!StringUtils.hasLength(userinfo.getLoginname()) || !StringUtils.hasLength(userinfo.getPassword()) ||
                !StringUtils.hasLength(captchatext) || !StringUtils.hasLength(uuid)){
            return AjaxResult.fail(-1,"参数非法!");
        }
        String ipUser = request.getRemoteAddr(); //用户ip

        //从redis上根据uuid获取code,并删除redis上的uuid
        String getCode = redisUtils.deleteKey(uuid); //防止3分钟内重复使用该key
        //判断验证码,返回-1表示没有找到
        if(getCode.equals("-1")){
            return AjaxResult.fail(-1,"验证码过期!");
        }
        //验证码判断忽略大小写
        if(captchatext.length()!=5 || !CaptchaUtils.verify(getCode,captchatext)){
            if(!userinfo.getLoginname().equals("tenjutest")){ //测试账号tenjutest,验证码12345
                return AjaxResult.fail(-1,"验证码错误!");
            }else {
                if(!captchatext.equals("12345")){
                    return AjaxResult.fail(-1,"验证码错误!");
                }
            }
        }
        //判断用户此次登录次数,1小时内超过6次直接返回
        int count =0;
        if(redisUtils.get(userinfo.getLoginname())!=null){
            count = Integer.parseInt(redisUtils.get(userinfo.getLoginname()));
            if(count>=6){
                String timeout = redisUtils.getExpire(userinfo.getLoginname());
                System.out.println(timeout);
                //用户登录次数超过6
                return AjaxResult.fail(-1,"登录操作频繁! "+ timeout +" 时间后重试!");
            }
        }

        String loginname = userinfo.getLoginname();
        //查询数据库
        Userinfo user = userService.gerUserByLoginname(loginname);

        if(user!=null && user.getId()>0){
            //用户存在
            //再判断密码是否正确
            if(PasswordUtils.check(userinfo.getPassword(),user.getPassword())){
                //登录成功
                //存储session
                HttpSession session = request.getSession();//没有,就创建
                session.setAttribute(AppVariable.USER_SESSION_KEY,user);
                user.setPassword("");//返回之前,将密码隐藏

                //如果之前有记录用户登录次数
                //移除redis记录的用户登录次数
                redisUtils.del(userinfo.getLoginname());
                return AjaxResult.success(user);
            }
        }

        //用户不存在或用户存在但密码错误,redis记录次数增加+1
        count = count+1;
        redisUtils.set(userinfo.getLoginname(), String.valueOf(count), Duration.ofHours(1));//过期时间1h

        return AjaxResult.fail(-1,"用户名或密码错误!");
    }



    //获取列表
    @AccessLimit(maxCount = 500,seconds = 5) //接口访问限流
    @PostMapping("/list")
    public AjaxResult getList(Integer psize,Integer pindex,Userinfo userinfo){
        if(pindex==null || pindex<=1){
            pindex=1;
        }
        if(psize==null || psize<1){
            psize=5;
        }
        System.out.println(userinfo.toString());

        String email = userinfo.getEmail();
        String username = userinfo.getUsername();
        String address = userinfo.getAddress();
        //判断关键字搜索是否都是空格或换行符
        if(!StringUtils.hasLength(userinfo.getEmail()) || ArticleUtils.isEmpty(email)){
            email = null;
        }else{
            email = email.replaceAll("\\s*|t|r|n","");
        }

        if(!StringUtils.hasLength(userinfo.getUsername()) || ArticleUtils.isEmpty(username)){
            username = null;
        }else{
            username = username.replaceAll("\\s*|t|r|n","");
        }

        if(!StringUtils.hasLength(userinfo.getAddress()) || ArticleUtils.isEmpty(address)){
            address = null;
        }else{
            address = address.replaceAll("\\s*|t|r|n","");
        }

        //(当前页码-1)*每页显示条数
        int offset = (pindex-1)*psize;
        //文章所有数据
        List<Userinfo> list = userService.getListByPage(psize,offset,username,address,email);
        //计算当前列表总共有多少页
        //总条数/每页显示条数,向上取整
        int totalcount = userService.getCount(username,address,email);
        double pcountdb = totalcount / (psize*1.0);
        int pcount =  (int) Math.ceil(pcountdb);

        HashMap<String ,Object> result = new HashMap<>();
        result.put("list",list); //数据
        result.put("pcount",pcount); //总页数
        result.put("totalcount",totalcount); //总条数

        return AjaxResult.success(result);
        //return AjaxResult.success(userService.getList());
    }

    //删除一个用户
    @AccessLimit(maxCount = 500,seconds = 5) //接口访问限流
    @DedupLimit(expireTime = 5000) //去除重复请求,5秒之内
    @PostMapping("/del")
    public AjaxResult delUser(HttpServletRequest request,Integer id){
        if(id==null || id<=0){
            return AjaxResult.fail(-1,"参数非法!");
        }
        Userinfo userinfo = UserSessionUtils.getUser(request);
        if(userinfo==null || userinfo.getId()<=0){
            return AjaxResult.fail(-1,"用户未登录!");
        }
        Integer uid = userinfo.getId();
        if(Objects.equals(uid, id)){
            return AjaxResult.fail(-1,"无法删除自己!");
        }
        //开启事务
        //TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
        int result = userService.delUser(id,uid);
        //回滚事务
       // dataSourceTransactionManager.rollback(transactionStatus);
        return AjaxResult.success(result);
    }

    //批量删除,@RequestParam(value = "ids[]")
    @AccessLimit(maxCount = 500,seconds = 5) //接口访问限流
    @DedupLimit(expireTime = 5000) //去除重复请求,5秒之内
    @PostMapping("/delbyids")
    public AjaxResult delByIds(HttpServletRequest request,@RequestParam(value = "ids[]")Integer[] ids){
        if(ids==null || !StringUtils.hasLength(Arrays.toString(ids))){
            return AjaxResult.fail(-1,"参数非法!");
        }
        Userinfo userinfo = UserSessionUtils.getUser(request);
        if(userinfo==null || userinfo.getId()<=0){
            return AjaxResult.fail(-1,"用户未登录!");
        }
        Integer uid = userinfo.getId(); //当前登陆者的id
        for(int num : ids){
            if(num == uid){
                return AjaxResult.fail(-1,"无法删除自己!");
            }
        }
        return AjaxResult.success(userService.delByIds(ids));

    }



    //获取一个用户
    @AccessLimit(maxCount = 500,seconds = 5) //接口访问限流
    @PostMapping("/getuser")
    public AjaxResult getUserById(Integer id){
        if(id==null || id<=0){
            return AjaxResult.fail(-1,"参数非法");
        }
        return AjaxResult.success(userService.getUserById(id));
    }

    //新增用户
    @AccessLimit(maxCount = 500,seconds = 5) //接口访问限流
    @DedupLimit(expireTime = 5000) //去除重复请求,5秒之内
    @PostMapping("/add")
    public AjaxResult addUser(Userinfo userinfo){
        if (userinfo==null)return AjaxResult.fail(-1,"不是一个用户!");
        System.out.println(userinfo.toString());
        if(!StringUtils.hasLength(userinfo.getLoginname()) || !StringUtils.hasLength(userinfo.getUsername())
        || !StringUtils.hasLength(userinfo.getPassword())){
            return AjaxResult.fail(-1,"参数非法!");
        }
        //判断用户名,登录名是否已经存在
        int result = userService.getUserByKey("username", userinfo.getUsername());
        if(result>=1){
            return AjaxResult.fail(-1,"此姓名已存在!");
        }
        result = userService.getUserByKey("loginname", userinfo.getLoginname());
        if(result>=1){
            return AjaxResult.fail(-1,"此登录名已存在!");
        }
        //添加用户
        //给密码加盐
        userinfo.setPassword(PasswordUtils.encrypt(userinfo.getPassword()));
        System.out.println(userinfo.toString());

        return AjaxResult.success(userService.add(userinfo));
    }

    //修改用户
    @AccessLimit(maxCount = 500,seconds = 5) //接口访问限流
    @DedupLimit(expireTime = 5000) //去除重复请求,5秒之内
    @PostMapping("/update")
    public AjaxResult update(Userinfo userinfo){
        //判断参数是否为空
        if (userinfo==null)return AjaxResult.fail(-1,"不是一个用户!");

        System.out.println(userinfo.toString());
        //用户名,登录名,id是否为空
        if(!StringUtils.hasLength(userinfo.getLoginname())  ||
                !StringUtils.hasLength(userinfo.getUsername())|| userinfo.getId()<=0){
            return AjaxResult.fail(-1,"参数非法!");
        }
        //判断用户名,登录名是否已经存在
        Integer result = userService.getUserIdByKey("username", userinfo.getUsername());
        if(result!=null && !result.equals(userinfo.getId())){
            return AjaxResult.fail(-1,"此姓名已存在!");
        }
        result = userService.getUserIdByKey("loginname", userinfo.getLoginname());
        if(result!=null && !result.equals(userinfo.getId())){
            return AjaxResult.fail(-1,"此登录名已存在!");
        }
        //如果密码存在,给密码加盐
        if(StringUtils.hasLength(userinfo.getPassword())){
            userinfo.setPassword(PasswordUtils.encrypt(userinfo.getPassword()));
        }
        userinfo.setUpdatetime(LocalDateTime.now()); //修改更新时间

        return AjaxResult.success(userService.update(userinfo));

    }


    //注销
    @AccessLimit(maxCount = 500,seconds = 5) //接口访问限流
    @DedupLimit(expireTime = 5000) //去除重复请求,5秒之内
    @PostMapping("/logout")
    public AjaxResult logout(HttpSession session){
        session.removeAttribute(AppVariable.USER_SESSION_KEY);
        return AjaxResult.success(1);
    }

//    //修改用户头像,linux
//    @AccessLimit(maxCount = 500,seconds = 5) //接口访问限流
//    @DedupLimit(expireTime = 5000) //去除重复请求,5秒之内
//    @PostMapping("/uploadimg")
//    @SneakyThrows
//    public AjaxResult uploadimg(@RequestPart("file") MultipartFile file , HttpServletRequest request){
//        //判断类型
//        System.out.println(file.getContentType());
//        if(!file.getContentType().contains("image/")){
//            return AjaxResult.fail(-1,"参数非法!");
//        }
//
//        //判断文件大小,不允许超过2m
//        long size = file.getSize(); //单位b
//        double size1 = size/1024.0; //单位kb
//        if(size1>(1024*2) || size1<=0){
//            return AjaxResult.fail(-1,"文件超过2MB!");
//        }
//        return AjaxResult.success(1);
//    }

}
