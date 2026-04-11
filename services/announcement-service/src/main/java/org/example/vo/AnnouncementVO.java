package org.example.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AnnouncementVO implements Serializable {

    private Long id;

    private String title;

    private String content;

    private String imageKey;

    private String imageUrl;

    private List<String> imageKeys;

    private List<String> imageUrls;

    private Long activityId;

    private List<Long> activityIds;

    private List<AnnouncementActivityVO> activities;

    private List<AnnouncementAttachmentVO> attachments;

    private String status;

    private Integer sortOrder;

    private Long publisherId;

    private LocalDateTime publishTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
