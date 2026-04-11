package org.example.dto;

import lombok.Data;

@Data
public class AnnouncementAttachmentRequest {

    private String attachmentKey;

    private String fileName;

    private String contentType;

    private Long fileSize;
}
