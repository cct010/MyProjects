package com.example.demo.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2023-12-07
 * Time: 21:37
 */
@Data
public class Userinfo implements Serializable {
    private final long serializableId = 1L;//表示可以持久化的
    private Integer id;
    private String username;
    private String password;
    private String photo;
    private LocalDateTime createtime;
    private LocalDateTime updatetime;
    private Integer state; //Integer可以接收null,不会报错
}
