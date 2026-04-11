package org.example.vo;

public record AnnouncementAttachmentUploadVO(
        String attachmentKey,
        String fileName,
        String contentType,
        Long fileSize,
        String url
) {
}
