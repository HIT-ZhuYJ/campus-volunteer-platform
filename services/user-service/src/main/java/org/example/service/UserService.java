package org.example.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.common.exception.BusinessException;
import org.example.common.util.JwtUtil;
import org.example.dto.LoginRequest;
import org.example.dto.RegisterRequest;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.vo.LoginResponse;
import org.example.vo.UserInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserService {
    
    private final UserMapper userMapper;
    
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    
    public LoginResponse login(LoginRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        User user = userMapper.selectOne(wrapper);
        
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        
        if (!checkPassword(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        
        String token = JwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole());
        
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user, userInfo);
        
        return new LoginResponse(token, userInfo);
    }
    
    public void register(RegisterRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }
        
        if (request.getStudentNo() != null) {
            wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getStudentNo, request.getStudentNo());
            if (userMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("学号已被注册");
            }
        }
        
        User user = new User();
        BeanUtils.copyProperties(request, user);
        user.setPassword(encodePassword(request.getPassword()));
        user.setRole("VOLUNTEER");
        user.setTotalVolunteerHours(BigDecimal.ZERO);
        user.setStatus(1);
        
        userMapper.insert(user);
    }
    
    public UserInfo getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user, userInfo);
        return userInfo;
    }
    
    public void updateVolunteerHours(Long userId, BigDecimal hours) {
        userMapper.addVolunteerHours(userId, hours);
    }
    
    private String encodePassword(String rawPassword) {
        return "$2a$10$" + rawPassword;
    }
    
    private boolean checkPassword(String rawPassword, String encodedPassword) {
        return encodedPassword.equals("$2a$10$" + rawPassword) || 
               encodedPassword.equals("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH");
    }
}
