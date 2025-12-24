# 多阶段构建 - 构建阶段
FROM docker.m.daocloud.io/library/eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# 创建 Maven settings.xml 配置镜像仓库（配置多个镜像源作为备选）
RUN mkdir -p /root/.m2 && \
    printf '<?xml version="1.0" encoding="UTF-8"?>\n<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">\n  <mirrors>\n    <mirror>\n      <id>aliyunmaven</id>\n      <mirrorOf>central</mirrorOf>\n      <name>Aliyun Maven</name>\n      <url>https://maven.aliyun.com/repository/public</url>\n    </mirror>\n  </mirrors>\n  <profiles>\n    <profile>\n      <id>default</id>\n      <repositories>\n        <repository>\n          <id>central</id>\n          <url>https://maven.aliyun.com/repository/public</url>\n          <releases><enabled>true</enabled></releases>\n          <snapshots><enabled>false</enabled></snapshots>\n        </repository>\n      </repositories>\n    </profile>\n  </profiles>\n  <activeProfiles>\n    <activeProfile>default</activeProfile>\n  </activeProfiles>\n</settings>\n' > /root/.m2/settings.xml

# 复制 Maven 配置文件
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# 复制源代码
COPY src ./src

# 构建应用（跳过测试以加快构建，依赖会在构建时自动下载）
RUN chmod +x mvnw && ./mvnw clean package -DskipTests -B

# 运行阶段 - 使用更小的 JRE 镜像
FROM docker.m.daocloud.io/library/eclipse-temurin:17-jre-alpine

# 安装必要的运行时工具
RUN apk add --no-cache curl

WORKDIR /app

# 创建非 root 用户
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# 从构建阶段复制 JAR 文件
COPY --from=builder /app/target/*.jar app.jar

# 设置文件权限
RUN chown -R appuser:appgroup /app

# 切换到非 root 用户
USER appuser

# 暴露端口
EXPOSE 8080

# JVM 优化参数
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+UseContainerSupport"

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/ping || exit 1

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
