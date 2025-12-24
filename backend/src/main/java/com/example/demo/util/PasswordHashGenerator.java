package com.example.demo.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码哈希生成工具
 * 用于生成 BCrypt 密码哈希
 */
public class PasswordHashGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 生成测试密码 "123456" 的 BCrypt 哈希
        String password = "123456";
        String hash = encoder.encode(password);

        System.out.println("=".repeat(80));
        System.out.println("密码哈希生成工具");
        System.out.println("=".repeat(80));
        System.out.println("原始密码: " + password);
        System.out.println("BCrypt哈希: " + hash);
        System.out.println("=".repeat(80));
        System.out.println("\nSQL更新语句（admin账号）:");
        System.out.println("UPDATE users SET password_hash = '" + hash + "', login_attempts = 0, locked_until = NULL WHERE username = 'admin';");
        System.out.println("\nSQL更新语句（user账号）:");
        String userHash = encoder.encode(password);
        System.out.println("UPDATE users SET password_hash = '" + userHash + "', login_attempts = 0, locked_until = NULL WHERE username = 'user';");
        System.out.println("=".repeat(80));

        // 验证密码
        boolean matches = encoder.matches(password, hash);
        System.out.println("\n密码验证测试: " + (matches ? "通过 ✓" : "失败 ✗"));
    }
}
