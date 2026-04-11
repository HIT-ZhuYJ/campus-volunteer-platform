package org.example.dto;

import lombok.Data;

@Data
public class FeedbackAttachmentRequest {

    private String attachmentKey;

    private String fileName;

    private String contentType;

    private Long fileSize;

    private String fileType;
}
