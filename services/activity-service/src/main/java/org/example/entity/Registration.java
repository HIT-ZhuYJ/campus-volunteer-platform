package org.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("vol_registration")
public class Registration {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long activityId;
    
    private LocalDateTime registrationTime;
    
    private Integer checkInStatus;
    
    private LocalDateTime checkInTime;
    
    private Integer hoursConfirmed;
    
    private LocalDateTime confirmTime;
    
    private String status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
