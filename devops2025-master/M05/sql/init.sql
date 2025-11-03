-- 用户管理系统数据库初始化脚本
-- 数据库: devops2025
-- 创建时间: 2024-01-15

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 用户表 (users)
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `email` varchar(100) NOT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `password_hash` varchar(255) NOT NULL COMMENT '密码哈希值',
  `nickname` varchar(100) DEFAULT NULL COMMENT '昵称',
  `avatar_url` varchar(500) DEFAULT NULL COMMENT '头像URL',
  `bio` text COMMENT '个人简介',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '账户状态：0-禁用，1-正常，2-锁定',
  `email_verified` tinyint NOT NULL DEFAULT '0' COMMENT '邮箱是否验证：0-未验证，1-已验证',
  `phone_verified` tinyint NOT NULL DEFAULT '0' COMMENT '手机是否验证：0-未验证，1-已验证',
  `login_attempts` int NOT NULL DEFAULT '0' COMMENT '登录失败次数',
  `locked_until` datetime DEFAULT NULL COMMENT '锁定到期时间',
  `last_login_at` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(45) DEFAULT NULL COMMENT '最后登录IP',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '删除时间（软删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_deleted_at` (`deleted_at`),
  KEY `idx_email_status` (`email`, `status`),
  KEY `idx_phone_status` (`phone`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ----------------------------
-- 2. 用户登录日志表 (user_login_logs)
-- ----------------------------
DROP TABLE IF EXISTS `user_login_logs`;
CREATE TABLE `user_login_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `login_type` varchar(20) NOT NULL COMMENT '登录方式：email, username, phone, sms',
  `ip_address` varchar(45) NOT NULL COMMENT '登录IP地址',
  `user_agent` text COMMENT '用户代理信息',
  `device_info` varchar(200) DEFAULT NULL COMMENT '设备信息',
  `location` varchar(100) DEFAULT NULL COMMENT '登录地点',
  `status` tinyint NOT NULL COMMENT '登录状态：0-失败，1-成功',
  `failure_reason` varchar(100) DEFAULT NULL COMMENT '失败原因',
  `session_id` varchar(100) DEFAULT NULL COMMENT '会话ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_ip_address` (`ip_address`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_status` (`status`),
  KEY `idx_user_time` (`user_id`, `created_at`),
  CONSTRAINT `fk_login_logs_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户登录日志表';

-- ----------------------------
-- 3. 用户验证码表 (user_verification_codes)
-- ----------------------------
DROP TABLE IF EXISTS `user_verification_codes`;
CREATE TABLE `user_verification_codes` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '验证码ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID（注册时可能为空）',
  `type` varchar(20) NOT NULL COMMENT '验证码类型：email_register, email_reset, sms_register, sms_reset, sms_login',
  `target` varchar(100) NOT NULL COMMENT '目标邮箱或手机号',
  `code` varchar(10) NOT NULL COMMENT '验证码',
  `used` tinyint NOT NULL DEFAULT '0' COMMENT '是否已使用：0-未使用，1-已使用',
  `expires_at` datetime NOT NULL COMMENT '过期时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_target_type` (`target`, `type`),
  KEY `idx_code` (`code`),
  KEY `idx_expires_at` (`expires_at`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_verification_codes_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户验证码表';

-- ----------------------------
-- 4. 角色表 (roles)
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(50) NOT NULL COMMENT '角色名称',
  `code` varchar(50) NOT NULL COMMENT '角色编码',
  `description` text COMMENT '角色描述',
  `is_system` tinyint NOT NULL DEFAULT '0' COMMENT '是否系统角色：0-否，1-是（系统角色不可删除）',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序顺序',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '删除时间（软删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_status` (`status`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_deleted_at` (`deleted_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- ----------------------------
-- 5. 权限表 (permissions)
-- ----------------------------
DROP TABLE IF EXISTS `permissions`;
CREATE TABLE `permissions` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `name` varchar(100) NOT NULL COMMENT '权限名称',
  `code` varchar(100) NOT NULL COMMENT '权限编码',
  `description` text COMMENT '权限描述',
  `module` varchar(50) NOT NULL COMMENT '所属模块',
  `resource` varchar(100) DEFAULT NULL COMMENT '资源标识',
  `action` varchar(50) DEFAULT NULL COMMENT '操作类型：create, read, update, delete, execute',
  `is_system` tinyint NOT NULL DEFAULT '0' COMMENT '是否系统权限：0-否，1-是',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序顺序',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '删除时间（软删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_module` (`module`),
  KEY `idx_resource_action` (`resource`, `action`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted_at` (`deleted_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- ----------------------------
-- 6. 角色权限关联表 (role_permissions)
-- ----------------------------
DROP TABLE IF EXISTS `role_permissions`;
CREATE TABLE `role_permissions` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_permission_id` (`permission_id`),
  CONSTRAINT `fk_role_permissions_role_id` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_role_permissions_permission_id` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- ----------------------------
-- 7. 用户角色关联表 (user_roles)
-- ----------------------------
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `assigned_by` bigint DEFAULT NULL COMMENT '分配者用户ID',
  `assigned_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
  `expires_at` datetime DEFAULT NULL COMMENT '过期时间（可选）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_assigned_by` (`assigned_by`),
  KEY `idx_expires_at` (`expires_at`),
  CONSTRAINT `fk_user_roles_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_roles_role_id` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_roles_assigned_by` FOREIGN KEY (`assigned_by`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- ----------------------------
-- 8. 团队表 (teams)
-- ----------------------------
DROP TABLE IF EXISTS `teams`;
CREATE TABLE `teams` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '团队ID',
  `name` varchar(100) NOT NULL COMMENT '团队名称',
  `code` varchar(50) DEFAULT NULL COMMENT '团队编码',
  `description` text COMMENT '团队描述',
  `avatar_url` varchar(500) DEFAULT NULL COMMENT '团队头像URL',
  `visibility` varchar(20) NOT NULL DEFAULT 'private' COMMENT '可见性：public-公开，private-私有',
  `join_policy` varchar(20) NOT NULL DEFAULT 'approval' COMMENT '加入策略：open-开放，approval-需审批，invite-仅邀请',
  `max_members` int DEFAULT NULL COMMENT '最大成员数（null表示无限制）',
  `owner_id` bigint NOT NULL COMMENT '团队负责人ID',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-正常，2-已解散',
  `settings` json DEFAULT NULL COMMENT '团队设置（JSON格式）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '删除时间（软删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_owner_id` (`owner_id`),
  KEY `idx_visibility` (`visibility`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_deleted_at` (`deleted_at`),
  CONSTRAINT `fk_teams_owner_id` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='团队表';

-- ----------------------------
-- 9. 团队成员表 (team_members)
-- ----------------------------
DROP TABLE IF EXISTS `team_members`;
CREATE TABLE `team_members` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '成员ID',
  `team_id` bigint NOT NULL COMMENT '团队ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role` varchar(20) NOT NULL DEFAULT 'member' COMMENT '团队角色：owner-负责人，admin-管理员，member-普通成员',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-已移除，1-正常',
  `joined_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  `invited_by` bigint DEFAULT NULL COMMENT '邀请者ID',
  `permissions` json DEFAULT NULL COMMENT '特殊权限设置（JSON格式）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_team_user` (`team_id`, `user_id`),
  KEY `idx_team_id` (`team_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role` (`role`),
  KEY `idx_status` (`status`),
  KEY `idx_invited_by` (`invited_by`),
  KEY `idx_team_role_status` (`team_id`, `role`, `status`),
  CONSTRAINT `fk_team_members_team_id` FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_team_members_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_team_members_invited_by` FOREIGN KEY (`invited_by`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='团队成员表';

-- ----------------------------
-- 10. 团队邀请表 (team_invitations)
-- ----------------------------
DROP TABLE IF EXISTS `team_invitations`;
CREATE TABLE `team_invitations` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '邀请ID',
  `team_id` bigint NOT NULL COMMENT '团队ID',
  `inviter_id` bigint NOT NULL COMMENT '邀请者ID',
  `invitee_email` varchar(100) DEFAULT NULL COMMENT '被邀请者邮箱',
  `invitee_user_id` bigint DEFAULT NULL COMMENT '被邀请者用户ID（如果已注册）',
  `invitation_code` varchar(100) NOT NULL COMMENT '邀请码',
  `role` varchar(20) NOT NULL DEFAULT 'member' COMMENT '邀请角色',
  `message` text COMMENT '邀请消息',
  `status` varchar(20) NOT NULL DEFAULT 'pending' COMMENT '状态：pending-待处理，accepted-已接受，rejected-已拒绝，expired-已过期',
  `expires_at` datetime NOT NULL COMMENT '过期时间',
  `responded_at` datetime DEFAULT NULL COMMENT '响应时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_invitation_code` (`invitation_code`),
  KEY `idx_team_id` (`team_id`),
  KEY `idx_inviter_id` (`inviter_id`),
  KEY `idx_invitee_email` (`invitee_email`),
  KEY `idx_invitee_user_id` (`invitee_user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_expires_at` (`expires_at`),
  CONSTRAINT `fk_team_invitations_team_id` FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_team_invitations_inviter_id` FOREIGN KEY (`inviter_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_team_invitations_invitee_user_id` FOREIGN KEY (`invitee_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='团队邀请表';

-- ----------------------------
-- 11. 团队加入申请表 (team_join_requests)
-- ----------------------------
DROP TABLE IF EXISTS `team_join_requests`;
CREATE TABLE `team_join_requests` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `team_id` bigint NOT NULL COMMENT '团队ID',
  `user_id` bigint NOT NULL COMMENT '申请者ID',
  `message` text COMMENT '申请消息',
  `status` varchar(20) NOT NULL DEFAULT 'pending' COMMENT '状态：pending-待审批，approved-已批准，rejected-已拒绝，cancelled-已取消',
  `reviewed_by` bigint DEFAULT NULL COMMENT '审批者ID',
  `reviewed_at` datetime DEFAULT NULL COMMENT '审批时间',
  `review_message` text COMMENT '审批意见',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_team_id` (`team_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_reviewed_by` (`reviewed_by`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `fk_team_join_requests_team_id` FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_team_join_requests_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_team_join_requests_reviewed_by` FOREIGN KEY (`reviewed_by`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='团队加入申请表';

-- ----------------------------
-- 12. 系统配置表 (system_configs)
-- ----------------------------
DROP TABLE IF EXISTS `system_configs`;
CREATE TABLE `system_configs` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(100) NOT NULL COMMENT '配置键',
  `config_value` text COMMENT '配置值',
  `description` varchar(255) DEFAULT NULL COMMENT '配置描述',
  `type` varchar(20) NOT NULL DEFAULT 'string' COMMENT '值类型：string, number, boolean, json',
  `is_public` tinyint NOT NULL DEFAULT '0' COMMENT '是否公开：0-否，1-是',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`),
  KEY `idx_is_public` (`is_public`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- ----------------------------
-- 13. 操作日志表 (operation_logs)
-- ----------------------------
DROP TABLE IF EXISTS `operation_logs`;
CREATE TABLE `operation_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint DEFAULT NULL COMMENT '操作用户ID',
  `operation` varchar(100) NOT NULL COMMENT '操作类型',
  `resource_type` varchar(50) DEFAULT NULL COMMENT '资源类型',
  `resource_id` varchar(100) DEFAULT NULL COMMENT '资源ID',
  `description` text COMMENT '操作描述',
  `ip_address` varchar(45) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` text COMMENT '用户代理',
  `request_data` json DEFAULT NULL COMMENT '请求数据',
  `response_data` json DEFAULT NULL COMMENT '响应数据',
  `status` varchar(20) NOT NULL COMMENT '操作状态：success, failure, error',
  `error_message` text COMMENT '错误信息',
  `duration` int DEFAULT NULL COMMENT '操作耗时（毫秒）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_operation` (`operation`),
  KEY `idx_resource` (`resource_type`, `resource_id`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_user_operation_time` (`user_id`, `operation`, `created_at`),
  CONSTRAINT `fk_operation_logs_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ----------------------------
-- 初始化数据
-- ----------------------------

-- 1. 初始化角色数据
INSERT INTO `roles` (`name`, `code`, `description`, `is_system`, `sort_order`) VALUES
('超级管理员', 'super_admin', '系统最高权限管理员，拥有所有权限', 1, 1),
('系统管理员', 'admin', '系统管理员，可管理用户、角色、团队等', 1, 2),
('团队负责人', 'team_owner', '团队创建者和最高管理者', 1, 3),
('团队管理员', 'team_admin', '团队管理员，可管理团队成员', 1, 4),
('普通用户', 'user', '普通注册用户', 1, 5);

-- 2. 初始化权限数据
INSERT INTO `permissions` (`name`, `code`, `description`, `module`, `resource`, `action`, `is_system`, `sort_order`) VALUES
-- 用户管理权限
('查看用户列表', 'user:list', '查看系统用户列表', 'user', 'user', 'read', 1, 1),
('创建用户', 'user:create', '创建新用户', 'user', 'user', 'create', 1, 2),
('编辑用户', 'user:update', '编辑用户信息', 'user', 'user', 'update', 1, 3),
('删除用户', 'user:delete', '删除用户', 'user', 'user', 'delete', 1, 4),
('重置用户密码', 'user:reset_password', '重置用户密码', 'user', 'user', 'execute', 1, 5),

-- 角色管理权限
('查看角色列表', 'role:list', '查看角色列表', 'role', 'role', 'read', 1, 10),
('创建角色', 'role:create', '创建新角色', 'role', 'role', 'create', 1, 11),
('编辑角色', 'role:update', '编辑角色信息', 'role', 'role', 'update', 1, 12),
('删除角色', 'role:delete', '删除角色', 'role', 'role', 'delete', 1, 13),
('分配角色权限', 'role:assign_permission', '为角色分配权限', 'role', 'role', 'execute', 1, 14),

-- 权限管理权限
('查看权限列表', 'permission:list', '查看权限列表', 'permission', 'permission', 'read', 1, 20),
('创建权限', 'permission:create', '创建新权限', 'permission', 'permission', 'create', 1, 21),
('编辑权限', 'permission:update', '编辑权限信息', 'permission', 'permission', 'update', 1, 22),
('删除权限', 'permission:delete', '删除权限', 'permission', 'permission', 'delete', 1, 23),
('分配用户角色', 'user:assign_role', '为用户分配角色', 'user', 'user', 'execute', 1, 24),

-- 团队管理权限
('查看团队列表', 'team:list', '查看团队列表', 'team', 'team', 'read', 1, 30),
('创建团队', 'team:create', '创建新团队', 'team', 'team', 'create', 1, 31),
('编辑团队', 'team:update', '编辑团队信息', 'team', 'team', 'update', 1, 32),
('删除团队', 'team:delete', '删除团队', 'team', 'team', 'delete', 1, 33),
('管理团队成员', 'team:manage_member', '管理团队成员', 'team', 'team_member', 'execute', 1, 34),
('邀请团队成员', 'team:invite', '邀请用户加入团队', 'team', 'team_invitation', 'create', 1, 35),
('审批加入申请', 'team:approve_request', '审批团队加入申请', 'team', 'team_request', 'execute', 1, 36),

-- 系统管理权限
('查看系统配置', 'system:config:read', '查看系统配置', 'system', 'config', 'read', 1, 40),
('修改系统配置', 'system:config:write', '修改系统配置', 'system', 'config', 'update', 1, 41),
('查看操作日志', 'system:log:read', '查看系统操作日志', 'system', 'log', 'read', 1, 42),
('查看登录日志', 'system:login_log:read', '查看用户登录日志', 'system', 'login_log', 'read', 1, 43);

-- 3. 初始化角色权限关联
-- 超级管理员拥有所有权限
INSERT INTO `role_permissions` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM `roles` r, `permissions` p WHERE r.code = 'super_admin';

-- 系统管理员权限
INSERT INTO `role_permissions` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM `roles` r, `permissions` p 
WHERE r.code = 'admin' AND p.code IN (
  'user:list', 'user:create', 'user:update', 'user:reset_password',
  'role:list', 'role:create', 'role:update', 'role:assign_permission',
  'permission:list', 'permission:create', 'permission:update', 'permission:delete', 'user:assign_role',
  'team:list', 'team:update', 'team:delete', 'team:manage_member',
  'system:config:read', 'system:log:read', 'system:login_log:read'
);

-- 团队负责人权限
INSERT INTO `role_permissions` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM `roles` r, `permissions` p 
WHERE r.code = 'team_owner' AND p.code IN (
  'team:create', 'team:update', 'team:delete', 'team:manage_member',
  'team:invite', 'team:approve_request'
);

-- 团队管理员权限
INSERT INTO `role_permissions` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM `roles` r, `permissions` p 
WHERE r.code = 'team_admin' AND p.code IN (
  'team:update', 'team:manage_member', 'team:invite', 'team:approve_request'
);

-- 普通用户权限
INSERT INTO `role_permissions` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM `roles` r, `permissions` p 
WHERE r.code = 'user' AND p.code IN ('team:create', 'team:list');

-- 4. 初始化系统配置
INSERT INTO `system_configs` (`config_key`, `config_value`, `description`, `type`, `is_public`) VALUES
('site_name', '用户管理系统', '网站名称', 'string', 1),
('site_description', '基于RBAC的用户管理系统', '网站描述', 'string', 1),
('user_register_enabled', 'true', '是否允许用户注册', 'boolean', 1),
('email_verification_required', 'true', '注册时是否需要邮箱验证', 'boolean', 0),
('login_max_attempts', '5', '登录最大失败次数', 'number', 0),
('login_lock_duration', '30', '登录锁定时长（分钟）', 'number', 0),
('verification_code_expires', '5', '验证码有效期（分钟）', 'number', 0),
('invitation_expires_days', '7', '邀请链接有效期（天）', 'number', 0),
('max_teams_per_user', '10', '每个用户最大创建团队数', 'number', 0),
('default_team_max_members', '50', '团队默认最大成员数', 'number', 0);

-- 5. 初始化测试用户数据
INSERT INTO `users` (`username`, `email`, `phone`, `password_hash`, `nickname`, `avatar_url`, `bio`, `status`, `email_verified`, `phone_verified`, `last_login_at`, `last_login_ip`) VALUES
('admin', 'admin@devops2025.com', '13800138000', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLO.TAGxK4oa', '系统管理员', 'https://avatar.example.com/admin.jpg', '系统管理员账户', 1, 1, 1, '2024-01-15 10:00:00', '127.0.0.1'),
('zhangsan', 'zhangsan@example.com', '13800138001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLO.TAGxK4oa', '张三', 'https://avatar.example.com/zhangsan.jpg', '前端开发工程师，专注于Vue.js和React开发', 1, 1, 1, '2024-01-15 09:30:00', '192.168.1.100'),
('lisi', 'lisi@example.com', '13800138002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLO.TAGxK4oa', '李四', 'https://avatar.example.com/lisi.jpg', '后端开发工程师，熟悉Spring Boot和微服务架构', 1, 1, 0, '2024-01-14 16:45:00', '192.168.1.101'),
('wangwu', 'wangwu@example.com', '13800138003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLO.TAGxK4oa', '王五', 'https://avatar.example.com/wangwu.jpg', 'DevOps工程师，专注于CI/CD和容器化技术', 1, 1, 1, '2024-01-15 08:20:00', '192.168.1.102'),
('zhaoliu', 'zhaoliu@example.com', '13800138004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLO.TAGxK4oa', '赵六', 'https://avatar.example.com/zhaoliu.jpg', '测试工程师，专注于自动化测试和质量保证', 1, 0, 0, NULL, NULL),
('sunqi', 'sunqi@example.com', '13800138005', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLO.TAGxK4oa', '孙七', NULL, '产品经理，负责产品规划和需求分析', 2, 1, 1, '2024-01-10 14:30:00', '192.168.1.103'),
('disabled_user', 'disabled@example.com', '13800138006', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLO.TAGxK4oa', '禁用用户', NULL, '这是一个被禁用的测试账户', 0, 0, 0, NULL, NULL);

-- 6. 初始化用户角色关联数据
INSERT INTO `user_roles` (`user_id`, `role_id`, `assigned_by`) VALUES
(1, 1, NULL),  -- admin 用户为超级管理员
(2, 5, 1),     -- zhangsan 为普通用户
(3, 5, 1),     -- lisi 为普通用户
(4, 5, 1),     -- wangwu 为普通用户
(5, 5, 1),     -- zhaoliu 为普通用户
(6, 5, 1),     -- sunqi 为普通用户
(7, 5, 1);     -- disabled_user 为普通用户

-- 7. 初始化团队数据
INSERT INTO `teams` (`name`, `code`, `description`, `avatar_url`, `visibility`, `join_policy`, `max_members`, `owner_id`, `status`) VALUES
('前端开发团队', 'frontend-team', '负责前端应用开发，包括Web端和移动端', 'https://avatar.example.com/team-frontend.jpg', 'private', 'approval', 20, 2, 1),
('后端开发团队', 'backend-team', '负责后端服务开发，API设计和数据库管理', 'https://avatar.example.com/team-backend.jpg', 'private', 'approval', 15, 3, 1),
('DevOps团队', 'devops-team', '负责CI/CD流程、容器化部署和运维监控', 'https://avatar.example.com/team-devops.jpg', 'private', 'invite', 10, 4, 1),
('测试团队', 'qa-team', '负责功能测试、自动化测试和质量保证', 'https://avatar.example.com/team-qa.jpg', 'public', 'open', NULL, 5, 1),
('产品团队', 'product-team', '负责产品规划、需求分析和用户体验设计', 'https://avatar.example.com/team-product.jpg', 'private', 'invite', 8, 6, 2);

-- 8. 初始化团队成员数据
INSERT INTO `team_members` (`team_id`, `user_id`, `role`, `status`, `invited_by`) VALUES
-- 前端开发团队
(1, 2, 'owner', 1, NULL),    -- zhangsan 是团队负责人
(1, 5, 'member', 1, 2),      -- zhaoliu 是成员

-- 后端开发团队
(2, 3, 'owner', 1, NULL),    -- lisi 是团队负责人
(2, 2, 'member', 1, 3),      -- zhangsan 也参与后端开发

-- DevOps团队
(3, 4, 'owner', 1, NULL),    -- wangwu 是团队负责人
(3, 3, 'admin', 1, 4),       -- lisi 是管理员

-- 测试团队
(4, 5, 'owner', 1, NULL),    -- zhaoliu 是团队负责人
(4, 2, 'member', 1, 5),      -- zhangsan 参与测试
(4, 3, 'member', 1, 5),      -- lisi 参与测试

-- 产品团队
(5, 6, 'owner', 1, NULL);    -- sunqi 是团队负责人

-- 9. 初始化用户登录日志数据
INSERT INTO `user_login_logs` (`user_id`, `login_type`, `ip_address`, `user_agent`, `device_info`, `location`, `status`, `session_id`) VALUES
(1, 'username', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 'Windows 10 Chrome', '本地', 1, 'sess_admin_001'),
(2, 'email', '192.168.1.100', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36', 'macOS Safari', '北京', 1, 'sess_zhangsan_001'),
(3, 'username', '192.168.1.101', 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36', 'Ubuntu Chrome', '上海', 1, 'sess_lisi_001'),
(4, 'email', '192.168.1.102', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 'Windows 11 Edge', '深圳', 1, 'sess_wangwu_001'),
(5, 'username', '192.168.1.103', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36', 'macOS Chrome', '广州', 0, NULL),
(2, 'email', '192.168.1.100', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X)', 'iPhone Safari', '北京', 1, 'sess_zhangsan_002');

-- 10. 初始化用户验证码数据
INSERT INTO `user_verification_codes` (`user_id`, `type`, `target`, `code`, `used`, `expires_at`) VALUES
(5, 'email_register', 'zhaoliu@example.com', '123456', 0, DATE_ADD(NOW(), INTERVAL 10 MINUTE)),
(NULL, 'email_register', 'newuser@example.com', '789012', 0, DATE_ADD(NOW(), INTERVAL 15 MINUTE)),
(2, 'sms_login', '13800138001', '567890', 1, DATE_SUB(NOW(), INTERVAL 5 MINUTE)),
(3, 'email_reset', 'lisi@example.com', '345678', 0, DATE_ADD(NOW(), INTERVAL 20 MINUTE));

-- 11. 初始化团队邀请数据
INSERT INTO `team_invitations` (`team_id`, `inviter_id`, `invitee_email`, `invitee_user_id`, `invitation_code`, `role`, `message`, `status`, `expires_at`) VALUES
(1, 2, 'newdev@example.com', NULL, 'inv_frontend_001', 'member', '欢迎加入前端开发团队！', 'pending', DATE_ADD(NOW(), INTERVAL 7 DAY)),
(2, 3, 'backend@example.com', NULL, 'inv_backend_001', 'member', '我们需要你的后端开发技能', 'pending', DATE_ADD(NOW(), INTERVAL 5 DAY)),
(3, 4, NULL, 6, 'inv_devops_001', 'member', '邀请你加入DevOps团队', 'accepted', DATE_ADD(NOW(), INTERVAL 3 DAY)),
(4, 5, 'tester@example.com', NULL, 'inv_qa_001', 'member', '加入我们的测试团队吧', 'expired', DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 12. 初始化团队加入申请数据
INSERT INTO `team_join_requests` (`team_id`, `user_id`, `message`, `status`, `reviewed_by`, `reviewed_at`, `review_message`) VALUES
(1, 4, '我想学习前端开发，希望能加入团队', 'pending', NULL, NULL, NULL),
(2, 5, '我有一些后端开发经验，希望能贡献力量', 'approved', 3, DATE_SUB(NOW(), INTERVAL 2 DAY), '欢迎加入！'),
(3, 2, '我对DevOps很感兴趣，想要学习相关技术', 'rejected', 4, DATE_SUB(NOW(), INTERVAL 1 DAY), '目前团队人员已满'),
(4, 6, '作为产品经理，我想了解测试流程', 'pending', NULL, NULL, NULL);

-- 13. 初始化操作日志数据
INSERT INTO `operation_logs` (`user_id`, `operation`, `resource_type`, `resource_id`, `description`, `ip_address`, `user_agent`, `status`, `duration`) VALUES
(1, 'user:create', 'user', '2', '创建用户: zhangsan', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'success', 150),
(1, 'user:create', 'user', '3', '创建用户: lisi', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'success', 120),
(2, 'team:create', 'team', '1', '创建团队: 前端开发团队', '192.168.1.100', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', 'success', 200),
(3, 'team:create', 'team', '2', '创建团队: 后端开发团队', '192.168.1.101', 'Mozilla/5.0 (X11; Linux x86_64)', 'success', 180),
(4, 'team:invite', 'team', '3', '邀请用户加入DevOps团队', '192.168.1.102', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'success', 100),
(2, 'user:login', 'user', '2', '用户登录', '192.168.1.100', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0)', 'success', 50),
(5, 'user:login', 'user', '5', '用户登录失败', '192.168.1.103', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', 'failure', 30);

SET FOREIGN_KEY_CHECKS = 1;