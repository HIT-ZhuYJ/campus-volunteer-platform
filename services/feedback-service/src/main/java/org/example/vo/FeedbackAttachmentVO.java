package org.example.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class FeedbackAttachmentVO implements Serializable {

    private Long id;

    private String attachmentKey;

    private String fileName;

    private String contentType;

    private Long fileSize;

    private String fileType;

    private String url;

    private LocalDateTime createTime;
}
