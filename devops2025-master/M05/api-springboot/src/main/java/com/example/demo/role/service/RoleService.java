package com.example.demo.role.service;

import com.example.demo.common.BusinessException;
import com.example.demo.permission.entity.Permission;
import com.example.demo.permission.service.PermissionService;
import com.example.demo.role.entity.Role;
import com.example.demo.role.entity.RolePermission;
import com.example.demo.role.mapper.RoleMapper;
import com.example.demo.role.mapper.RolePermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 角色服务类
 */
@Service
public class RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private PermissionService permissionService;

    /**
     * 获取所有角色
     */
    public List<Role> getAllRoles() {
        return roleMapper.findAll();
    }

    /**
     * 根据ID获取角色
     */
    public Role getRoleById(Long id) {
        Role role = roleMapper.findById(id);
        if (role == null) {
            throw new BusinessException(40004, "角色不存在");
        }
        return role;
    }

    /**
     * 创建角色
     */
    public Role createRole(Role role) {
        // 检查角色名称是否已存在
        if (roleMapper.findByName(role.getName()) != null) {
            throw new BusinessException(40003, "角色名称已存在");
        }

        // 设置默认值
        if (role.getCode() == null || role.getCode().trim().isEmpty()) {
            role.setCode(role.getName().toUpperCase().replace(" ", "_"));
        }
        role.setStatus(1); // 启用状态
        role.setIsSystem(false);
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());

        roleMapper.insert(role);
        return role;
    }

    /**
     * 更新角色
     */
    public Role updateRole(Role role) {
        Role existingRole = getRoleById(role.getId());
        
        // 检查角色名称是否被其他角色使用
        Role roleWithSameName = roleMapper.findByName(role.getName());
        if (roleWithSameName != null && !roleWithSameName.getId().equals(role.getId())) {
            throw new BusinessException(40003, "角色名称已存在");
        }

        // 更新字段
        existingRole.setName(role.getName());
        existingRole.setDescription(role.getDescription());
        existingRole.setUpdatedAt(LocalDateTime.now());

        roleMapper.update(existingRole);
        return existingRole;
    }

    /**
     * 删除角色
     */
    public void deleteRole(Long id) {
        Role role = getRoleById(id);
        
        // 检查是否为系统角色
        if (Boolean.TRUE.equals(role.getIsSystem())) {
            throw new BusinessException(40005, "系统角色不能删除");
        }

        roleMapper.deleteById(id);
    }

    /**
     * 获取角色的权限列表
     */
    public List<Permission> getRolePermissions(Long roleId) {
        // 验证角色是否存在
        getRoleById(roleId);
        return permissionService.getPermissionsByRoleId(roleId);
    }

    /**
     * 为角色分配权限
     */
    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        // 验证角色是否存在
        getRoleById(roleId);

        // 验证所有权限是否存在
        for (Long permissionId : permissionIds) {
            permissionService.getPermissionById(permissionId);
        }

        // 删除角色现有的所有权限
        rolePermissionMapper.deleteByRoleId(roleId);

        // 添加新的权限关联
        if (!permissionIds.isEmpty()) {
            List<RolePermission> rolePermissions = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            for (Long permissionId : permissionIds) {
                RolePermission rolePermission = new RolePermission(roleId, permissionId);
                rolePermission.setCreatedAt(now);
                rolePermissions.add(rolePermission);
            }
            rolePermissionMapper.batchInsert(rolePermissions);
        }
    }

    /**
     * 为角色添加权限
     */
    public void addPermissionToRole(Long roleId, Long permissionId) {
        // 验证角色和权限是否存在
        getRoleById(roleId);
        permissionService.getPermissionById(permissionId);

        // 检查关联是否已存在
        if (rolePermissionMapper.countByRoleIdAndPermissionId(roleId, permissionId) > 0) {
            throw new BusinessException(40003, "角色已拥有该权限");
        }

        // 添加权限关联
        RolePermission rolePermission = new RolePermission(roleId, permissionId);
        rolePermissionMapper.insert(rolePermission);
    }

    /**
     * 从角色中移除权限
     */
    public void removePermissionFromRole(Long roleId, Long permissionId) {
        // 验证角色和权限是否存在
        getRoleById(roleId);
        permissionService.getPermissionById(permissionId);

        // 删除权限关联
        int deleted = rolePermissionMapper.deleteByRoleIdAndPermissionId(roleId, permissionId);
        if (deleted == 0) {
            throw new BusinessException(40004, "角色权限关联不存在");
        }
    }
}