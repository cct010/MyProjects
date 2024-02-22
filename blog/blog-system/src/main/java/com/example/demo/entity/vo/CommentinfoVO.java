package com.example.demo.entity.vo;

import com.example.demo.entity.Commentinfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2023-12-11
 * Time: 15:55
 */
@Data
public class CommentinfoVO extends Commentinfo {
    private String username;
    private String replyname;
    private String userphoto;
    private String articlename;
    private String articleauthor;
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDateTime showtime;
    private List<CommentinfoVO> replies = new ArrayList<>();

    public void setReplies(CommentinfoVO replies) {
        this.replies.add(replies);
    }




}
