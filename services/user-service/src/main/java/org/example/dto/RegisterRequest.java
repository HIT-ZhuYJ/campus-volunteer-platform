package org.example.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RegisterRequest implements Serializable {
    
    private String username;
    
    private String password;
    
    private String realName;
    
    private String studentNo;
    
    private String phone;
    
    private String email;
}
