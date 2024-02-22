package com.example.demo.entity.vo;

import com.example.demo.entity.Userinfo;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2023-12-08
 * Time: 15:19
 */
//userinfo的扩展类
@Data
public class UserinfoVO extends Userinfo {
    private Integer artTotal; //用户发表的文章总数
}
