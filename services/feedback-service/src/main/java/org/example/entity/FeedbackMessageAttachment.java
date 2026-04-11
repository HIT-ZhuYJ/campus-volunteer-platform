package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("feedback_message_attachment")
public class FeedbackMessageAttachment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long feedbackId;

    private Long messageId;

    private String objectKey;

    private String originalName;

    private String contentType;

    private Long fileSize;

    private String fileType;

    private LocalDateTime createTime;
}
