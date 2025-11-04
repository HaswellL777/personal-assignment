package com.example.demo.role.mapper;

import com.example.demo.role.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色Mapper接口
 */
@Mapper
public interface RoleMapper {
    
    /**
     * 查找所有角色
     */
    List<Role> findAll();
    
    /**
     * 根据ID查找角色
     */
    Role findById(Long id);
    
    /**
     * 插入角色
     */
    int insert(Role role);
    
    /**
     * 更新角色
     */
    int update(Role role);
    
    /**
     * 删除角色
     */
    int deleteById(Long id);
    
    /**
     * 根据名称查找角色
     */
    Role findByName(String name);
    
    /**
     * 统计所有角色数量
     */
    Long countAll();
    
    /**
     * 根据是否为系统角色统计数量
     */
    Long countByIsSystem(@Param("isSystem") Boolean isSystem);
    
    /**
     * 根据用户ID查找角色代码列表
     */
    List<String> findRoleCodesByUserId(@Param("userId") Long userId);
}