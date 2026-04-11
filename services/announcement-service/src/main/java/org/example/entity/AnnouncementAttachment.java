package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("vol_announcement_attachment")
public class AnnouncementAttachment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long announcementId;

    private String objectKey;

    private String originalName;

    private String contentType;

    private Long fileSize;

    private Integer sortOrder;

    private LocalDateTime createTime;
}
