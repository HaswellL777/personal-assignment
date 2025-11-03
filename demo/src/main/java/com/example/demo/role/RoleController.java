package com.example.demo.role;

import com.example.demo.common.ApiResponse;
import com.example.demo.permission.entity.Permission;
import com.example.demo.role.entity.Role;
import com.example.demo.role.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 获取所有角色
     */
    @GetMapping
    public ApiResponse<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ApiResponse.ok(roles);
    }

    /**
     * 根据ID获取角色
     */
    @GetMapping("/{id}")
    public ApiResponse<Role> getRoleById(@PathVariable Long id) {
        Role role = roleService.getRoleById(id);
        return ApiResponse.ok(role);
    }

    /**
     * 创建角色
     */
    @PostMapping
    public ApiResponse<Role> createRole(@RequestBody Role role) {
        Role createdRole = roleService.createRole(role);
        return ApiResponse.ok(createdRole);
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    public ApiResponse<Role> updateRole(@PathVariable Long id, @RequestBody Role role) {
        role.setId(id);
        Role updatedRole = roleService.updateRole(role);
        return ApiResponse.ok(updatedRole);
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ApiResponse.ok();
    }

    /**
     * 获取角色的权限列表
     */
    @GetMapping("/{id}/permissions")
    public ApiResponse<List<Permission>> getRolePermissions(@PathVariable Long id) {
        List<Permission> permissions = roleService.getRolePermissions(id);
        return ApiResponse.ok(permissions);
    }

    /**
     * 为角色分配权限
     */
    @PostMapping("/{id}/permissions")
    public ApiResponse<Void> assignPermissions(@PathVariable Long id, @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(id, permissionIds);
        return ApiResponse.ok();
    }

    /**
     * 为角色添加单个权限
     */
    @PostMapping("/{roleId}/permissions/{permissionId}")
    public ApiResponse<Void> addPermissionToRole(@PathVariable Long roleId, @PathVariable Long permissionId) {
        roleService.addPermissionToRole(roleId, permissionId);
        return ApiResponse.ok();
    }

    /**
     * 从角色中移除权限
     */
    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    public ApiResponse<Void> removePermissionFromRole(@PathVariable Long roleId, @PathVariable Long permissionId) {
        roleService.removePermissionFromRole(roleId, permissionId);
        return ApiResponse.ok();
    }
}