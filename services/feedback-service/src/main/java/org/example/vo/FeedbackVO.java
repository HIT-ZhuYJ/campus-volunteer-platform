package org.example.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class FeedbackVO implements Serializable {

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
