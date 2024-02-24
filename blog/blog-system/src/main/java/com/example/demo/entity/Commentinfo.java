package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2023-12-11
 * Time: 15:51
 */
@Data
public class Commentinfo implements Serializable {
    private final long serializableId = 1L;
    private Integer id;
    private Integer articleid;
    private Integer uid;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createtime;
    private Integer replyid;
    private Integer parentid; //一级评论的id
}
