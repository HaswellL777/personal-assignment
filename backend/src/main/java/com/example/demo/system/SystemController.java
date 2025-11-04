package com.example.demo.system;

import com.example.demo.common.ApiResponse;
import com.example.demo.system.dto.PermissionCheckRequest;
import com.example.demo.system.dto.RoleCheckRequest;
import com.example.demo.system.dto.SystemStatsResponse;
import com.example.demo.system.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 系统管理控制器
 */
@RestController
@RequestMapping("/api/system")
public class SystemController {

    @Autowired
    private SystemService systemService;

    /**
     * 获取系统统计信息
     */
    @GetMapping("/stats")
    public ApiResponse<SystemStatsResponse> getSystemStats() {
        SystemStatsResponse stats = systemService.getSystemStats();
        return ApiResponse.ok(stats);
    }

    /**
     * 执行系统清理
     */
    @PostMapping("/cleanup")
    public ApiResponse<Void> cleanup() {
        systemService.cleanup();
        return ApiResponse.ok();
    }

    /**
     * 检查用户权限
     */
    @PostMapping("/permissions/check")
    public ApiResponse<Boolean> checkPermission(@Valid @RequestBody PermissionCheckRequest request) {
        boolean hasPermission = systemService.checkUserPermission(request.getUserId(), request.getPermissionCode());
        return ApiResponse.ok(hasPermission);
    }

    /**
     * 检查用户角色
     */
    @PostMapping("/roles/check")
    public ApiResponse<Boolean> checkRole(@Valid @RequestBody RoleCheckRequest request) {
        boolean hasRole = systemService.checkUserRole(request.getUserId(), request.getRoleCode());
        return ApiResponse.ok(hasRole);
    }

    /**
     * 获取用户权限列表
     */
    @GetMapping("/user/{userId}/permissions")
    public ApiResponse<List<String>> getUserPermissions(@PathVariable Long userId) {
        List<String> permissions = systemService.getUserPermissions(userId);
        return ApiResponse.ok(permissions);
    }

    /**
     * 获取用户角色列表
     */
    @GetMapping("/user/{userId}/roles")
    public ApiResponse<List<String>> getUserRoles(@PathVariable Long userId) {
        List<String> roles = systemService.getUserRoles(userId);
        return ApiResponse.ok(roles);
    }
}