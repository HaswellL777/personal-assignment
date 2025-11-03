package com.example.demo.permission.service;

import com.example.demo.common.BusinessException;
import com.example.demo.common.ErrorCode;
import com.example.demo.permission.entity.Permission;
import com.example.demo.permission.mapper.PermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限服务类
 */
@Service
public class PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    /**
     * 获取所有权限
     */
    public List<Permission> getAllPermissions() {
        return permissionMapper.findAll();
    }

    /**
     * 根据ID获取权限
     */
    public Permission getPermissionById(Long id) {
        Permission permission = permissionMapper.findById(id);
        if (permission == null) {
            throw new BusinessException(40004, "权限不存在");
        }
        return permission;
    }

    /**
     * 根据模块获取权限
     */
    public List<Permission> getPermissionsByModule(String module) {
        return permissionMapper.findByModule(module);
    }

    /**
     * 获取所有模块
     */
    public List<String> getAllModules() {
        return permissionMapper.findAllModules();
    }

    /**
     * 创建权限
     */
    public Permission createPermission(Permission permission) {
        // 验证权限代码唯一性
        if (permissionMapper.countByCode(permission.getCode()) > 0) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "权限代码已存在");
        }

        // 设置创建时间
        permission.setCreatedAt(LocalDateTime.now());
        permission.setUpdatedAt(LocalDateTime.now());
        
        // 默认值设置
        if (permission.getStatus() == null) {
            permission.setStatus(1);
        }
        if (permission.getIsSystem() == null) {
            permission.setIsSystem(false);
        }

        permissionMapper.insert(permission);
        return permission;
    }

    /**
     * 更新权限
     */
    public Permission updatePermission(Long id, Permission permission) {
        Permission existingPermission = getPermissionById(id);
        
        // 验证权限代码唯一性（排除自己）
        if (permissionMapper.countByCodeExcludeId(permission.getCode(), id) > 0) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "权限代码已存在");
        }

        // 系统权限不允许修改某些字段
        if (existingPermission.getIsSystem() && 
            (!permission.getCode().equals(existingPermission.getCode()) || 
             !permission.getModule().equals(existingPermission.getModule()))) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "系统权限不允许修改代码和模块");
        }

        permission.setId(id);
        permission.setUpdatedAt(LocalDateTime.now());
        permission.setCreatedAt(existingPermission.getCreatedAt());
        permission.setIsSystem(existingPermission.getIsSystem());

        permissionMapper.update(permission);
        return permission;
    }

    /**
     * 删除权限
     */
    public void deletePermission(Long id) {
        Permission permission = getPermissionById(id);
        
        // 系统权限不允许删除
        if (permission.getIsSystem()) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "系统权限不允许删除");
        }

        permissionMapper.deleteById(id);
    }

    /**
     * 根据角色ID获取权限
     */
    public List<Permission> getPermissionsByRoleId(Long roleId) {
        return permissionMapper.findByRoleId(roleId);
    }
}