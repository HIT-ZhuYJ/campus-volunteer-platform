package org.example.dto;

import lombok.Data;

@Data
public class FeedbackStatusUpdateRequest {

    private String status;

    private String reason;

    private String priority;
}
