package org.example.dto;

import lombok.Data;

import java.util.List;

@Data
public class FeedbackMessageRequest {

    private String content;

    private List<FeedbackAttachmentRequest> attachments;
}
