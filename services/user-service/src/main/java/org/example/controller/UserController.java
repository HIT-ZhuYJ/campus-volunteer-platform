package org.example.controller;

import org.example.common.result.Result;
import org.example.dto.LoginRequest;
import org.example.dto.RegisterRequest;
import org.example.service.UserService;
import org.example.vo.LoginResponse;
import org.example.vo.UserInfo;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return Result.success(response);
    }
    
    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterRequest request) {
        userService.register(request);
        return Result.success();
    }
    
    @GetMapping("/info")
    public Result<UserInfo> getUserInfo(@RequestHeader("X-User-Id") Long userId) {
        UserInfo userInfo = userService.getUserInfo(userId);
        return Result.success(userInfo);
    }
}
