-- DevOps 管理平台初始化数据脚本
-- 描述: 插入基础角色、权限和管理员用户

-- 插入基础角色
INSERT INTO roles (code, name, description, status, is_system) VALUES
('SUPER_ADMIN', '超级管理员', '系统超级管理员，拥有所有权限', 1, TRUE),
('ADMIN', '管理员', '系统管理员，拥有大部分管理权限', 1, TRUE),
('USER', '普通用户', '系统普通用户', 1, TRUE)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description);

-- 插入默认管理员用户
-- 密码: admin123 (BCrypt 加密)
INSERT INTO users (username, email, password_hash, nickname, status, email_verified) VALUES
('admin', 'admin@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 1, 1)
ON DUPLICATE KEY UPDATE
    nickname = VALUES(nickname);

-- 插入测试用户
-- 密码: user123 (BCrypt 加密)
INSERT INTO users (username, email, password_hash, nickname, status, email_verified) VALUES
('user', 'user@example.com', '$2a$10$xn3LI/AjqicFYZFruSwve.dcOYKrJwOJLkfJB7XCx3RmBKGHoGFDG', '测试用户', 1, 1)
ON DUPLICATE KEY UPDATE
    nickname = VALUES(nickname);

-- 为管理员分配 ADMIN 角色
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'admin' AND r.code = 'ADMIN'
ON DUPLICATE KEY UPDATE created_at = created_at;

-- 为测试用户分配 USER 角色
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'user' AND r.code = 'USER'
ON DUPLICATE KEY UPDATE created_at = created_at;

-- 插入基础权限
INSERT INTO permissions (code, name, description, module, resource, operation_type, status, is_system) VALUES
('user:profile:view', '查看个人信息', '查看个人用户信息', 'USER_PROFILE', 'profile', 'READ', 1, TRUE),
('user:profile:update', '更新个人信息', '更新个人用户信息', 'USER_PROFILE', 'profile', 'UPDATE', 1, TRUE),
('user:password:change', '修改密码', '修改个人密码', 'USER_PROFILE', 'password', 'UPDATE', 1, TRUE),
('user:list', '用户列表', '查看用户列表', 'USER_MANAGEMENT', 'user', 'LIST', 1, TRUE),
('user:view', '查看用户', '查看用户详细信息', 'USER_MANAGEMENT', 'user', 'READ', 1, TRUE),
('user:create', '创建用户', '创建新用户', 'USER_MANAGEMENT', 'user', 'CREATE', 1, TRUE),
('user:update', '更新用户', '更新用户信息', 'USER_MANAGEMENT', 'user', 'UPDATE', 1, TRUE),
('user:delete', '删除用户', '删除用户', 'USER_MANAGEMENT', 'user', 'DELETE', 1, TRUE)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description);

-- 为普通用户角色分配基础权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.code = 'USER'
AND p.code IN ('user:profile:view', 'user:profile:update', 'user:password:change')
ON DUPLICATE KEY UPDATE created_at = created_at;

-- 为管理员分配所有权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.code = 'ADMIN'
ON DUPLICATE KEY UPDATE created_at = created_at;
