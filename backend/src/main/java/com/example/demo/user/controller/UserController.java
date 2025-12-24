package com.example.demo.user.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.user.entity.User;
import com.example.demo.user.service.UserService;
import com.example.demo.security.JwtUtil;
import com.example.demo.user.dto.UpdateProfileRequest;
import com.example.demo.user.dto.ChangePasswordRequest;
import com.example.demo.user.dto.UserProfileResponse;
import com.example.demo.common.BusinessException;
import com.example.demo.role.mapper.RoleMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.List;

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

    @Autowired
    private RoleMapper roleMapper;

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

            // 从数据库查询用户角色
            List<String> roles = roleMapper.findRoleCodesByUserId(user.getId());
            if (roles != null && !roles.isEmpty()) {
                // 使用第一个角色作为主角色
                userProfile.setRole(roles.get(0));
                // 如果有ADMIN角色，设置为admin
                if (roles.contains("ADMIN")) {
                    userProfile.setRole("admin");
                    userProfile.setDepartment("技术部");
                    userProfile.setPosition("系统管理员");
                } else {
                    userProfile.setDepartment("业务部");
                    userProfile.setPosition("普通用户");
                }
            } else {
                userProfile.setRole("user");
                userProfile.setDepartment("业务部");
                userProfile.setPosition("普通用户");
            }

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
}