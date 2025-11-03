package com.example.demo.user.service;

import com.example.demo.auth.dto.LoginRequest;
import com.example.demo.auth.dto.RegisterRequest;
import com.example.demo.common.BusinessException;
import com.example.demo.common.ErrorCode;
import com.example.demo.user.entity.User;
import com.example.demo.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户服务类
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 用户注册
     */
    public User register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userMapper.findByUsername(request.getUsername()) != null) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (userMapper.findByEmail(request.getEmail()) != null) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "邮箱已存在");
        }
        
        // 检查手机号是否已存在（如果提供）
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            if (userMapper.findByPhone(request.getPhone()) != null) {
                throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "手机号已存在");
            }
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setStatus(1); // 默认激活状态
        user.setEmailVerified(0);
        user.setPhoneVerified(0);
        user.setLoginAttempts(0);
        
        // 保存用户
        userMapper.insert(user);
        
        return user;
    }

    /**
     * 用户登录
     */
    public User login(LoginRequest request) {
        logger.info("开始登录验证，用户标识: {}", request.getUsername());
        
        // 查找用户（支持用户名、邮箱、手机号登录）
        User user = null;
        String loginField = request.getUsername();
        
        if (loginField.contains("@")) {
            // 邮箱登录
            user = userMapper.findByEmail(loginField);
            logger.info("通过邮箱查找用户: {}", loginField);
        } else if (loginField.matches("^1[3-9]\\d{9}$")) {
            // 手机号登录
            user = userMapper.findByPhone(loginField);
            logger.info("通过手机号查找用户: {}", loginField);
        } else {
            // 用户名登录
            user = userMapper.findByUsername(loginField);
            logger.info("通过用户名查找用户: {}", loginField);
        }
        
        if (user == null) {
            logger.warn("用户不存在: {}", loginField);
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }
        
        logger.info("找到用户: {}, ID: {}, 状态: {}", user.getUsername(), user.getId(), user.getStatus());
        
        // 检查用户状态
        if (user.getStatus() != 1) {
            logger.warn("用户已被禁用: {}", user.getUsername());
            throw new BusinessException(ErrorCode.USER_DISABLED, "用户已被禁用");
        }
        
        // 检查是否被锁定
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            logger.warn("用户已被锁定: {}, 锁定到: {}", user.getUsername(), user.getLockedUntil());
            throw new BusinessException(ErrorCode.USER_LOCKED, "用户已被锁定");
        }
        
        // 验证密码
        logger.info("开始验证密码，用户: {}", user.getUsername());
        logger.debug("输入密码: {}", request.getPassword());
        logger.debug("存储的密码哈希: {}", user.getPasswordHash());
        
        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());
        logger.info("密码验证结果: {}", passwordMatches);
        
        if (!passwordMatches) {
            // 增加登录失败次数
            user.setLoginAttempts(user.getLoginAttempts() + 1);
            logger.warn("密码验证失败，登录失败次数: {}", user.getLoginAttempts());
            
            if (user.getLoginAttempts() >= 5) {
                // 锁定用户30分钟
                user.setLockedUntil(LocalDateTime.now().plusMinutes(30));
                logger.warn("用户被锁定30分钟: {}", user.getUsername());
            }
            userMapper.update(user);
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS, "密码错误");
        }
        
        logger.info("登录成功: {}", user.getUsername());
        
        // 登录成功，重置登录失败次数
        if (user.getLoginAttempts() > 0) {
            user.setLoginAttempts(0);
            user.setLockedUntil(null);
            userMapper.update(user);
            logger.info("重置登录失败次数: {}", user.getUsername());
        }
        
        // 更新最后登录信息
        userMapper.updateLastLogin(user.getId(), LocalDateTime.now(), getClientIp());
        
        return user;
    }
    
    /**
     * 获取客户端IP（简化实现）
     */
    private String getClientIp() {
        // 这里简化处理，实际应该从HttpServletRequest中获取
        return "127.0.0.1";
    }

    /**
     * 根据ID查找用户
     */
    public User findById(Long id) {
        return userMapper.findById(id);
    }

    /**
     * 根据用户名查找用户
     */
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    /**
     * 更新用户信息
     */
    public User updateProfile(Long userId, String nickname, String bio, String avatarUrl) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        // 更新用户信息
        if (nickname != null) {
            user.setNickname(nickname);
        }
        if (bio != null) {
            user.setBio(bio);
        }
        if (avatarUrl != null) {
            user.setAvatarUrl(avatarUrl);
        }

        // 保存更新
        userMapper.update(user);
        
        return user;
    }

    /**
     * 修改密码
     */
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        // 验证当前密码
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD, "当前密码不正确");
        }

        // 加密新密码
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.setPasswordHash(encodedNewPassword);

        // 保存更新
        userMapper.update(user);
    }

    /**
     * 创建用户（管理员功能）
     */
    public User createUser(String username, String email, String password) {
        // 检查用户名是否已存在
        if (userMapper.findByUsername(username) != null) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (userMapper.findByEmail(email) != null) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "邮箱已存在");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setNickname(username); // 默认昵称为用户名
        user.setStatus(1); // 默认激活状态
        user.setEmailVerified(false);
        user.setPhoneVerified(false);
        user.setLoginAttempts(0);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userMapper.insert(user);
        logger.info("管理员创建用户成功: {}", username);
        
        return user;
    }

    /**
     * 更新用户信息（管理员功能）
     */
    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "用户ID不能为空");
        }

        User existingUser = userMapper.findById(user.getId());
        if (existingUser == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        // 检查邮箱是否被其他用户使用
        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
            User emailUser = userMapper.findByEmail(user.getEmail());
            if (emailUser != null && !emailUser.getId().equals(user.getId())) {
                throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "邮箱已被其他用户使用");
            }
        }

        // 检查手机号是否被其他用户使用
        if (user.getPhone() != null && !user.getPhone().equals(existingUser.getPhone())) {
            User phoneUser = userMapper.findByPhone(user.getPhone());
            if (phoneUser != null && !phoneUser.getId().equals(user.getId())) {
                throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "手机号已被其他用户使用");
            }
        }

        user.setUpdatedAt(LocalDateTime.now());
        userMapper.update(user);
        logger.info("管理员更新用户信息成功: {}", user.getUsername());
        
        return userMapper.findById(user.getId());
    }

    /**
     * 删除用户（管理员功能）
     */
    public boolean deleteUser(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            return false;
        }

        // 这里可以实现软删除或硬删除
        // 为了安全起见，我们实现软删除（设置删除时间）
        user.setDeletedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.update(user);
        
        logger.info("管理员删除用户成功: {}", user.getUsername());
        return true;
    }

    /**
     * 更新用户状态（管理员功能）
     */
    public boolean updateUserStatus(Long userId, Integer status) {
        User user = userMapper.findById(userId);
        if (user == null) {
            return false;
        }

        user.setStatus(status);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.update(user);
        
        logger.info("管理员更新用户状态成功: {} -> {}", user.getUsername(), status);
        return true;
    }

    /**
     * 重置用户密码（管理员功能）
     */
    public String resetPassword(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        // 生成新的临时密码
        String newPassword = generateTemporaryPassword();
        String encodedPassword = passwordEncoder.encode(newPassword);
        
        user.setPasswordHash(encodedPassword);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.update(user);
        
        logger.info("管理员重置用户密码成功: {}", user.getUsername());
        return newPassword;
    }

    /**
     * 生成临时密码
     */
    private String generateTemporaryPassword() {
        // 生成8位随机密码
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            password.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return password.toString();
    }
}