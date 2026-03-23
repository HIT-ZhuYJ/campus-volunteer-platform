package org.example.controller;

import org.example.common.result.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/user")
public class InternalUserController {
    
    private final org.example.service.UserService userService;
    
    public InternalUserController(org.example.service.UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping("/updateHours")
    public Result<Void> updateVolunteerHours(@RequestParam Long userId, @RequestParam BigDecimal hours) {
        userService.updateVolunteerHours(userId, hours);
        return Result.success();
    }
}
