from fastapi import FastAPI, HTTPException, Header
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import Optional
import time

app = FastAPI(title="Mock Auth API", version="1.0.0")

# Allow local Vite dev server
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:5173"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


class LoginRequest(BaseModel):
    username: str
    password: str


@app.get("/api/ping")
def ping():
    return "pong"


@app.post("/api/v1/auth/login")
def login(req: LoginRequest):
    # Simple mock rule: password must be '123456'
    if not req.username or not req.password:
        raise HTTPException(status_code=400, detail="用户名或密码不能为空")
    if req.password != "123456":
        raise HTTPException(status_code=401, detail="用户名或密码错误")

    token = f"mock-token-{req.username}"
    now = int(time.time() * 1000)
    return {
        "code": 0,
        "message": "OK",
        "data": {
            "token": token,
            "user": {
                "username": req.username,
                "nickname": req.username,
            },
        },
        "timestamp": now,
    }


@app.get("/api/v1/menus")
def get_menus(authorization: str | None = Header(default=None)):
    # Require Bearer token
    if not authorization or not authorization.lower().startswith("bearer "):
        raise HTTPException(status_code=401, detail="未授权，请先登录")
    token = authorization.split(" ", 1)[1]
    username = token.replace("mock-token-", "") if token.startswith("mock-token-") else "user"

    base_menus = [
        {"name": "仪表盘", "path": "/home/dashboard", "icon": "dashboard"},
        {"name": "应用", "path": "/home/apps", "icon": "apps"},
        {"name": "个人中心", "path": "/home/profile", "icon": "user"},
    ]
    admin_extra = [
        {"name": "用户管理", "path": "/home/admin/users", "icon": "users"},
        {"name": "角色管理", "path": "/home/admin/roles", "icon": "roles"},
        {"name": "权限管理", "path": "/home/admin/permissions", "icon": "shield"},
    ]

    menus = base_menus.copy()
    if username == "admin":
        menus.extend(admin_extra)

    now = int(time.time() * 1000)
    return {
        "code": 0,
        "message": "OK",
        "data": menus,
        "timestamp": now,
    }


@app.get("/api/v1/user/profile")
def get_user_profile(authorization: str | None = Header(default=None)):
    # Require Bearer token
    if not authorization or not authorization.lower().startswith("bearer "):
        raise HTTPException(status_code=401, detail="未授权，请先登录")
    token = authorization.split(" ", 1)[1]
    username = token.replace("mock-token-", "") if token.startswith("mock-token-") else "user"

    # Mock user profile data based on username
    profile_data = {
        "username": username,
        "nickname": f"{username}的昵称",
        "email": f"{username}@example.com",
        "phone": "138****8888",
        "department": "技术部",
        "position": "高级工程师" if username == "admin" else "工程师",
        "joinDate": "2023-01-15",
        "lastLogin": "2024-12-30 10:30:00",
        "avatar": f"https://api.dicebear.com/7.x/avataaars/svg?seed={username}",
        "status": "active"
    }

    now = int(time.time() * 1000)
    return {
        "code": 0,
        "message": "OK",
        "data": profile_data,
        "timestamp": now,
    }


@app.post("/api/v1/admin/delete-user")
def delete_user(authorization: str | None = Header(default=None)):
    """模拟管理员删除用户的操作 - 需要管理员权限"""
    # 验证token
    if not authorization or not authorization.lower().startswith("bearer "):
        raise HTTPException(status_code=401, detail="未授权，请先登录")
    
    token = authorization.split(" ", 1)[1]
    username = token.replace("mock-token-", "") if token.startswith("mock-token-") else "user"
    
    # 权限检查：只有admin用户可以执行此操作
    if username != "admin":
        raise HTTPException(
            status_code=403, 
            detail="权限不足：您没有执行此操作的权限。此操作仅限管理员用户。"
        )
    
    # 模拟删除用户操作成功
    now = int(time.time() * 1000)
    return {
        "code": 0,
        "message": "用户删除成功",
        "data": {
            "operation": "delete_user",
            "operator": username,
            "timestamp": now
        },
        "timestamp": now,
    }


@app.get("/api/v1/admin/system-config")
def get_system_config(authorization: str | None = Header(default=None)):
    """模拟获取系统配置的操作 - 需要管理员权限"""
    # 验证token
    if not authorization or not authorization.lower().startswith("bearer "):
        raise HTTPException(status_code=401, detail="未授权，请先登录")
    
    token = authorization.split(" ", 1)[1]
    username = token.replace("mock-token-", "") if token.startswith("mock-token-") else "user"
    
    # 权限检查：只有admin用户可以访问系统配置
    if username != "admin":
        raise HTTPException(
            status_code=403, 
            detail="访问被拒绝：您没有查看系统配置的权限。此功能仅对管理员开放。"
        )
    
    # 模拟返回系统配置
    now = int(time.time() * 1000)
    return {
        "code": 0,
        "message": "获取系统配置成功",
        "data": {
            "systemName": "智能体创作平台",
            "version": "1.0.0",
            "maxUsers": 1000,
            "enableDebug": True,
            "databaseUrl": "postgresql://localhost:5432/aiplatform",
            "secretKey": "***hidden***"
        },
        "timestamp": now,
    }


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)