# HTTP协议基础

## 目录
- [HTTP是什么](#http是什么)
- [URL统一资源定位符](#url统一资源定位符)
- [HTTP请求协议](#http请求协议)
- [HTTP请求方法](#http请求方法)
- [HTTP响应状态码](#http响应状态码)
- [Cookie机制](#cookie机制)
- [HTTP与HTTPS](#http与https)
- [在本系统中的应用](#在本系统中的应用)

## HTTP是什么

**超文本传输协议**（英文：HyperText Transfer Protocol，缩写：HTTP）是用于从万维网（WWW:World Wide Web）服务器传输超文本到本地浏览器的传送协议。

### 核心特点

- **客户端-服务器模型**：HTTP是一个客户端终端（用户）和服务器端（网站）请求和应答的标准
- **请求/响应协议**：HTTP协议定义Web客户端如何从Web服务器请求Web页面，以及服务器如何把Web页面传送给客户端
- **基于TCP/IP**：HTTP是一个基于TCP/IP通信协议来传递数据，同时也是一个属于应用层的面向对象的协议
- **无状态协议**：每个请求都是独立的，服务器不会保留之前请求的信息

### 工作原理

HTTP协议采用了请求/响应模型：

1. **客户端请求**：客户端向服务器发送一个请求报文，包含：
   - 请求的方法（GET、POST等）
   - URL（统一资源定位符）
   - 协议版本
   - 请求头部
   - 请求数据

2. **服务器响应**：服务器以一个状态行作为响应，包含：
   - 协议的版本
   - 成功或者错误代码
   - 服务器信息
   - 响应头部
   - 响应数据

## URL统一资源定位符

**URL**（Uniform Resource Locator）统一资源定位符，是对可以从互联网上得到的资源的位置和访问方法的一种简洁的表示，是互联网上标准资源的地址。

### URL结构

```
协议://主机名:端口号/路径?查询参数#锚点
```

**示例**：
```
https://api.example.com:8080/users/123?include=profile#section1
```

- **协议**：https
- **主机名**：api.example.com
- **端口号**：8080
- **路径**：/users/123
- **查询参数**：include=profile
- **锚点**：section1

## HTTP请求协议

HTTP请求由请求行、请求头部、空行和请求数据四个部分组成。

![HTTP请求头示例](../images/request_header.svg)

### 请求格式

```
GET /api/users HTTP/1.1
Host: api.example.com
User-Agent: Mozilla/5.0
Accept: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "username": "admin",
  "password": "123456"
}
```

## HTTP请求方法

### HTTP 1.0 方法
- **GET**：请求指定的页面信息，并返回实体主体
- **POST**：向指定资源提交数据进行处理请求
- **HEAD**：类似于GET请求，只不过返回的响应中没有具体的内容

### HTTP 1.1 新增方法
- **OPTIONS**：允许客户端查看服务器的性能
- **PUT**：从客户端向服务器传送的数据取代指定的文档的内容
- **DELETE**：请求服务器删除指定的页面
- **TRACE**：回显服务器收到的请求，主要用于测试或诊断
- **CONNECT**：HTTP/1.1协议中预留给能够将连接改为管道方式的代理服务器

### 常用方法详解

| 方法 | 描述 | 幂等性 | 安全性 |
|------|------|--------|--------|
| GET | 获取资源 | ✓ | ✓ |
| POST | 创建资源 | ✗ | ✗ |
| PUT | 更新/创建资源 | ✓ | ✗ |
| DELETE | 删除资源 | ✓ | ✗ |
| PATCH | 部分更新资源 | ✗ | ✗ |

## HTTP响应状态码

所有HTTP响应的第一行都是状态行，依次是当前HTTP版本号，3位数字组成的状态代码，以及描述状态的短语，彼此由空格分隔。

### 状态码分类

状态代码的第一个数字代表当前响应的类型：

#### 1xx 消息
请求已被服务器接收，继续处理
- **100 Continue**：继续请求
- **101 Switching Protocols**：切换协议

#### 2xx 成功
请求已成功被服务器接收、理解、并接受
- **200 OK**：请求成功
- **201 Created**：已创建
- **204 No Content**：无内容
- **206 Partial Content**：部分内容

#### 3xx 重定向
需要后续操作才能完成这一请求
- **301 Moved Permanently**：永久重定向
- **302 Found**：临时重定向
- **304 Not Modified**：未修改

#### 4xx 客户端错误
请求含有词法错误或者无法被执行
- **400 Bad Request**：错误请求
- **401 Unauthorized**：未授权
- **403 Forbidden**：禁止访问
- **404 Not Found**：未找到
- **405 Method Not Allowed**：方法不允许

#### 5xx 服务器错误
服务器在处理某个正确请求时发生错误
- **500 Internal Server Error**：内部服务器错误
- **502 Bad Gateway**：错误网关
- **503 Service Unavailable**：服务不可用

## Cookie机制

### 什么是Cookie？

Cookie是一种存储在用户浏览器上的小型文本文件，用于存储会话信息、用户偏好、身份验证信息等。Cookie是由Web服务器设置的，并可由该服务器在用户之后的请求中进行访问和修改。

### Cookie的组成

Cookie主要包含以下内容：

- **名称**：Cookie的唯一标识
- **值**：存储的数据内容
- **过期时间**：Cookie的过期时间，过了这个时间Cookie就会被自动删除
- **路径**：Cookie在哪些路径下有效
- **域**：Cookie在哪个域名下有效
- **安全标志**：标示Cookie是否仅通过安全（HTTPS）连接发送

### Cookie示例

```http
Set-Cookie: sessionId=abc123; Path=/; Domain=.example.com; Secure; HttpOnly; Max-Age=3600
```

### Cookie的特点

- **有限存储**：每个域名下的Cookie数量和大小都是有限制的
- **安全性**：未经加密的Cookie不应存储敏感信息，因为它们可以通过中间人攻击被截获
- **跨域限制**：Cookie默认不能跨域，只能被设置它的域名访问

### Cookie vs Session vs JWT

| 特性 | Cookie | Session | JWT |
|------|--------|---------|-----|
| 存储位置 | 客户端 | 服务端 | 客户端 |
| 安全性 | 中等 | 高 | 高 |
| 扩展性 | 差 | 差 | 好 |
| 性能 | 好 | 中等 | 好 |

## HTTP与HTTPS

### HTTPS的优势

- **加密传输**：所有数据都经过SSL/TLS加密
- **身份验证**：确保连接到正确的服务器
- **数据完整性**：防止数据在传输过程中被篡改

### 在API开发中的重要性

在现代Web应用中，特别是涉及用户认证和敏感数据传输时，HTTPS是必需的：

```javascript
// 安全的API调用
fetch('https://api.example.com/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    username: 'user',
    password: 'password'
  })
})
```

## 在本系统中的应用

### API设计原则

我们的DevOps管理系统严格遵循HTTP协议标准：

1. **RESTful设计**：使用标准HTTP方法
   - GET /api/users - 获取用户列表
   - POST /api/users - 创建用户
   - PUT /api/users/{id} - 更新用户
   - DELETE /api/users/{id} - 删除用户

2. **状态码使用**：
   - 成功操作返回适当的2xx状态码
   - 客户端错误返回4xx状态码
   - 服务器错误返回5xx状态码

3. **请求头管理**：
   - Content-Type: application/json
   - Authorization: Bearer {JWT_TOKEN}
   - Accept: application/json

4. **安全考虑**：
   - 所有API调用使用HTTPS
   - 敏感信息通过JWT而非Cookie传输
   - 实施CORS策略

### 与JWT的结合

HTTP协议为JWT认证提供了完美的载体：

```http
POST /api/auth/login HTTP/1.1
Host: api.example.com
Content-Type: application/json

{
  "username": "admin",
  "password": "password"
}

HTTP/1.1 200 OK
Content-Type: application/json

{
  "code": 0,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "admin"
    }
  }
}
```

## 总结

HTTP协议是现代Web应用的基础，理解HTTP的工作原理对于开发高质量的Web应用至关重要。结合JWT认证机制，我们可以构建安全、高效、可扩展的API服务。

## 参考资源

- [HTTP协议官方规范](https://tools.ietf.org/html/rfc7231)
- [MDN HTTP文档](https://developer.mozilla.org/zh-CN/docs/Web/HTTP)
- [RESTful API设计指南](https://restfulapi.net/)