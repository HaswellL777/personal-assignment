package com.example.demo.role.mapper;

import com.example.demo.role.entity.RolePermission;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 角色权限关联数据访问接口
 */
@Mapper
public interface RolePermissionMapper {

    /**
     * 根据角色ID获取权限ID列表
     */
    @Select("SELECT permission_id FROM role_permissions WHERE role_id = #{roleId}")
    List<Long> findPermissionIdsByRoleId(Long roleId);

    /**
     * 根据权限ID获取角色ID列表
     */
    @Select("SELECT role_id FROM role_permissions WHERE permission_id = #{permissionId}")
    List<Long> findRoleIdsByPermissionId(Long permissionId);

    /**
     * 添加角色权限关联
     */
    @Insert("INSERT INTO role_permissions (role_id, permission_id, created_at) " +
            "VALUES (#{roleId}, #{permissionId}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(RolePermission rolePermission);

    /**
     * 删除角色的所有权限
     */
    @Delete("DELETE FROM role_permissions WHERE role_id = #{roleId}")
    int deleteByRoleId(Long roleId);

    /**
     * 删除权限的所有角色关联
     */
    @Delete("DELETE FROM role_permissions WHERE permission_id = #{permissionId}")
    int deleteByPermissionId(Long permissionId);

    /**
     * 删除特定的角色权限关联
     */
    @Delete("DELETE FROM role_permissions WHERE role_id = #{roleId} AND permission_id = #{permissionId}")
    int deleteByRoleIdAndPermissionId(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    /**
     * 检查角色权限关联是否存在
     */
    @Select("SELECT COUNT(*) FROM role_permissions WHERE role_id = #{roleId} AND permission_id = #{permissionId}")
    int countByRoleIdAndPermissionId(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    /**
     * 批量插入角色权限关联
     */
    @Insert("<script>" +
            "INSERT INTO role_permissions (role_id, permission_id, created_at) VALUES " +
            "<foreach collection='rolePermissions' item='item' separator=','>" +
            "(#{item.roleId}, #{item.permissionId}, #{item.createdAt})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("rolePermissions") List<RolePermission> rolePermissions);
}