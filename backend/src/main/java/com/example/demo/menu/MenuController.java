package com.example.demo.menu;

import com.example.demo.common.ApiResponse;
import com.example.demo.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 菜单控制器
 * 根据用户权限返回不同的菜单
 */
@RestController
@RequestMapping("/api/v1")
public class MenuController {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取当前用户的菜单列表
     * 管理员可见所有菜单，普通用户只能看到基础菜单
     */
    @GetMapping("/menus")
    public ApiResponse<List<Map<String, Object>>> getMenus(HttpServletRequest request) {
        try {
            // 从请求头获取token
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ApiResponse.fail(401, "未提供有效的认证token");
            }

            String token = authHeader.substring(7);

            // 验证token并获取用户名
            if (!jwtUtil.validateToken(token)) {
                return ApiResponse.fail(401, "token无效或已过期");
            }

            String username = jwtUtil.getUsernameFromToken(token);

            // 根据用户名判断角色并返回对应菜单
            List<Map<String, Object>> menus = new ArrayList<>();

            if ("admin".equals(username)) {
                // 管理员菜单 - 可见所有菜单
                menus.add(createMenuItem("/home", "首页", "dashboard"));
                menus.add(createMenuItem("/home/dashboard", "数据面板", "dashboard"));
                menus.add(createMenuItem("/home/apps", "应用管理", "apps"));
                menus.add(createMenuItem("/home/profile", "个人信息", "user"));
            } else {
                // 普通用户菜单 - 只能看到基础菜单
                menus.add(createMenuItem("/home", "首页", "dashboard"));
                menus.add(createMenuItem("/home/profile", "个人信息", "user"));
            }

            return ApiResponse.ok(menus);

        } catch (Exception e) {
            return ApiResponse.fail(500, "获取菜单失败: " + e.getMessage());
        }
    }

    /**
     * 创建菜单项
     */
    private Map<String, Object> createMenuItem(String path, String name, String icon) {
        Map<String, Object> menu = new HashMap<>();
        menu.put("path", path);
        menu.put("name", name);
        menu.put("icon", icon);
        return menu;
    }
}
