package com.example.demo.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 权限检查请求DTO
 */
public class PermissionCheckRequest {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "权限代码不能为空")
    private String permissionCode;

    public PermissionCheckRequest() {}

    public PermissionCheckRequest(Long userId, String permissionCode) {
        this.userId = userId;
        this.permissionCode = permissionCode;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }
}