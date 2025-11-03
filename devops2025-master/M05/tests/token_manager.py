#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import sys
import os
# 设置标准输出编码为UTF-8
if sys.platform.startswith('win'):
    import codecs
    try:
        sys.stdout = codecs.getwriter('utf-8')(sys.stdout.detach())
    except AttributeError:
        # 在某些环境中stdout可能没有detach方法
        pass

"""
JWT Token管理器
确保测试过程中token始终有效，自动处理token过期和刷新
"""

import requests
import json
import time
from datetime import datetime, timedelta
import jwt
import threading
from typing import Dict, Optional, Callable

class TokenManager:
    def __init__(self, base_url: str = "http://localhost:8080"):
        self.base_url = base_url
        self.session = requests.Session()
        self.tokens: Dict[str, dict] = {}
        self.lock = threading.Lock()
        self.auto_refresh = True
        self.refresh_threshold = 300  # 5分钟内过期就刷新
        
    def _decode_token_payload(self, token: str) -> Optional[dict]:
        """解码JWT token获取payload信息（不验证签名）"""
        try:
            # 不验证签名，只解码payload
            payload = jwt.decode(token, options={"verify_signature": False})
            return payload
        except Exception as e:
            print(f"⚠️ Token解码失败: {str(e)}")
            return None
    
    def _is_token_expired(self, token: str) -> bool:
        """检查token是否过期"""
        payload = self._decode_token_payload(token)
        if not payload:
            return True
            
        exp = payload.get('exp')
        if not exp:
            return True
            
        # 检查是否在刷新阈值内
        current_time = time.time()
        return (exp - current_time) <= self.refresh_threshold
    
    def _login_user(self, username: str, password: str) -> Optional[str]:
        """用户登录获取token"""
        login_data = {
            "username": username,
            "password": password
        }
        
        try:
            response = self.session.post(
                f"{self.base_url}/api/v1/auth/login",
                json=login_data,
                headers={"Content-Type": "application/json"}
            )
            
            if response.status_code == 200:
                result = response.json()
                if result.get('code') == 0:
                    token = result.get('data', {}).get('token')
                    if token:
                        print(f"✅ 用户 {username} 登录成功")
                        return token
                    else:
                        print(f"❌ 用户 {username} 登录成功但未获取到token")
                else:
                    print(f"❌ 用户 {username} 登录失败: {result.get('message')}")
            else:
                print(f"❌ 用户 {username} 登录请求失败: {response.status_code}")
                
        except Exception as e:
            print(f"❌ 用户 {username} 登录异常: {str(e)}")
            
        return None
    
    def get_token(self, username: str, password: str, force_refresh: bool = False) -> Optional[str]:
        """获取有效的token，如果过期则自动刷新"""
        with self.lock:
            user_key = f"{username}:{password}"
            
            # 如果强制刷新或没有缓存的token
            if force_refresh or user_key not in self.tokens:
                token = self._login_user(username, password)
                if token:
                    self.tokens[user_key] = {
                        'token': token,
                        'username': username,
                        'password': password,
                        'created_at': datetime.now(),
                        'last_used': datetime.now()
                    }
                    return token
                return None
            
            # 检查现有token是否过期
            token_info = self.tokens[user_key]
            current_token = token_info['token']
            
            if self._is_token_expired(current_token):
                print(f"🔄 Token即将过期，为用户 {username} 刷新token...")
                new_token = self._login_user(username, password)
                if new_token:
                    token_info['token'] = new_token
                    token_info['created_at'] = datetime.now()
                    token_info['last_used'] = datetime.now()
                    return new_token
                else:
                    print(f"❌ 用户 {username} token刷新失败")
                    return None
            
            # 更新最后使用时间
            token_info['last_used'] = datetime.now()
            return current_token
    
    def get_auth_headers(self, username: str, password: str, force_refresh: bool = False) -> Dict[str, str]:
        """获取包含有效token的认证头"""
        token = self.get_token(username, password, force_refresh)
        if token:
            return {
                "Authorization": f"Bearer {token}",
                "Content-Type": "application/json"
            }
        return {"Content-Type": "application/json"}
    
    def make_authenticated_request(self, method: str, url: str, username: str, password: str, 
                                 max_retries: int = 2, **kwargs) -> requests.Response:
        """发送带认证的请求，自动处理token过期重试"""
        for attempt in range(max_retries + 1):
            # 获取认证头
            headers = kwargs.get('headers', {})
            auth_headers = self.get_auth_headers(username, password, force_refresh=(attempt > 0))
            headers.update(auth_headers)
            kwargs['headers'] = headers
            
            # 发送请求
            response = self.session.request(method, url, **kwargs)
            
            # 如果是401错误且还有重试次数，则刷新token重试
            if response.status_code == 401 and attempt < max_retries:
                print(f"🔄 收到401错误，尝试刷新token重试 (第{attempt + 1}次)")
                continue
            
            return response
        
        return response
    
    def validate_token(self, username: str, password: str) -> bool:
        """验证token是否有效"""
        token = self.get_token(username, password)
        if not token:
            return False
            
        # 尝试访问需要认证的接口
        try:
            response = self.make_authenticated_request(
                'GET', 
                f"{self.base_url}/api/v1/user/profile",
                username, 
                password
            )
            return response.status_code == 200
        except Exception as e:
            print(f"❌ Token验证异常: {str(e)}")
            return False
    
    def has_valid_token(self, username: str, password: str = "123456") -> bool:
        """检查是否有有效的token（别名方法）"""
        return self.validate_token(username, password)
    
    def set_token(self, username: str, token: str, password: str = "123456"):
        """手动设置用户的token（主要用于测试）"""
        with self.lock:
            user_key = f"{username}:{password}"
            self.tokens[user_key] = {
                'token': token,
                'timestamp': datetime.now(),
                'username': username
            }
            print(f"🔧 已为用户 {username} 手动设置token")
    
    def clear_token(self, username: str, password: str):
        """清除指定用户的token缓存"""
        with self.lock:
            user_key = f"{username}:{password}"
            if user_key in self.tokens:
                del self.tokens[user_key]
                print(f"🗑️ 已清除用户 {username} 的token缓存")
    
    def clear_all_tokens(self):
        """清除所有token缓存"""
        with self.lock:
            self.tokens.clear()
            print("🗑️ 已清除所有token缓存")
    
    def get_token_info(self, username: str, password: str) -> Optional[dict]:
        """获取token详细信息"""
        user_key = f"{username}:{password}"
        if user_key not in self.tokens:
            return None
            
        token_info = self.tokens[user_key].copy()
        token = token_info['token']
        payload = self._decode_token_payload(token)
        
        if payload:
            token_info['payload'] = payload
            token_info['expires_at'] = datetime.fromtimestamp(payload.get('exp', 0))
            token_info['is_expired'] = self._is_token_expired(token)
            
        return token_info
    
    def print_token_status(self):
        """打印所有token的状态"""
        print("\n📊 Token状态报告:")
        print("-" * 80)
        
        if not self.tokens:
            print("没有缓存的token")
            return
            
        for user_key, token_info in self.tokens.items():
            username = token_info['username']
            created_at = token_info['created_at'].strftime("%Y-%m-%d %H:%M:%S")
            last_used = token_info['last_used'].strftime("%Y-%m-%d %H:%M:%S")
            
            payload = self._decode_token_payload(token_info['token'])
            if payload:
                expires_at = datetime.fromtimestamp(payload.get('exp', 0)).strftime("%Y-%m-%d %H:%M:%S")
                is_expired = self._is_token_expired(token_info['token'])
                status = "❌ 已过期" if is_expired else "✅ 有效"
            else:
                expires_at = "未知"
                status = "❓ 无法解析"
            
            print(f"用户: {username}")
            print(f"  状态: {status}")
            print(f"  创建时间: {created_at}")
            print(f"  最后使用: {last_used}")
            print(f"  过期时间: {expires_at}")
            print()


# 全局token管理器实例
token_manager = TokenManager()

# 便捷函数
def get_auth_headers(username: str, password: str, force_refresh: bool = False) -> Dict[str, str]:
    """获取认证头的便捷函数"""
    return token_manager.get_auth_headers(username, password, force_refresh)

def make_authenticated_request(method: str, url: str, username: str, password: str, **kwargs) -> requests.Response:
    """发送认证请求的便捷函数"""
    return token_manager.make_authenticated_request(method, url, username, password, **kwargs)

def validate_token(username: str, password: str) -> bool:
    """验证token的便捷函数"""
    return token_manager.validate_token(username, password)