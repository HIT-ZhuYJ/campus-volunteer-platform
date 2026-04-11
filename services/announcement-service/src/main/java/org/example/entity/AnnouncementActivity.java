package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("vol_announcement_activity")
public class AnnouncementActivity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long announcementId;

    private Long activityId;

    private LocalDateTime createTime;
}
