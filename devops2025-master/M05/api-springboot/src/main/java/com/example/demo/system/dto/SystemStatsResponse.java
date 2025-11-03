package com.example.demo.system.dto;

/**
 * 系统统计信息响应DTO
 */
public class SystemStatsResponse {
    private Long totalUsers;
    private Long totalRoles;
    private Long totalPermissions;
    private Long activeUsers;
    private Long systemRoles;
    private Long systemPermissions;

    public SystemStatsResponse() {}

    public SystemStatsResponse(Long totalUsers, Long totalRoles, Long totalPermissions, 
                              Long activeUsers, Long systemRoles, Long systemPermissions) {
        this.totalUsers = totalUsers;
        this.totalRoles = totalRoles;
        this.totalPermissions = totalPermissions;
        this.activeUsers = activeUsers;
        this.systemRoles = systemRoles;
        this.systemPermissions = systemPermissions;
    }

    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Long getTotalRoles() {
        return totalRoles;
    }

    public void setTotalRoles(Long totalRoles) {
        this.totalRoles = totalRoles;
    }

    public Long getTotalPermissions() {
        return totalPermissions;
    }

    public void setTotalPermissions(Long totalPermissions) {
        this.totalPermissions = totalPermissions;
    }

    public Long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(Long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public Long getSystemRoles() {
        return systemRoles;
    }

    public void setSystemRoles(Long systemRoles) {
        this.systemRoles = systemRoles;
    }

    public Long getSystemPermissions() {
        return systemPermissions;
    }

    public void setSystemPermissions(Long systemPermissions) {
        this.systemPermissions = systemPermissions;
    }
}