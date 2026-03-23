package org.example.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AIGenerateRequest implements Serializable {
    
    private String location;
    
    private String category;
    
    private String keywords;
}
