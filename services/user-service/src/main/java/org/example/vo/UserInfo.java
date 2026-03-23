package org.example.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class UserInfo implements Serializable {
    
    private Long id;
    
    private String username;
    
    private String realName;
    
    private String studentNo;
    
    private String phone;
    
    private String email;
    
    private String role;
    
    private BigDecimal totalVolunteerHours;
}
