package com.example.demo.common;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-03-25
 * Time: 0:20
 */
//去重工具类
@Component
public class ReqDeupUtils {
    @Autowired
    private ObjectMapper objectMapper; //属性注入

    public String dedupParam(HttpServletRequest request) throws IOException, ServletException {
        //获取request参数,需要排序,不然位置不同,生成的md5也不一样
        String requestType = request.getContentType(); //判断请求类型
        Map<String, String[]> parameterMap ;
        StringBuilder parameter = new StringBuilder("");
        //String parameter = "";
        if(requestType.contains("application/json")){
            //获取json格式,获取请求的body
            parameterMap = objectMapper.readValue(request.getInputStream(), Map.class);
        }else if(requestType.contains("multipart/form-data")){
            //上传文件的,特别处理一下
            Part part = request.getPart("file"); //只能获取一个文件
            if(part!=null){
                StringBuilder partKey = new StringBuilder(part.getContentType() + part.getSubmittedFileName());
                parameter = parameter.append(partKey);
            }
            parameterMap = request.getParameterMap();
        }else{
            parameterMap = request.getParameterMap();
        }

        if(parameterMap!=null && !parameterMap.isEmpty()){
            List<Map.Entry<String,String[]>> lstEntry=new ArrayList<>(parameterMap.entrySet());
            lstEntry.sort(((o1, o2) -> {
                return o1.getKey().compareTo(o2.getKey());
            }));
            //String parameter = JSONUtil.toJsonStr(parameterMap);
            parameter = parameter.append(JSONUtil.toJsonStr(lstEntry));
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("url", String.valueOf(request.getRequestURL()));
        map.put("parameter", String.valueOf(parameter));

        ObjectMapper objectMapper = new ObjectMapper();
        String paramTreeMapJSON = objectMapper.writeValueAsString(map);
        String md5deDupParam = DigestUtils.md5DigestAsHex(paramTreeMapJSON.getBytes());  //进行MD5

//        System.out.println("--------");
//        System.out.println(request.getRequestURL());
//        System.out.println(parameter);
//        System.out.println(paramTreeMapJSON);
//        System.out.println(md5deDupParam);
        return md5deDupParam;
    }

}
