# JWT认证机制介绍

## Token

- Token在计算机身份认证中是令牌（临时）的意思，在词法分析中是标记的意思。一般作为邀请、登录系统使用。
- Token 是在服务端产生的。如果前端使用用户名/密码向服务端请求认证，服务端认证成功，那么在服务端会返回 Token 给前端。前端可以在每次请求的时候带上 Token 证明自己的合法地位。
- 如果这个 Token 在服务端持久化（比如存入数据库），那它就是一个永久的身份令牌。
- 通常Token 是有过期时间的。
- JWT(JSON Web Token) 是一个开放标准(RFC 7519)，它定义了一种紧凑的、自包含的方式，用于作为JSON对象在各方之间安全地传输信息。该信息可以被验证和信任，因为它是数字签名的。

## JWT

`https://jwt.io/`

```code
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1laWRlbnRpZmllciI6IjAwMiIsImh0dHA6Ly9zY2hlbWFzLnhtbHNvYXAub3JnL3dzLzIwMDUvMDUvaWRlbnRpdHkvY2xhaW1zL25hbWUiOiLmnY7lm5siLCJuYmYiOjE1NjU5MjMxMjIsImV4cCI6MTU2NTkyMzI0MiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo1NDIxNCIsImF1ZCI6Imh0dHA6Ly9sb2NhbGhvc3Q6NTQyMTUifQ.Mrta7nftmfXeo_igBVd4rl2keMmm0rg0WkqRXoVAeik
```

JSON Web Token由三部分组成，它们之间用圆点(.)连接。这三部分分别是：

### Header（头部）
由两部分组成：token的类型（"JWT"）和算法名称

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

### Payload（负载）
Payload是JWT用于信息存储部分，其中包含了许多种的声明（claims）。可以自定义多个声明添加到Payload中，系统也提供了一些默认的类型：

- **iss (issuer)**：签发人
- **exp (expiration time)**：过期时间
- **sub (subject)**：主题
- **aud (audience)**：受众
- **nbf (Not Before)**：生效时间
- **iat (Issued At)**：签发时间
- **jti (JWT ID)**：编号

```json
{
  "sub": "1234567890",
  "name": "John Doe",
  "iat": 1516239022,
  "exp": 1516242622
}
```

### Signature（签名）
签名信息，防止数据被篡改

```
HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret)
```

> **安全提示**：不要在JWT的payload或header中放置敏感信息，除非它们是加密的。

## JWT结构图

```
Header.Payload.Signature
```

JWT的完整结构如下：
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9  ←  Header (Base64编码)
.
eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ  ←  Payload (Base64编码)
.
SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c  ←  Signature
```

## 认证流程

1. **用户登录**：用户首先通过登录，到认证服务器获取一个Token。
2. **携带Token**：在访问应用服务器的API的时候，将获取到的Token放置在请求的Header中。
3. **验证Token**：应用服务器验证该Token，通过后返回对应的结果。

### 详细认证流程图

![JWT认证流程](../images/JWT.svg)

```
客户端                    认证服务器                应用服务器
  |                          |                        |
  |  1. 用户名/密码登录        |                        |
  |------------------------->|                        |
  |                          |                        |
  |  2. 返回JWT Token         |                        |
  |<-------------------------|                        |
  |                          |                        |
  |  3. 携带Token访问API                               |
  |-------------------------------------------------------->|
  |                          |                        |
  |                          |  4. 验证Token           |
  |                          |<-----------------------|
  |                          |                        |
  |                          |  5. 返回验证结果        |
  |                          |----------------------->|
  |                          |                        |
  |  6. 返回API响应                                    |
  |<--------------------------------------------------------|
```

## 应用场景

### 单体应用
- 对于小型项目，可能认证服务和应用服务在一起。本例通过分开的方式来实现，使我们能更好的了解二者之间的认证流程。

### 分布式系统
- 对于复杂一些的项目，可能存在多个应用服务，用户获取到的Token可以在多个分布式服务中被认证，这也是JWT的优势之一。

### 微服务架构
- 在微服务架构中，JWT可以在各个服务之间传递用户身份信息，无需每次都查询用户数据库。
- 每个微服务都可以独立验证JWT的有效性，提高了系统的可扩展性。

## JWT的优势

1. **无状态**：服务器不需要保存会话信息
2. **跨域支持**：可以在不同域名间传递
3. **移动端友好**：适合移动应用的认证机制
4. **分布式系统支持**：多个服务可以共享同一个Token
5. **性能优异**：避免了频繁的数据库查询

## JWT的注意事项

1. **Token大小**：JWT包含用户信息，比传统session id要大
2. **安全性**：需要使用HTTPS传输，防止Token被截获
3. **过期处理**：需要合理设置过期时间，并提供刷新机制
4. **撤销困难**：JWT在过期前很难主动撤销
5. **敏感信息**：不要在Payload中存储敏感信息

## 在本系统中的应用

在我们的用户权限管理系统中，JWT用于：

- 用户登录后生成JWT Token
- 前端在每次API请求中携带Token
- 后端验证Token的有效性和用户权限
- 支持Token刷新机制，提升用户体验

相关API接口请参考：[API接口设计文档](./03_API接口设计.md#认证相关接口)