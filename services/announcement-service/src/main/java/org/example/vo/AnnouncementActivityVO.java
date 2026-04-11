package org.example.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AnnouncementActivityVO implements Serializable {

    private Long id;

    private String title;

    private String location;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String status;

    private String category;
}
