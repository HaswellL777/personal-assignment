package com.example.demo.common;

/**
 * 错误代码常量
 */
public class ErrorCode {
    
    // 用户相关错误
    public static final int USER_NOT_FOUND = 40001;
    public static final int USER_ALREADY_EXISTS = 40002;
    public static final int USER_DISABLED = 40003;
    public static final int USER_LOCKED = 40004;
    public static final int INVALID_CREDENTIALS = 40005;
    public static final int INVALID_PASSWORD = 40006;
    
    // 认证相关错误
    public static final int UNAUTHORIZED = 40101;
    public static final int TOKEN_EXPIRED = 40102;
    public static final int TOKEN_INVALID = 40103;
    
    // 权限相关错误
    public static final int FORBIDDEN = 40301;
    public static final int INSUFFICIENT_PERMISSIONS = 40302;
    
    // 系统错误
    public static final int INTERNAL_ERROR = 50000;
    public static final int DATABASE_ERROR = 50001;
    public static final int NETWORK_ERROR = 50002;
    
    // 参数错误
    public static final int INVALID_PARAMETER = 40000;
    public static final int MISSING_PARAMETER = 40007;
}