package org.example.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RegistrationVO implements Serializable {
    
    private Long id;
    
    private Long activityId;
    
    private String activityTitle;
    
    private String location;
    
    private BigDecimal volunteerHours;
    
    private LocalDateTime startTime;
    
    private LocalDateTime registrationTime;
    
    private Integer checkInStatus;
    
    private Integer hoursConfirmed;
    
    private String status;
}
