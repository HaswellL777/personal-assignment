package com.example.demo.admin;

import com.example.demo.common.ApiResponse;
import com.example.demo.security.JwtUtil;
import com.example.demo.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员操作控制器
 * 用于演示权限验证功能
 */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminOperationController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    /**
     * 删除用户（模拟管理员操作，用于权限验证演示）
     * 只有管理员才能执行此操作，普通用户会收到 403 错误
     */
    @PostMapping("/delete-user")
    public ApiResponse<String> deleteUser(@RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
        try {
            // 从请求头获取token
            String authHeader = httpRequest.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ApiResponse.fail(401, "未提供有效的认证token");
            }

            String token = authHeader.substring(7);

            // 验证token并获取用户名
            if (!jwtUtil.validateToken(token)) {
                return ApiResponse.fail(401, "token无效或已过期");
            }

            String username = jwtUtil.getUsernameFromToken(token);

            // 权限验证：只有管理员才能删除用户
            if (!"admin".equals(username)) {
                return ApiResponse.fail(403, "权限不足：您没有执行此操作的权限");
            }

            // 如果是管理员，执行操作（这里只是模拟，不实际删除）
            String userId = (String) request.get("userId");
            return ApiResponse.ok("管理员操作成功：已删除用户 " + userId);

        } catch (Exception e) {
            return ApiResponse.fail(500, "操作失败: " + e.getMessage());
        }
    }
}
