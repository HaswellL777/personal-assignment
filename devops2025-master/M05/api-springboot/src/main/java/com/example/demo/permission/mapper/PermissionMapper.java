package com.example.demo.permission.mapper;

import com.example.demo.permission.entity.Permission;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 权限数据访问接口
 */
@Mapper
public interface PermissionMapper {

    /**
     * 获取所有权限
     */
    @Select("SELECT * FROM permissions ORDER BY module, name")
    List<Permission> findAll();

    /**
     * 根据ID获取权限
     */
    @Select("SELECT * FROM permissions WHERE id = #{id}")
    Permission findById(Long id);

    /**
     * 根据代码获取权限
     */
    @Select("SELECT * FROM permissions WHERE code = #{code}")
    Permission findByCode(String code);

    /**
     * 根据模块获取权限
     */
    @Select("SELECT * FROM permissions WHERE module = #{module} ORDER BY name")
    List<Permission> findByModule(String module);

    /**
     * 获取所有模块
     */
    @Select("SELECT DISTINCT module FROM permissions ORDER BY module")
    List<String> findAllModules();

    /**
     * 创建权限
     */
    @Insert("INSERT INTO permissions (name, code, description, module, resource, action, status, is_system, created_at, updated_at) " +
            "VALUES (#{name}, #{code}, #{description}, #{module}, #{resource}, #{action}, #{status}, #{isSystem}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Permission permission);

    /**
     * 更新权限
     */
    @Update("UPDATE permissions SET name = #{name}, code = #{code}, description = #{description}, " +
            "module = #{module}, resource = #{resource}, action = #{action}, status = #{status}, " +
            "updated_at = #{updatedAt} WHERE id = #{id}")
    int update(Permission permission);

    /**
     * 删除权限
     */
    @Delete("DELETE FROM permissions WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 检查权限代码是否存在
     */
    @Select("SELECT COUNT(*) FROM permissions WHERE code = #{code} AND id != #{id}")
    int countByCodeExcludeId(@Param("code") String code, @Param("id") Long id);

    /**
     * 检查权限代码是否存在（新增时）
     */
    @Select("SELECT COUNT(*) FROM permissions WHERE code = #{code}")
    int countByCode(String code);

    /**
     * 根据角色ID获取权限
     */
    @Select("SELECT p.* FROM permissions p " +
            "INNER JOIN role_permissions rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId} ORDER BY p.module, p.name")
    List<Permission> findByRoleId(Long roleId);
    
    /**
     * 统计所有权限数量
     */
    @Select("SELECT COUNT(*) FROM permissions")
    Long countAll();
    
    /**
     * 根据是否为系统权限统计数量
     */
    @Select("SELECT COUNT(*) FROM permissions WHERE is_system = #{isSystem}")
    Long countByIsSystem(@Param("isSystem") Boolean isSystem);
    
    /**
     * 根据用户ID查找权限代码列表
     */
    @Select("SELECT DISTINCT p.code FROM permissions p " +
            "INNER JOIN role_permissions rp ON p.id = rp.permission_id " +
            "INNER JOIN user_roles ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<String> findPermissionCodesByUserId(@Param("userId") Long userId);
}