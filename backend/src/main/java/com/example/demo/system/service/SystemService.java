package com.example.demo.system.service;

import com.example.demo.permission.mapper.PermissionMapper;
import com.example.demo.role.mapper.RoleMapper;
import com.example.demo.system.dto.SystemStatsResponse;
import com.example.demo.user.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统管理服务类
 */
@Service
public class SystemService {

    private static final Logger logger = LoggerFactory.getLogger(SystemService.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    /**
     * 获取系统统计信息
     */
    public SystemStatsResponse getSystemStats() {
        logger.info("获取系统统计信息");

        // 统计用户数量
        Long totalUsers = userMapper.countAll();
        Long activeUsers = userMapper.countByStatus(1);

        // 统计角色数量
        Long totalRoles = roleMapper.countAll();
        Long systemRoles = roleMapper.countByIsSystem(true);

        // 统计权限数量
        Long totalPermissions = permissionMapper.countAll();
        Long systemPermissions = permissionMapper.countByIsSystem(true);

        SystemStatsResponse stats = new SystemStatsResponse(
            totalUsers, totalRoles, totalPermissions,
            activeUsers, systemRoles, systemPermissions
        );

        logger.info("系统统计信息: 用户总数={}, 活跃用户={}, 角色总数={}, 系统角色={}, 权限总数={}, 系统权限={}",
            totalUsers, activeUsers, totalRoles, systemRoles, totalPermissions, systemPermissions);

        return stats;
    }

    /**
     * 执行系统清理
     */
    public void cleanup() {
        logger.info("开始执行系统清理");

        try {
            // 清理过期的会话数据（这里可以根据实际需求实现）
            // 例如：清理过期的JWT token记录、临时文件等
            
            // 清理日志文件（示例）
            cleanupLogs();
            
            // 清理临时数据
            cleanupTempData();
            
            logger.info("系统清理完成");
        } catch (Exception e) {
            logger.error("系统清理失败", e);
            throw new RuntimeException("系统清理失败: " + e.getMessage());
        }
    }

    /**
     * 检查用户是否拥有指定权限
     */
    public boolean checkUserPermission(Long userId, String permissionCode) {
        logger.info("检查用户权限: userId={}, permissionCode={}", userId, permissionCode);

        try {
            // 获取用户的所有权限
            List<String> userPermissions = permissionMapper.findPermissionCodesByUserId(userId);
            
            boolean hasPermission = userPermissions.contains(permissionCode);
            
            logger.info("用户权限检查结果: userId={}, permissionCode={}, hasPermission={}", 
                userId, permissionCode, hasPermission);
            
            return hasPermission;
        } catch (Exception e) {
            logger.error("检查用户权限失败", e);
            return false;
        }
    }

    /**
     * 检查用户是否拥有指定角色
     */
    public boolean checkUserRole(Long userId, String roleCode) {
        logger.info("检查用户角色: userId={}, roleCode={}", userId, roleCode);

        try {
            // 获取用户的所有角色
            List<String> userRoles = roleMapper.findRoleCodesByUserId(userId);
            
            boolean hasRole = userRoles.contains(roleCode);
            
            logger.info("用户角色检查结果: userId={}, roleCode={}, hasRole={}", 
                userId, roleCode, hasRole);
            
            return hasRole;
        } catch (Exception e) {
            logger.error("检查用户角色失败", e);
            return false;
        }
    }

    /**
     * 清理日志文件
     */
    private void cleanupLogs() {
        logger.debug("清理日志文件");
        // 这里可以实现具体的日志清理逻辑
        // 例如：删除超过30天的日志文件
    }

    /**
     * 清理临时数据
     */
    private void cleanupTempData() {
        logger.debug("清理临时数据");
        // 这里可以实现具体的临时数据清理逻辑
        // 例如：清理缓存、临时文件等
    }

    /**
     * 获取用户权限列表
     */
    public List<String> getUserPermissions(Long userId) {
        logger.info("获取用户权限列表: userId={}", userId);

        try {
            List<String> permissions = permissionMapper.findPermissionCodesByUserId(userId);
            logger.info("用户权限列表: userId={}, permissions={}", userId, permissions);
            return permissions;
        } catch (Exception e) {
            logger.error("获取用户权限列表失败", e);
            throw new RuntimeException("获取用户权限列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户角色列表
     */
    public List<String> getUserRoles(Long userId) {
        logger.info("获取用户角色列表: userId={}", userId);

        try {
            List<String> roles = roleMapper.findRoleCodesByUserId(userId);
            logger.info("用户角色列表: userId={}, roles={}", userId, roles);
            return roles;
        } catch (Exception e) {
            logger.error("获取用户角色列表失败", e);
            throw new RuntimeException("获取用户角色列表失败: " + e.getMessage());
        }
    }
}