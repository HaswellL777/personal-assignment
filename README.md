# 智能体创作平台

基于 Spring Boot 3 + Vue 3 的前后端分离权限管理系统，实现完整的 RBAC 权限模型和 AI 对话功能，适用于课程演示和学习。

## 功能特性

### 用户认证
- JWT Token 认证（24小时有效期）
- 用户注册/登录/登出
- 密码加密存储（BCrypt）
- Token 自动验证和刷新

### RBAC 权限管理
- 用户-角色-权限 三层模型
- 基于角色的动态菜单
- 方法级权限验证（@PreAuthorize）
- 管理员/普通用户权限区分

### AI 智能助手
- 集成 OpenAI 兼容 API
- 支持流式对话响应（SSE）
- 实时聊天交互界面

### 系统管理
- 用户信息管理
- 个人资料编辑
- 密码修改

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Java 17, Spring Boot 3.5.5, Spring Security, MyBatis, MySQL 8 |
| 前端 | Vue 3, Vite, Element Plus, Vue Router |
| 认证 | JWT (jjwt 0.11.5) |
| 文档 | Swagger/OpenAPI 3 (springdoc) |
| 部署 | Docker, Docker Compose, Nginx |

## 快速开始

### Docker 部署（推荐）

```bash
# 1. 克隆项目
git clone <repository-url>
cd personal-assignment

# 2. 配置环境变量
cp .env.example .env

# 3. 编辑 .env 文件，设置必要的环境变量
#    - JWT_SECRET: JWT密钥（至少32字符）
#    - LLM_API_KEY: AI API密钥（可选）

# 4. 启动服务
docker-compose up -d

# 5. 查看服务状态
docker-compose ps
```

访问地址：
- 前端界面: http://localhost
- API 文档: http://localhost:8080/swagger-ui.html

### 本地开发

**环境要求**: Java 17+, Node.js 18+, MySQL 8.0+

```bash
# 1. 创建数据库
mysql -u root -p
CREATE DATABASE devops2025 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 2. 启动后端
cd backend
export JWT_SECRET="your-secret-key-at-least-32-characters"
./mvnw spring-boot:run

# 3. 启动前端（新终端）
cd frontend
npm install
npm run dev
```

开发环境地址：
- 前端: http://localhost:5173
- 后端: http://localhost:8080
- API文档: http://localhost:8080/swagger-ui.html

## 测试账号

| 用户名 | 密码 | 角色 | 权限说明 |
|-------|------|------|----------|
| admin | admin123 | ADMIN | 管理员，可见所有菜单，可执行管理员操作 |
| user | user123 | USER | 普通用户，仅可见基础菜单 |

## 项目结构

```
personal-assignment/
├── backend/                          # Spring Boot 后端
│   ├── src/main/java/com/example/demo/
│   │   ├── admin/                   # 管理员操作
│   │   ├── auth/                    # 认证模块（登录/注册）
│   │   ├── common/                  # 公共组件（响应/异常）
│   │   ├── config/                  # 配置类（CORS/Security）
│   │   ├── llm/                     # AI助手服务
│   │   ├── menu/                    # 动态菜单
│   │   ├── role/                    # 角色管理
│   │   ├── security/                # JWT安全配置
│   │   └── user/                    # 用户管理
│   │       ├── controller/
│   │       ├── dto/                 # 数据传输对象
│   │       ├── entity/
│   │       ├── mapper/
│   │       └── service/
│   ├── src/main/resources/
│   │   ├── mapper/                  # MyBatis XML
│   │   └── application.yml
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                         # Vue 3 前端
│   ├── src/
│   │   ├── components/              # 公共组件（Layout）
│   │   ├── pages/                   # 页面组件
│   │   ├── router/                  # 路由配置
│   │   └── utils/                   # 工具函数
│   ├── Dockerfile
│   └── nginx.conf
├── init-db/                          # 数据库初始化脚本
│   ├── 01-create-tables.sql         # 表结构
│   └── 02-insert-data.sql           # 初始数据
├── docker-compose.yml
├── .env.example
└── README.md
```

## API 接口

### 认证接口
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | /api/v1/auth/login | 用户登录 | 公开 |
| POST | /api/v1/auth/register | 用户注册 | 公开 |
| POST | /api/v1/auth/logout | 用户登出 | 认证 |

### 用户接口
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/v1/users/profile | 获取个人信息 | 认证 |
| PUT | /api/v1/users/profile | 更新个人信息 | 认证 |
| PUT | /api/v1/users/password | 修改密码 | 认证 |

### 菜单接口
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/v1/menus | 获取用户菜单 | 认证 |

### AI助手接口
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | /api/v1/llm/chat | 同步对话 | 认证 |
| POST | /api/v1/llm/chat/stream | 流式对话 | 认证 |

### 管理员接口
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | /api/v1/admin/delete-user | 删除用户（演示） | ADMIN |

## 环境变量配置

### 必需配置
| 变量 | 说明 | 示例 |
|------|------|------|
| JWT_SECRET | JWT签名密钥（至少32字符） | `my-super-secret-key-at-least-32-chars` |

### 数据库配置
| 变量 | 说明 | 默认值 |
|------|------|--------|
| DB_HOST | MySQL 主机 | localhost |
| DB_PORT | MySQL 端口 | 3306 |
| DB_NAME | 数据库名 | devops2025 |
| DB_USERNAME | 数据库用户 | root |
| DB_PASSWORD | 数据库密码 | root |

### AI服务配置（可选）
| 变量 | 说明 | 默认值 |
|------|------|--------|
| LLM_API_URL | LLM API 地址 | https://api.openai.com/v1/chat/completions |
| LLM_API_KEY | LLM API 密钥 | - |
| LLM_MODEL | 使用的模型 | gpt-3.5-turbo |

### Docker 部署配置
| 变量 | 说明 | 默认值 |
|------|------|--------|
| MYSQL_ROOT_PASSWORD | MySQL root 密码 | rootpassword |
| FRONTEND_PORT | 前端端口 | 80 |
| BACKEND_PORT | 后端端口 | 8080 |

## 数据库设计

### 用户表 (users)
- 用户基本信息、认证信息、状态管理

### 角色表 (roles)
- 系统角色定义（SUPER_ADMIN, ADMIN, USER）

### 权限表 (permissions)
- 细粒度权限定义

### 关联表
- user_roles: 用户-角色关联
- role_permissions: 角色-权限关联

## 安全特性

- JWT Token 认证
- 密码 BCrypt 加密
- CORS 跨域配置
- 全局异常处理
- 异常日志记录
- 权限注解验证

## 常见问题

### 1. JWT_SECRET 未设置
```
错误: JWT_SECRET 环境变量未设置
解决: export JWT_SECRET="your-secret-key-at-least-32-characters"
```

### 2. 数据库连接失败
```
确保 MySQL 服务已启动，数据库已创建
Docker 部署时会自动初始化数据库
```

### 3. AI 功能不可用
```
需要配置 LLM_API_KEY 环境变量
支持 OpenAI 兼容的 API 服务
```

## 许可证

MIT License

---

**版本**: 1.0.0
**最后更新**: 2025-12-24
