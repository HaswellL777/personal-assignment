package com.example.demo.admin;

import com.example.demo.common.ApiResponse;
import com.example.demo.user.entity.User;
import com.example.demo.user.service.UserService;
import com.example.demo.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 管理员用户管理控制器
 * 所有接口需要 ADMIN 角色才能访问
 */
@RestController
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserMapper userMapper;

    /**
     * 获取用户列表
     */
    @GetMapping
    public ApiResponse<List<User>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        try {
            // 简单实现：获取所有用户
            List<User> users = userMapper.findAll();
            
            // 清除敏感信息
            users.forEach(user -> user.setPasswordHash(null));
            
            return ApiResponse.ok(users);
        } catch (Exception e) {
            return ApiResponse.fail(500, "获取用户列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取用户详情
     */
    @GetMapping("/{id}")
    public ApiResponse<User> getUserById(@PathVariable Long id) {
        try {
            User user = userService.findById(id);
            if (user != null) {
                // 清除敏感信息
                user.setPasswordHash(null);
                return ApiResponse.ok(user);
            } else {
                return ApiResponse.fail(404, "用户不存在");
            }
        } catch (Exception e) {
            return ApiResponse.fail(500, "获取用户详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建用户
     */
    @PostMapping
    public ApiResponse<User> createUser(@RequestBody Map<String, Object> userData) {
        try {
            // 简单实现：创建用户
            String username = (String) userData.get("username");
            String email = (String) userData.get("email");
            String password = (String) userData.get("password");
            
            if (username == null || email == null || password == null) {
                return ApiResponse.fail(400, "用户名、邮箱和密码不能为空");
            }
            
            User user = userService.createUser(username, email, password);
            user.setPasswordHash(null); // 清除敏感信息
            
            return ApiResponse.ok(user);
        } catch (Exception e) {
            return ApiResponse.fail(500, "创建用户失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public ApiResponse<User> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> userData) {
        try {
            User user = userService.findById(id);
            if (user == null) {
                return ApiResponse.fail(404, "用户不存在");
            }
            
            // 更新用户信息
            if (userData.containsKey("nickname")) {
                user.setNickname((String) userData.get("nickname"));
            }
            if (userData.containsKey("email")) {
                user.setEmail((String) userData.get("email"));
            }
            if (userData.containsKey("phone")) {
                user.setPhone((String) userData.get("phone"));
            }
            
            User updatedUser = userService.updateUser(user);
            updatedUser.setPasswordHash(null); // 清除敏感信息
            
            return ApiResponse.ok(updatedUser);
        } catch (Exception e) {
            return ApiResponse.fail(500, "更新用户失败: " + e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteUser(@PathVariable Long id) {
        try {
            boolean deleted = userService.deleteUser(id);
            if (deleted) {
                return ApiResponse.ok("用户删除成功");
            } else {
                return ApiResponse.fail(404, "用户不存在");
            }
        } catch (Exception e) {
            return ApiResponse.fail(500, "删除用户失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/{id}/status")
    public ApiResponse<String> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Object> statusData) {
        try {
            Integer status = (Integer) statusData.get("status");
            if (status == null) {
                return ApiResponse.fail(400, "状态值不能为空");
            }
            
            boolean updated = userService.updateUserStatus(id, status);
            if (updated) {
                return ApiResponse.ok("用户状态更新成功");
            } else {
                return ApiResponse.fail(404, "用户不存在");
            }
        } catch (Exception e) {
            return ApiResponse.fail(500, "更新用户状态失败: " + e.getMessage());
        }
    }

    /**
     * 重置用户密码
     */
    @PutMapping("/{id}/password/reset")
    public ApiResponse<String> resetUserPassword(@PathVariable Long id) {
        try {
            String newPassword = userService.resetPassword(id);
            return ApiResponse.ok(newPassword);
        } catch (Exception e) {
            return ApiResponse.fail(500, "重置密码失败: " + e.getMessage());
        }
    }
}