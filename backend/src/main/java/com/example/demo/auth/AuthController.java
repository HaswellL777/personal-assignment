package com.example.demo.auth;

import com.example.demo.auth.dto.AuthResponse;
import com.example.demo.auth.dto.LoginRequest;
import com.example.demo.auth.dto.RegisterRequest;
import com.example.demo.common.ApiResponse;
import com.example.demo.common.BusinessException;
import com.example.demo.security.JwtUtil;
import com.example.demo.user.entity.User;
import com.example.demo.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        // 注册用户
        User user = userService.register(request);

        // 生成JWT token
        String token = jwtUtil.generateToken(user.getUsername(), user.getId());

        // 构建响应
        AuthResponse response = new AuthResponse(token, user);

        logger.info("用户注册成功: {}", user.getUsername());
        return ApiResponse.ok(response);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            // 验证用户凭据
            User user = userService.login(request, httpRequest);
            logger.info("用户登录验证成功: {}, ID: {}", user.getUsername(), user.getId());

            // 生成JWT token
            String token = jwtUtil.generateToken(user.getUsername(), user.getId());
            logger.debug("JWT token生成成功");

            // 构建响应
            AuthResponse authResponse = new AuthResponse();
            authResponse.setToken(token);
            authResponse.setUser(user);

            logger.info("用户 {} 登录成功", user.getUsername());
            return ApiResponse.ok(authResponse);
        } catch (BusinessException e) {
            logger.warn("登录失败 - BusinessException: {}", e.getMessage());
            return ApiResponse.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("登录失败 - 异常: {} - {}", e.getClass().getName(), e.getMessage(), e);
            return ApiResponse.fail(50000, "登录失败");
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        // JWT是无状态的，客户端删除token即可
        logger.debug("用户登出");
        return ApiResponse.ok();
    }
}
