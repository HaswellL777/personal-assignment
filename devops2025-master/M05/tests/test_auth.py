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
        # 在某些环境中，detach()方法可能不存在
        pass

"""
认证授权功能测试脚本
测试用户登录、注册、权限验证等功能
"""

import requests
import json
from datetime import datetime
from base_test import BaseTest

class AuthTester(BaseTest):
    def __init__(self):
        super().__init__()
        self.test_user_id = None
        
    def test_user_registration(self):
        """测试用户注册"""
        print("📝 测试用户注册...")
        
        # 生成唯一用户名
        timestamp = datetime.now().strftime("%H%M%S")
        test_username = f"testuser_{timestamp}"
        test_email = f"test_{timestamp}@example.com"
        
        register_data = {
            "username": test_username,
            "email": test_email,
            "password": "testpass123"
        }
        
        response = self.make_request(
            "POST",
            "/api/v1/auth/register",
            json=register_data,
            use_auth=False  # 注册不需要认证
        )
        
        print(f"注册状态码: {response.status_code}")
        
        if response.status_code == 200:
            result = response.json()
            print(f"注册响应code: {result.get('code')}")
            
            if result.get("code") == 0:
                print("✅ 用户注册成功")
                self.test_user_id = result.get("data", {}).get("id")
                return True, test_username, "testpass123"
            else:
                print(f"❌ 用户注册失败: {result.get('message')}")
                return False, None, None
        else:
            print(f"❌ 用户注册请求失败，状态码: {response.status_code}")
            return False, None, None
    
    def test_user_login(self, username, password):
        """测试用户登录"""
        print(f"🔐 测试用户登录: {username}...")
        
        login_data = {
            "username": username,
            "password": password
        }
        
        response = self.make_request(
            "POST",
            "/api/v1/auth/login",
            json=login_data,
            use_auth=False  # 登录不需要认证
        )
        
        print(f"登录状态码: {response.status_code}")
        
        if response.status_code == 200:
            result = response.json()
            print(f"登录响应code: {result.get('code')}")
            
            if result.get("code") == 0:
                token = result.get("data", {}).get("token")
                if token:
                    # 使用TokenManager管理token
                    self.token_manager.set_token(username, token)
                    print("✅ 用户登录成功，获取到Token")
                    return True
                else:
                    print("❌ 登录成功但未获取到Token")
                    return False
            else:
                print(f"❌ 用户登录失败: {result.get('message')}")
                return False
        else:
            print(f"❌ 用户登录请求失败，状态码: {response.status_code}")
            return False
    
    def test_admin_login(self):
        """测试管理员登录"""
        print("🔐 测试管理员登录...")
        
        login_data = {
            "username": "admin",
            "password": "123456"
        }
        
        response = self.make_request(
            "POST",
            "/api/v1/auth/login",
            json=login_data,
            use_auth=False  # 登录不需要认证
        )
        
        print(f"管理员登录状态码: {response.status_code}")
        
        if response.status_code == 200:
            result = response.json()
            print(f"管理员登录响应code: {result.get('code')}")
            
            if result.get("code") == 0:
                token = result.get("data", {}).get("token")
                if token:
                    # 使用TokenManager管理token
                    self.token_manager.set_token("admin", token)
                    print("✅ 管理员登录成功，获取到Token")
                    return True
                else:
                    print("❌ 管理员登录成功但未获取到Token")
                    return False
            else:
                print(f"❌ 管理员登录失败: {result.get('message')}")
                return False
        else:
            print(f"❌ 管理员登录请求失败，状态码: {response.status_code}")
            return False
    
    def test_token_validation(self, username="admin"):
        """测试Token验证"""
        print("🔍 测试Token验证...")
        
        if not self.token_manager.has_valid_token(username):
            print("❌ 没有可用的Token进行验证")
            return False
        
        # 尝试访问需要认证的接口
        response = self.make_request(
            "GET",
            "/api/v1/users/profile",
            username=username
        )
        
        print(f"Token验证状态码: {response.status_code}")
        
        if response.status_code == 200:
            result = response.json()
            print(f"Token验证响应code: {result.get('code')}")
            
            if result.get("code") == 0:
                print("✅ Token验证成功")
                return True
            else:
                print(f"❌ Token验证失败: {result.get('message')}")
                return False
        else:
            print(f"❌ Token验证请求失败，状态码: {response.status_code}")
            return False
    
    def test_invalid_token(self):
        """测试无效Token"""
        print("🚫 测试无效Token...")
        
        # 临时设置无效token
        invalid_username = "invalid_user"
        self.token_manager.set_token(invalid_username, "invalid_token_123")
        
        response = self.make_request(
            "GET",
            "/api/v1/users/profile",
            username=invalid_username
        )
        
        print(f"无效Token状态码: {response.status_code}")
        
        if response.status_code == 401 or response.status_code == 403:
            print("✅ 无效Token正确被拒绝")
            return True
        elif response.status_code == 200:
            result = response.json()
            if result.get("code") != 0:
                print("✅ 无效Token正确被拒绝")
                return True
            else:
                print("❌ 无效Token未被正确拒绝")
                return False
        else:
            print(f"❌ 无效Token测试异常，状态码: {response.status_code}")
            return False
    
    def test_logout(self, username="admin"):
        """测试用户登出"""
        print("🚪 测试用户登出...")
        
        if not self.token_manager.has_valid_token(username):
            print("❌ 没有可用的Token进行登出")
            return False
        
        response = self.make_request(
            "POST",
            "/api/v1/auth/logout",
            username=username
        )
        
        print(f"登出状态码: {response.status_code}")
        
        if response.status_code == 200:
            result = response.json()
            print(f"登出响应code: {result.get('code')}")
            
            if result.get("code") == 0:
                print("✅ 用户登出成功")
                # 清除Token
                self.token_manager.clear_token(username, "123456")
                return True
            else:
                print(f"❌ 用户登出失败: {result.get('message')}")
                return False
        else:
            print(f"❌ 用户登出请求失败，状态码: {response.status_code}")
            return False
    
    def test_access_after_logout(self, username="admin"):
        """测试登出后访问"""
        print("🔒 测试登出后访问...")
        
        # 尝试使用已登出的用户访问
        response = self.make_request(
            "GET",
            "/api/v1/users/profile",
            username=username,
            use_auth=False  # 不使用认证，模拟登出后的状态
        )
        
        print(f"登出后访问状态码: {response.status_code}")
        
        if response.status_code == 401 or response.status_code == 403:
            print("✅ 登出后正确拒绝访问")
            return True
        elif response.status_code == 200:
            result = response.json()
            if result.get("code") != 0:
                print("✅ 登出后正确拒绝访问")
                return True
            else:
                print("❌ 登出后仍可访问，Token未正确失效")
                return False
        else:
            print(f"❌ 登出后访问测试异常，状态码: {response.status_code}")
            return False
    
    def run_all_tests(self):
        """运行所有认证测试"""
        print("开始认证授权功能测试")
        print("="*50)
        
        test_results = []
        
        # 1. 测试用户注册
        success, username, password = self.test_user_registration()
        test_results.append(("用户注册", success))
        
        if not success:
            print("❌ 用户注册失败，使用默认测试用户")
            username = "zhangsan"
            password = "123456"
        
        # 2. 测试用户登录
        success = self.test_user_login(username, password)
        test_results.append(("用户登录", success))
        
        # 3. 测试Token验证
        if success:
            success = self.test_token_validation(username)
            test_results.append(("Token验证", success))
        
        # 4. 测试无效Token
        success = self.test_invalid_token()
        test_results.append(("无效Token拒绝", success))
        
        # 5. 测试管理员登录
        admin_success = self.test_admin_login()
        test_results.append(("管理员登录", admin_success))
        
        # 6. 测试登出（使用管理员账户）
        if admin_success:
            success = self.test_logout("admin")
            test_results.append(("用户登出", success))
            
            # 7. 测试登出后访问
            success = self.test_access_after_logout("admin")
            test_results.append(("登出后访问控制", success))
        else:
            test_results.append(("用户登出", False))
            test_results.append(("登出后访问控制", False))
        
        # 输出测试结果
        print("\n" + "="*50)
        print("认证授权测试结果:")
        print("="*50)
        
        passed = 0
        total = len(test_results)
        
        for test_name, result in test_results:
            status = "✅ 通过" if result else "❌ 失败"
            print(f"{test_name}: {status}")
            if result:
                passed += 1
        
        print(f"\n测试总结: {passed}/{total} 通过")
        
        if passed == total:
            print("✅ 所有认证授权测试通过")
            return True
        else:
            print("❌ 部分认证授权测试失败")
            return False

def main():
    """主函数"""
    print("认证授权功能测试脚本")
    print("测试用户登录、注册、权限验证等功能")
    print("="*60)
    
    tester = AuthTester()
    success = tester.run_all_tests()
    
    print("\n" + "="*60)
    if success:
        print("认证授权功能测试完成")
        print("所有测试执行完成")
    else:
        print("认证授权测试未完全成功")
    
    return 0 if success else 1

if __name__ == "__main__":
    exit_code = main()
    sys.exit(exit_code)