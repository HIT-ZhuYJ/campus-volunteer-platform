package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("feedback")
public class Feedback {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String title;

    private String category;

    private String status;

    private String priority;

    private LocalDateTime lastMessageTime;

    private String lastReplierRole;

    private Long closedBy;

    private LocalDateTime closedTime;

    private String rejectReason;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
