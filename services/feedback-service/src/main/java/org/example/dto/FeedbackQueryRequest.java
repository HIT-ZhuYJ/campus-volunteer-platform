package org.example.dto;

import lombok.Data;

@Data
public class FeedbackQueryRequest {

    private Integer page;

    private Integer size;

    private String status;

    private String category;

    private String priority;

    private String keyword;
}
