package com.example.demo.user.mapper;

import com.example.demo.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户数据访问接口
 */
@Mapper
public interface UserMapper {
    
    /**
     * 根据用户名查找用户
     */
    User findByUsername(@Param("username") String username);
    
    /**
     * 根据邮箱查找用户
     */
    User findByEmail(@Param("email") String email);
    
    /**
     * 根据手机号查找用户
     */
    User findByPhone(@Param("phone") String phone);
    
    /**
     * 根据ID查找用户
     */
    User findById(@Param("id") Long id);
    
    /**
     * 插入新用户
     */
    int insert(User user);
    
    /**
     * 更新用户信息
     */
    int update(User user);
    
    /**
     * 更新最后登录信息
     */
    int updateLastLogin(@Param("id") Long id, @Param("lastLoginAt") LocalDateTime lastLoginAt, @Param("lastLoginIp") String lastLoginIp);
    
    /**
     * 查找所有用户
     */
    List<User> findAll();
    
    /**
     * 统计所有用户数量
     */
    Long countAll();
    
    /**
     * 根据状态统计用户数量
     */
    Long countByStatus(@Param("status") Integer status);
}