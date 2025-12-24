package com.example.demo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 用户解锁工具
 * 重置登录失败次数和解锁账号
 *
 * 使用方式: 设置环境变量后运行
 * 必需环境变量: DB_HOST, DB_PORT, DB_NAME, DB_USERNAME, DB_PASSWORD
 */
public class UnlockUserTool {

    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }

    private static String getRequiredEnv(String key) {
        String value = System.getenv(key);
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException("必需的环境变量未设置: " + key);
        }
        return value;
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("用户解锁工具");
        System.out.println("=".repeat(80));

        Connection conn = null;
        try {
            // 从环境变量读取数据库配置
            String dbHost = getEnvOrDefault("DB_HOST", "localhost");
            String dbPort = getEnvOrDefault("DB_PORT", "3306");
            String dbName = getEnvOrDefault("DB_NAME", "devops2025");
            String dbUsername = getRequiredEnv("DB_USERNAME");
            String dbPassword = getRequiredEnv("DB_PASSWORD");

            String url = String.format(
                "jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai",
                dbHost, dbPort, dbName
            );

            // 加载驱动
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 连接数据库
            System.out.println("\n连接数据库...");
            System.out.println("Host: " + dbHost + ":" + dbPort);
            System.out.println("Database: " + dbName);
            conn = DriverManager.getConnection(url, dbUsername, dbPassword);
            System.out.println("✓ 数据库连接成功");

            // 解锁所有测试账号
            System.out.println("\n解锁账号...");
            unlockUsers(conn);

            // 验证结果
            System.out.println("\n验证结果：");
            verifyUsers(conn);

            System.out.println("\n" + "=".repeat(80));
            System.out.println("✓ 完成！所有测试账号已解锁");
            System.out.println("=".repeat(80));

        } catch (IllegalStateException e) {
            System.err.println("\n✗ 配置错误：" + e.getMessage());
            System.err.println("\n请设置以下环境变量：");
            System.err.println("  export DB_USERNAME=your_username");
            System.err.println("  export DB_PASSWORD=your_password");
            System.err.println("  export DB_HOST=localhost (可选)");
            System.err.println("  export DB_PORT=3306 (可选)");
            System.err.println("  export DB_NAME=devops2025 (可选)");
        } catch (Exception e) {
            System.err.println("\n✗ 失败：" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    // 忽略关闭异常
                }
            }
        }
    }

    private static void unlockUsers(Connection conn) throws Exception {
        String sql = "UPDATE users SET login_attempts = 0, locked_until = NULL, updated_at = NOW() WHERE username IN ('admin', 'user')";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int rows = pstmt.executeUpdate();
            System.out.println("✓ 已解锁 " + rows + " 个账号");
        }
    }

    private static void verifyUsers(Connection conn) throws Exception {
        String sql = "SELECT username, login_attempts, locked_until FROM users WHERE username IN ('admin', 'user')";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("-".repeat(80));
            System.out.printf("%-15s %-20s %-30s%n", "用户名", "登录失败次数", "锁定状态");
            System.out.println("-".repeat(80));

            while (rs.next()) {
                String username = rs.getString("username");
                int loginAttempts = rs.getInt("login_attempts");
                String lockedUntil = rs.getString("locked_until");

                System.out.printf("%-15s %-20d %-30s%n",
                        username,
                        loginAttempts,
                        lockedUntil == null ? "未锁定 ✓" : "已锁定至: " + lockedUntil);
            }
            System.out.println("-".repeat(80));
        }
    }
}
