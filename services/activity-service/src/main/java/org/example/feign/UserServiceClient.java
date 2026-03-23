package org.example.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    
    @PostMapping("/user/updateHours")
    void updateVolunteerHours(@RequestParam("userId") Long userId, 
                             @RequestParam("hours") BigDecimal hours);
}
