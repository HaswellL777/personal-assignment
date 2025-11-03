
**启动数据库环境**

```bash
# 进入 Docker 目录
cd docker-mysql

# 启动 MySQL + phpMyAdmin
docker compose up -d

# 验证服务状态
docker compose ps
```


**启动后端服务**

```bash
# 进入后端项目目录
cd api-springboot

# 安装依赖并启动
mvn clean install
mvn spring-boot:run
```



**启动前端服务**

```bash
# 进入前端项目目录
cd web-vue

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```


**启动小程序项目**

* 打开微信开发者工具
* 导入 `wx-miniprogram` 项目
* 使用测试号进行开发
