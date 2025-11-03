package com.example.demo.permission;

import com.example.demo.common.ApiResponse;
import com.example.demo.permission.dto.CreatePermissionRequest;
import com.example.demo.permission.dto.UpdatePermissionRequest;
import com.example.demo.permission.entity.Permission;
import com.example.demo.permission.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限管理控制器
 */
@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 获取权限列表
     */
    @GetMapping
    public ApiResponse<List<Permission>> getPermissions(@RequestParam(required = false) String module) {
        List<Permission> permissions;
        if (module != null && !module.trim().isEmpty()) {
            permissions = permissionService.getPermissionsByModule(module);
        } else {
            permissions = permissionService.getAllPermissions();
        }
        return ApiResponse.ok(permissions);
    }

    /**
     * 获取权限模块列表
     */
    @GetMapping("/modules")
    public ApiResponse<List<String>> getPermissionModules() {
        List<String> modules = permissionService.getAllModules();
        return ApiResponse.ok(modules);
    }

    /**
     * 获取权限详情
     */
    @GetMapping("/{id}")
    public ApiResponse<Permission> getPermission(@PathVariable Long id) {
        Permission permission = permissionService.getPermissionById(id);
        return ApiResponse.ok(permission);
    }

    /**
     * 创建权限
     */
    @PostMapping
    public ApiResponse<Permission> createPermission(@RequestBody CreatePermissionRequest request) {
        Permission permission = new Permission();
        permission.setName(request.getName());
        permission.setCode(request.getCode());
        permission.setDescription(request.getDescription());
        permission.setModule(request.getModule());
        permission.setResource(request.getResource());
        permission.setAction(request.getAction());
        permission.setStatus(request.getStatus());

        Permission createdPermission = permissionService.createPermission(permission);
        return ApiResponse.ok(createdPermission);
    }

    /**
     * 更新权限
     */
    @PutMapping("/{id}")
    public ApiResponse<Permission> updatePermission(@PathVariable Long id, @RequestBody UpdatePermissionRequest request) {
        Permission permission = new Permission();
        permission.setName(request.getName());
        permission.setCode(request.getCode());
        permission.setDescription(request.getDescription());
        permission.setModule(request.getModule());
        permission.setResource(request.getResource());
        permission.setAction(request.getAction());
        permission.setStatus(request.getStatus());

        Permission updatedPermission = permissionService.updatePermission(id, permission);
        return ApiResponse.ok(updatedPermission);
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return ApiResponse.ok();
    }
}