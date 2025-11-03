package com.example.demo.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 角色检查请求DTO
 */
public class RoleCheckRequest {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "角色代码不能为空")
    private String roleCode;

    public RoleCheckRequest() {}

    public RoleCheckRequest(Long userId, String roleCode) {
        this.userId = userId;
        this.roleCode = roleCode;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }
}