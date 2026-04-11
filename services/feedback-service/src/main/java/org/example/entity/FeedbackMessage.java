package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("feedback_message")
public class FeedbackMessage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long feedbackId;

    private Long senderId;

    private String senderRole;

    private String content;

    private String messageType;

    private LocalDateTime createTime;
}
