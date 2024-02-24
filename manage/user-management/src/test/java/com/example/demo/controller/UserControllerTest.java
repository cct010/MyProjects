package com.example.demo.controller;


import com.example.demo.entity.Userinfo;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-01-17
 * Time: 1:01
 */
@SpringBootTest
@Transactional
//@RunWith(SpringRunner.class)
//@WebMvcTest(UserController.class)
class UserControllerTest {
//
////    @Autowired
////    private UserService userService;
////
//    @Autowired
//    private UserMapper userMapper;
//
////    @Autowired
////    private MockMvc mockMvc; //模拟http请求
//
//    @Test
//    @Transactional
//    void delUser() throws Exception {
////        Integer uid = 2;
////        Integer id = 1;
////        System.out.println(userMapper.delUser(id,uid));
//
////        String t = "安徽,北京,重庆,福建,甘肃,广东,贵州,海南,河北,河南,黑龙江,湖北,湖南,吉林,江苏,江西,辽宁,内蒙古自治区,宁夏回族自治区,青海,山东,山西,陕西,上海,四川,天津,西藏自治区,新疆维吾尔自治区,云南,浙江";
////        String[] strings = t.split(",");
////        System.out.println(Arrays.toString(strings));
////        for(int i=0;i<strings.length;i++){
////            String tt =  "<option value='"+strings[i]+"'>"+strings[i]+"</option>";
////            System.out.println(tt);
////        }
//
//
//    }
//
//    @Test
//    void getList() {
//        Integer pindex = 2;
//        Integer psize = 3;
//        Userinfo userinfo = new Userinfo();
//        userinfo.setEmail("com");
//        userinfo.setUsername("j");
//
//        //(当前页码-1)*每页显示条数
//        int offset = (pindex-1)*psize;
//        //文章所有数据
//        List<Userinfo> list = userMapper.getListByPage(psize,offset,userinfo.getUsername(), userinfo.getAddress(), userinfo.getEmail());
//        //计算当前列表总共有多少页
//        //总条数/每页显示条数,向上取整
//        int totalcount = userMapper.getCount(userinfo.getUsername(), userinfo.getAddress(), userinfo.getEmail());
//        double pcountdb = totalcount / (psize*1.0);
//        int pcount =  (int) Math.ceil(pcountdb);
//
//        HashMap<String ,Object> result = new HashMap<>();
//      //  result.put("list",list);
//        result.put("pcount",pcount); //总页数
////        result.put("totalcount",totalcount); //总条数
//
//        System.out.println(result);
//    }
//
//    @Test
//    @Transactional
//    void addUser() {
//        Userinfo userinfo = new Userinfo();
//        userinfo.setUsername("op");
//        userinfo.setLoginname("as");
//        userinfo.setPassword("45");
//        userinfo.setAddress("oooo");
//        System.out.println(userinfo.toString());
//        //int u = userMapper.add(userinfo); //增加
//        Integer u = userMapper.getUserIdByKey("username" ,"mango111");
//        if(u!=null){
//            System.out.println(u);
//        }else{
//            System.out.println("没有此用户");
//        }
//
//    }
//
//    @Test
//    @Transactional
//    void delByIds() {
//        Integer[] ids = {1,3,4,5,6};
//        int result = userMapper.delByIds(ids);
//        System.out.println(result);
//    }
//
//    @Test
//    @Transactional
//    void update() {
//        Userinfo userinfo = new Userinfo();
//        userinfo.setId(56);
//        userinfo.setUsername("op");
//        userinfo.setLoginname("as");
//        userinfo.setPassword("45");
//        userinfo.setAddress("oooo");
//        userinfo.setEmail("123");
//        userinfo.setQq("ff");
//        userinfo.setAge(34);
//        userinfo.setUpdatetime(LocalDateTime.now());
//        System.out.println(userinfo.toString());
//        //int u = userMapper.add(userinfo); //增加
//        Integer u = userMapper.getUserIdByKey("username" ,"mango");
//        if(u!=null){
//            System.out.println(u);
//        }else{
//            System.out.println("没有此用户");
//        }
//        int s = userMapper.update(userinfo);
//        System.out.println(s);
//        System.out.println(userMapper.getUserById(56).toString());
//    }
}