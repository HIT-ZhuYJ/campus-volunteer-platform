package org.example.dto;

import lombok.Data;

import java.util.List;

@Data
public class FeedbackCreateRequest {

    private String title;

    private String category;

    private String content;

    private List<FeedbackAttachmentRequest> attachments;
}
