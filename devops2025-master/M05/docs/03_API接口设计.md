# 用户权限管理系统 - API接口文档

## 🔐 权限体系设计

### 权限模块

系统按功能模块划分权限：

- **USER_MANAGEMENT**: 用户管理
- **ROLE_MANAGEMENT**: 角色管理  
- **PERMISSION_MANAGEMENT**: 权限管理
- **SYSTEM_MANAGEMENT**: 系统管理
- **DATA_MANAGEMENT**: 数据管理

### 操作类型

每个模块支持以下操作：

- **CREATE**: 创建
- **READ**: 查看
- **UPDATE**: 更新
- **DELETE**: 删除
- **MANAGE**: 管理（包含所有操作）

### 权限命名规范

权限名称格式：`模块:操作`

例如：
- `USER_MANAGEMENT:CREATE` - 创建用户
- `ROLE_MANAGEMENT:READ` - 查看角色
- `PERMISSION_MANAGEMENT:MANAGE` - 管理权限

## 📊 统一响应格式

### 成功响应格式
```json
{
  "code": 0,
  "message": "操作成功",
  "data": {},
  "timestamp": 1634567890123
}
```

### 错误响应格式
```json
{
  "code": 400,
  "message": "请求参数错误",
  "data": null,
  "timestamp": 1634567890123
}
```

### 响应状态码说明
| HTTP状态码 | 业务状态码 | 说明 | 描述 |
|-----------|-----------|------|------|
| 200 | 0 | SUCCESS | 操作成功 |
| 400 | 400 | BAD_REQUEST | 请求参数错误 |
| 401 | 401 | UNAUTHORIZED | 未认证或认证失败 |
| 403 | 403 | FORBIDDEN | 权限不足 |
| 404 | 404 | NOT_FOUND | 资源不存在 |
| 409 | 409 | CONFLICT | 资源冲突 |
| 422 | 422 | VALIDATION_ERROR | 数据验证失败 |
| 500 | 500 | INTERNAL_ERROR | 服务器内部错误 |

---

# 📚 API接口详细文档

## 🔑 认证相关接口

### 用户注册

**接口地址**: `POST /api/v1/auth/register`

**控制器**: `AuthController.register()`

**请求参数**:
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePass123!",
  "confirmPassword": "SecurePass123!",
  "phone": "13800138000",
  "nickname": "John"
}
```

**参数验证**:
- `username`: 必填，3-50字符，只能包含字母、数字、下划线
- `email`: 必填，有效邮箱格式
- `password`: 必填，8-20字符，包含字母和数字
- `confirmPassword`: 必填，与password一致
- `phone`: 可选，11位手机号
- `nickname`: 可选，1-100字符

**响应示例**:
```json
{
  "code": 0,
  "message": "注册成功",
  "data": {
    "id": 123,
    "username": "john_doe",
    "email": "john@example.com",
    "nickname": "John",
    "status": 1,
    "createdAt": "2024-01-15T10:30:00"
  },
  "timestamp": 1634567890123
}
```

**错误响应**:
```json
{
  "code": 409,
  "message": "用户名已存在",
  "data": null,
  "timestamp": 1634567890123
}
```

### 用户登录

**接口地址**: `POST /api/v1/auth/login`

**控制器**: `AuthController.login()`

**请求参数**:
```json
{
  "username": "john_doe",
  "password": "SecurePass123!"
}
```

**参数验证**:
- `username`: 必填，用户名或邮箱
- `password`: 必填，密码

**响应示例**:
```json
{
  "code": 0,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 3600,
    "user": {
      "id": 123,
      "username": "john_doe",
      "email": "john@example.com",
      "nickname": "John",
      "roles": ["USER"]
    }
  },
  "timestamp": 1634567890123
}
```

### 用户登出

**接口地址**: `POST /api/v1/auth/logout`

**控制器**: `AuthController.logout()`

**请求头**: `Authorization: Bearer {token}`

**响应示例**:
```json
{
  "code": 0,
  "message": "登出成功",
  "data": null,
  "timestamp": 1634567890123
}
```

## 👥 用户管理接口

### 获取用户列表

**接口地址**: `GET /api/v1/users`

**控制器**: `UserController.getUsers()`

**权限要求**: `USER_MANAGEMENT:READ`

**请求参数**:
- `page`: 页码，默认1
- `size`: 每页大小，默认10
- `keyword`: 搜索关键词（可选）
- `status`: 用户状态（可选）
- `roleId`: 角色ID（可选）

**响应示例**:
```json
{
  "code": 0,
  "message": "获取成功",
  "data": {
    "content": [
      {
        "id": 123,
        "username": "john_doe",
        "email": "john@example.com",
        "nickname": "John",
        "phone": "13800138000",
        "status": 1,
        "roles": [
          {
            "id": 2,
            "name": "USER",
            "displayName": "普通用户"
          }
        ],
        "createdAt": "2024-01-15T10:30:00",
        "lastLoginAt": "2024-01-20T14:20:00"
      }
    ],
    "totalElements": 100,
    "totalPages": 10,
    "currentPage": 1,
    "size": 10
  },
  "timestamp": 1634567890123
}
```

### 创建用户

**接口地址**: `POST /api/v1/users`

**控制器**: `UserController.createUser()`

**权限要求**: `USER_MANAGEMENT:CREATE`

**请求参数**:
```json
{
  "username": "new_user",
  "email": "newuser@example.com",
  "password": "TempPass123!",
  "nickname": "New User",
  "phone": "13900139000",
  "roleIds": [2]
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "用户创建成功",
  "data": {
    "id": 124,
    "username": "new_user",
    "email": "newuser@example.com",
    "nickname": "New User",
    "phone": "13900139000",
    "status": 1,
    "roles": [
      {
        "id": 2,
        "name": "USER",
        "displayName": "普通用户"
      }
    ],
    "createdAt": "2024-01-21T09:15:00"
  },
  "timestamp": 1634567890123
}
```

### 更新用户

**接口地址**: `PUT /api/v1/users/{id}`

**控制器**: `UserController.updateUser()`

**权限要求**: `USER_MANAGEMENT:UPDATE`

**请求参数**:
```json
{
  "nickname": "Updated Name",
  "phone": "13700137000",
  "status": 1
}
```

### 删除用户

**接口地址**: `DELETE /api/v1/users/{id}`

**控制器**: `UserController.deleteUser()`

**权限要求**: `USER_MANAGEMENT:DELETE`

### 为用户分配角色

**接口地址**: `POST /api/v1/users/{id}/roles`

**控制器**: `UserController.assignRoles()`

**权限要求**: `USER_MANAGEMENT:UPDATE`

**请求参数**:
```json
{
  "roleIds": [2, 3]
}
```

## 🎭 角色管理接口

### 获取角色列表

**接口地址**: `GET /api/v1/roles`

**控制器**: `RoleController.getRoles()`

**权限要求**: `ROLE_MANAGEMENT:READ`

**响应示例**:
```json
{
  "code": 0,
  "message": "获取成功",
  "data": [
    {
      "id": 1,
      "name": "SUPER_ADMIN",
      "displayName": "超级管理员",
      "description": "系统最高权限",
      "level": 1,
      "status": 1,
      "permissions": [
        {
          "id": 1,
          "name": "USER_MANAGEMENT:MANAGE",
          "displayName": "用户管理",
          "module": "USER_MANAGEMENT"
        }
      ],
      "createdAt": "2024-01-01T00:00:00"
    }
  ],
  "timestamp": 1634567890123
}
```

### 创建角色

**接口地址**: `POST /api/v1/roles`

**控制器**: `RoleController.createRole()`

**权限要求**: `ROLE_MANAGEMENT:CREATE`

**请求参数**:
```json
{
  "name": "CUSTOM_ROLE",
  "displayName": "自定义角色",
  "description": "自定义角色描述",
  "level": 5
}
```

### 为角色分配权限

**接口地址**: `POST /api/v1/roles/{id}/permissions`

**控制器**: `RoleController.assignPermissions()`

**权限要求**: `ROLE_MANAGEMENT:UPDATE`

**请求参数**:
```json
{
  "permissionIds": [1, 2, 3]
}
```

## 🔒 权限管理接口

### 获取权限列表

**接口地址**: `GET /api/v1/permissions`

**控制器**: `PermissionController.getPermissions()`

**权限要求**: `PERMISSION_MANAGEMENT:READ`

**响应示例**:
```json
{
  "code": 0,
  "message": "获取成功",
  "data": [
    {
      "id": 1,
      "name": "USER_MANAGEMENT:CREATE",
      "displayName": "创建用户",
      "description": "创建新用户的权限",
      "module": "USER_MANAGEMENT",
      "operation": "CREATE",
      "resourceType": "USER",
      "status": 1,
      "createdAt": "2024-01-01T00:00:00"
    }
  ],
  "timestamp": 1634567890123
}
```

### 获取权限模块列表

**接口地址**: `GET /api/v1/permissions/modules`

**控制器**: `PermissionController.getModules()`

**权限要求**: `PERMISSION_MANAGEMENT:READ`

**响应示例**:
```json
{
  "code": 0,
  "message": "获取成功",
  "data": [
    {
      "module": "USER_MANAGEMENT",
      "displayName": "用户管理",
      "permissions": [
        {
          "id": 1,
          "name": "USER_MANAGEMENT:CREATE",
          "displayName": "创建用户",
          "operation": "CREATE"
        }
      ]
    }
  ],
  "timestamp": 1634567890123
}
```

### 创建权限

**接口地址**: `POST /api/v1/permissions`

**控制器**: `PermissionController.createPermission()`

**权限要求**: `PERMISSION_MANAGEMENT:CREATE`

**请求参数**:
```json
{
  "name": "CUSTOM_MODULE:READ",
  "displayName": "查看自定义模块",
  "description": "查看自定义模块的权限",
  "module": "CUSTOM_MODULE",
  "operation": "READ",
  "resourceType": "CUSTOM"
}
```

## 🏢 团队管理接口

### 获取团队列表

**接口地址**: `GET /api/v1/teams`

**控制器**: `TeamController.getTeams()`

### 创建团队

**接口地址**: `POST /api/v1/teams`

**控制器**: `TeamController.createTeam()`

**请求参数**:
```json
{
  "name": "开发团队",
  "description": "负责产品开发",
  "visibility": "private"
}
```

### 邀请成员

**接口地址**: `POST /api/v1/teams/{id}/invitations`

**控制器**: `TeamController.inviteMember()`

**请求参数**:
```json
{
  "email": "member@example.com",
  "role": "member",
  "message": "邀请您加入我们的团队"
}
```

### 处理邀请

**接口地址**: `POST /api/v1/teams/invitations/{code}/respond`

**控制器**: `TeamController.respondInvitation()`

**请求参数**:
```json
{
  "action": "accept"
}
```

## ⚙️ 系统管理接口

### 获取系统配置

**接口地址**: `GET /api/v1/system/configs`

**控制器**: `SystemController.getConfigs()`

**权限要求**: `SYSTEM_MANAGEMENT:READ`

### 更新系统配置

**接口地址**: `PUT /api/v1/system/configs`

**控制器**: `SystemController.updateConfigs()`

**权限要求**: `SYSTEM_MANAGEMENT:UPDATE`

### 初始化系统数据

**接口地址**: `POST /api/v1/system/init`

**控制器**: `SystemController.initSystem()`

**权限要求**: `SYSTEM_MANAGEMENT:MANAGE`

### 获取系统信息

**接口地址**: `GET /api/v1/system/info`

**控制器**: `SystemController.getSystemInfo()`

## 📁 文件上传接口

### 上传头像

**接口地址**: `POST /api/v1/upload/avatar`

**控制器**: `FileController.uploadAvatar()`

**请求参数**: `multipart/form-data`
- `file`: 图片文件（必填）

**响应示例**:
```json
{
  "code": 0,
  "message": "上传成功",
  "data": {
    "url": "http://localhost:8080/uploads/avatars/123456789.jpg",
    "filename": "123456789.jpg",
    "size": 102400
  },
  "timestamp": 1634567890123
}
```

## 📊 统计分析接口

### 获取用户统计

**接口地址**: `GET /api/v1/statistics/users`

**控制器**: `StatisticsController.getUserStats()`

**权限要求**: `DATA_MANAGEMENT:READ`

**响应示例**:
```json
{
  "code": 0,
  "message": "获取成功",
  "data": {
    "totalUsers": 1000,
    "activeUsers": 850,
    "newUsersToday": 15,
    "newUsersThisWeek": 89,
    "usersByRole": {
      "SUPER_ADMIN": 1,
      "ADMIN": 5,
      "USER": 994
    }
  },
  "timestamp": 1634567890123
}
```

### 获取团队统计

**接口地址**: `GET /api/v1/statistics/teams`

**控制器**: `StatisticsController.getTeamStats()`

**权限要求**: `DATA_MANAGEMENT:READ`


