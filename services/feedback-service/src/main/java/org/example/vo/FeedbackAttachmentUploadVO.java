package org.example.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackAttachmentUploadVO implements Serializable {

    private String attachmentKey;

    private String fileName;

    private String contentType;

    private Long fileSize;

    private String fileType;

    private String url;
}
