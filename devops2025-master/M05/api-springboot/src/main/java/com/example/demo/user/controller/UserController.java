package com.example.demo.user.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.user.entity.User;
import com.example.demo.user.service.UserService;
import com.example.demo.security.JwtUtil;
import com.example.demo.user.dto.UpdateProfileRequest;
import com.example.demo.user.dto.ChangePasswordRequest;
import com.example.demo.common.BusinessException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    public ApiResponse getUserProfile(HttpServletRequest request) {
        try {
            // 从请求头获取token
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ApiResponse.fail(401, "未提供有效的认证token");
            }

            String token = authHeader.substring(7);
            
            // 验证token并获取用户名
            if (!jwtUtil.validateToken(token)) {
                return ApiResponse.fail(401, "token无效或已过期");
            }

            String username = jwtUtil.getUsernameFromToken(token);
            
            // 根据用户名查找用户
            User user = userService.findByUsername(username);
            if (user == null) {
                return ApiResponse.fail(404, "用户不存在");
            }

            // 构建用户信息响应（不包含敏感信息）
            UserProfileResponse userProfile = new UserProfileResponse();
            userProfile.setId(user.getId());
            userProfile.setUsername(user.getUsername());
            userProfile.setEmail(user.getEmail());
            userProfile.setPhone(user.getPhone());
            userProfile.setNickname(user.getNickname());
            userProfile.setAvatarUrl(user.getAvatarUrl());
            userProfile.setBio(user.getBio());
            userProfile.setStatus(user.getStatus());
            userProfile.setEmailVerified(user.getEmailVerified() == 1);
            userProfile.setPhoneVerified(user.getPhoneVerified() == 1);
            userProfile.setLastLoginAt(user.getLastLoginAt());
            userProfile.setCreatedAt(user.getCreatedAt());

            return ApiResponse.ok(userProfile);

        } catch (Exception e) {
            return ApiResponse.fail(500, "获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/profile")
    public ApiResponse<User> updateUserProfile(@Valid @RequestBody UpdateProfileRequest request, 
                                             HttpServletRequest httpRequest) {
        // 从请求头中获取token
        String token = getTokenFromRequest(httpRequest);
        if (token == null) {
            return ApiResponse.fail(401, "未提供认证token");
        }

        // 从token中获取用户ID
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return ApiResponse.fail(401, "无效的token");
        }

        try {
            // 更新用户信息
            User updatedUser = userService.updateProfile(userId, 
                request.getNickname(), 
                request.getBio(), 
                request.getAvatarUrl());

            // 清除敏感信息
            updatedUser.setPasswordHash(null);

            return ApiResponse.ok(updatedUser);
        } catch (Exception e) {
            return ApiResponse.fail(500, "更新用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public ApiResponse<String> changePassword(@Valid @RequestBody ChangePasswordRequest request, 
                                            HttpServletRequest httpRequest) {
        try {
            // 验证新密码和确认密码是否一致
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                return ApiResponse.fail(400, "新密码和确认密码不一致");
            }

            // 从token中获取用户ID
            String token = getTokenFromRequest(httpRequest);
            Long userId = jwtUtil.getUserIdFromToken(token);

            // 修改密码
            userService.changePassword(userId, request.getCurrentPassword(), request.getNewPassword());

            return ApiResponse.ok("密码修改成功");
        } catch (BusinessException e) {
            return ApiResponse.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            return ApiResponse.fail(500, "修改密码失败: " + e.getMessage());
        }
    }

    /**
     * 从请求头中获取token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 用户信息响应类
     */
    public static class UserProfileResponse {
        private Long id;
        private String username;
        private String email;
        private String phone;
        private String nickname;
        private String avatarUrl;
        private String bio;
        private Integer status;
        private Boolean emailVerified;
        private Boolean phoneVerified;
        private java.time.LocalDateTime lastLoginAt;
        private java.time.LocalDateTime createdAt;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }

        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }

        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }

        public Boolean getEmailVerified() { return emailVerified; }
        public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }

        public Boolean getPhoneVerified() { return phoneVerified; }
        public void setPhoneVerified(Boolean phoneVerified) { this.phoneVerified = phoneVerified; }

        public java.time.LocalDateTime getLastLoginAt() { return lastLoginAt; }
        public void setLastLoginAt(java.time.LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }

        public java.time.LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
}