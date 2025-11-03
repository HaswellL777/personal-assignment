# Spring Boot Demo 项目

基于SpringBoot 3.5.5的演示项目，提供基础的Web API功能和数据库操作示例。

## 项目特性

- 🚀 **基础Web API**: 提供RESTful风格的API接口
- 💾 **数据库集成**: 集成MyBatis进行数据库操作
- 📝 **统一响应**: 统一的API响应格式和异常处理
- 🔧 **开发工具**: 集成Lombok简化代码开发
- 📊 **实体管理**: 基础的应用实体CRUD操作

## 技术栈

- **后端框架**: Spring Boot 3.5.5
- **数据库**: MySQL 8.0
- **ORM框架**: MyBatis 3.0.3
- **工具类**: Lombok 1.18.32
- **构建工具**: Maven 3.x
- **JDK版本**: Java 17

## 项目结构

```
M05/
├── api-springboot/                 # SpringBoot后端项目
│   ├── src/main/java/
│   │   └── com/example/demo/
│   │       ├── DemoApplication.java              # 启动类
│   │       ├── HelloController.java              # 基础控制器
│   │       ├── common/                           # 公共类
│   │       │   ├── ApiResponse.java              # 统一响应结果
│   │       │   ├── BusinessException.java        # 业务异常
│   │       │   └── GlobalExceptionHandler.java   # 全局异常处理
│   │       ├── config/                           # 配置类
│   │       │   └── WebConfig.java                # Web配置
│   │       └── app/                              # 应用模块
│   │           ├── AppController.java            # 应用控制器
│   │           ├── AppRawController.java         # 原始控制器
│   │           ├── entity/                       # 实体类
│   │           │   └── AppEntity.java            # 应用实体
│   │           ├── mapper/                       # 数据访问层
│   │           │   └── AppMapper.java            # 应用Mapper
│   │           └── vo/                           # 视图对象
│   │               └── AppVO.java                # 应用VO
│   ├── src/main/resources/
│   │   ├── application.yml                       # 应用配置
│   │   └── mapper/                               # MyBatis映射文件
│   │       └── AppMapper.xml                     # 应用Mapper映射
│   └── pom.xml                                   # Maven配置
├── tests/                                        # API测试目录
│   ├── test_api.py                               # Python API自动化测试程序
│   ├── requirements.txt                          # Python依赖包
│   └── README.md                                 # 测试说明文档
├── sql/
│   └── init.sql                                  # 数据库初始化脚本
├── 01_需求分析.md                                # 需求分析文档
├── 02_数据库设计.md                              # 数据库设计文档
├── 03_API接口设计.md                             # API接口设计文档
└── README.md                                     # 项目说明文档
```

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 2. 数据库配置

本项目使用远程MySQL数据库，配置信息如下：

- **数据库地址**: 101.201.127.215
- **端口**: 3306
- **数据库名**: devops2025
- **用户名**: devops2025
- **密码**: sspku2025

### 3. 配置文件

`src/main/resources/application.yml` 中的数据库配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://101.201.127.215:3306/devops2025?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8&allowPublicKeyRetrieval=true&connectTimeout=60000&socketTimeout=60000&autoReconnect=true
    username: devops2025
    password: sspku2025
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      connection-timeout: 60000
      idle-timeout: 600000
      max-lifetime: 1800000
      maximum-pool-size: 10
      minimum-idle: 5
```

### 4. 启动应用

```bash
cd M05/api-springboot
mvn spring-boot:run
```

### 5. 访问应用

- 应用地址: http://localhost:8080/usermanagement
- API文档: http://localhost:8080/usermanagement/doc.html
- Druid监控: http://localhost:8080/usermanagement/druid

## API接口

### 公开接口
- `GET /api/ping` - 健康检查，返回"pong"

## 数据库设计

当前项目为简单的Demo项目，暂未包含具体的数据库表结构。

如需添加数据库表，可在 `sql/init.sql` 文件中定义表结构和初始数据。

## 安全特性

当前项目为基础Demo，包含以下基础安全特性：
- 全局异常处理
- 统一响应格式
- 基础的输入验证框架

## 开发指南

### 代码规范
- 使用Lombok减少样板代码
- 统一异常处理
- 统一响应格式

### 开发流程
1. 在现有代码基础上进行开发
2. 遵循现有的包结构和命名规范
3. 使用统一的ApiResponse格式返回数据

### 测试

#### 单元测试
```bash
# 运行Java单元测试
mvn test
```

#### API自动化测试
项目提供了完整的Python API自动化测试程序，位于 `tests/` 目录：

```bash
# 进入测试目录
cd tests

# 安装Python依赖
pip install -r requirements.txt

# 运行API测试（确保Spring Boot应用已启动）
python test_api.py

# 或指定自定义服务器地址
python test_api.py http://localhost:8080
```

测试程序会自动执行以下测试流程：
- 服务器健康检查
- 用户注册测试
- 用户登录测试
- 获取用户信息
- 更新用户信息
- 修改密码测试
- 新密码登录验证
- 安全性测试（无效token）

详细说明请参考 `tests/README.md`

## 部署说明

### 本地开发部署

1. 打包应用：
```bash
mvn clean package
```

2. 运行JAR文件：
```bash
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

### 开发环境运行

直接在IDE中运行 `DemoApplication.java` 主类，或使用Maven命令：
```bash
mvn spring-boot:run
```

## 常见问题

### Q: 如何修改数据库连接配置？
A: 修改 `src/main/resources/application.yml` 文件中的数据源配置。

### Q: 如何添加新的API接口？
A: 在对应的Controller中添加方法，使用统一的ApiResponse格式返回数据。

### Q: 项目启动失败怎么办？
A: 检查数据库连接配置是否正确，确保MySQL服务正在运行。

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

- 项目地址: https://github.com/devops2025/user-management
- 问题反馈: https://github.com/devops2025/user-management/issues
- 邮箱: devops2025@example.com