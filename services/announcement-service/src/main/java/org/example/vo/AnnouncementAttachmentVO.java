package org.example.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AnnouncementAttachmentVO implements Serializable {

    private String attachmentKey;

    private String fileName;

    private String contentType;

    private Long fileSize;

    private String url;
}
