package org.example.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FeedbackMessageVO implements Serializable {

    private Long id;

    private Long feedbackId;

    private Long senderId;

    private String senderRole;

    private String content;

    private String messageType;

    private LocalDateTime createTime;

    private List<FeedbackAttachmentVO> attachments;
}
