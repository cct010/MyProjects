package com.example.demo.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-01-15
 * Time: 17:54
 */
@Data
public class Userinfo implements Serializable {
    private static final long serialVersionUID = 8413358021046803164L ;//4542684839012057215L; //序列化id //8413358021046803164
    private Integer id;
    private String loginname;
    private String username;
    private String password;
    private String sex;
    private Integer age;
    private String address;
    private String qq;
    private String email;
    private boolean isadmin;
    private int state;
    private LocalDateTime createtime;
    private LocalDateTime updatetime;

}