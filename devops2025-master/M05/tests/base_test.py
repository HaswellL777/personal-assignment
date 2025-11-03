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
测试基类
提供统一的认证管理和测试工具方法
"""

import requests
import json
import time
from datetime import datetime
from typing import Dict, List, Optional, Any
from token_manager import TokenManager

class BaseTest:
    def __init__(self, base_url: str = "http://localhost:8080"):
        self.base_url = base_url
        self.api_base = base_url
        self.token_manager = TokenManager(base_url)
        self.test_results: List[Dict] = []
        
        # 预定义的测试用户
        self.test_users = {
            'admin': {'username': 'admin', 'password': '123456'},
            'user': {'username': 'zhangsan', 'password': '123456'},
            'test_user': {'username': 'testuser', 'password': 'testpass123'}
        }
    
    def log_result(self, test_name: str, success: bool, message: str, details: Any = None):
        """记录测试结果"""
        result = {
            "test_name": test_name,
            "success": success,
            "message": message,
            "details": details,
            "timestamp": datetime.now().isoformat()
        }
        self.test_results.append(result)
        
        status = "✅ PASS" if success else "❌ FAIL"
        print(f"{status} {test_name}: {message}")
        if details and not success:
            print(f"   详细信息: {details}")
    
    def setup_authentication(self) -> bool:
        """设置认证，确保所有测试用户都能正常登录"""
        print("🔐 设置测试认证...")
        
        all_success = True
        for user_type, credentials in self.test_users.items():
            username = credentials['username']
            password = credentials['password']
            
            if self.token_manager.validate_token(username, password):
                self.log_result(
                    f"{user_type}用户认证",
                    True,
                    f"用户 {username} 认证成功"
                )
            else:
                self.log_result(
                    f"{user_type}用户认证",
                    False,
                    f"用户 {username} 认证失败"
                )
                all_success = False
        
        return all_success
    
    def get_auth_headers(self, user_type: str = 'admin') -> Dict[str, str]:
        """获取指定用户类型的认证头"""
        if user_type not in self.test_users:
            raise ValueError(f"未知的用户类型: {user_type}")
        
        credentials = self.test_users[user_type]
        return self.token_manager.get_auth_headers(
            credentials['username'], 
            credentials['password']
        )
    
    def login_user(self, username: str, password: str) -> bool:
        """用户登录方法，验证用户凭据并获取token"""
        try:
            # 使用token_manager验证用户凭据
            if self.token_manager.validate_token(username, password):
                print(f"✅ 用户 {username} 登录成功")
                return True
            else:
                print(f"❌ 用户 {username} 登录失败")
                return False
        except Exception as e:
            print(f"❌ 用户 {username} 登录异常: {str(e)}")
            return False
    
    def make_request(self, method: str, endpoint: str, username: str = None, 
                    user_type: str = None, data: Any = None, params: Dict = None, 
                    expect_success: bool = True, use_auth: bool = True, **kwargs) -> requests.Response:
        """发送API请求的统一方法"""
        # 支持两种参数方式：username直接指定用户名，或user_type指定用户类型
        if username:
            # 直接使用提供的用户名，密码从test_users中查找或使用默认密码
            password = "123456"  # 默认密码
            for user_data in self.test_users.values():
                if user_data['username'] == username:
                    password = user_data['password']
                    break
            credentials = {'username': username, 'password': password}
        elif user_type:
            if user_type not in self.test_users:
                raise ValueError(f"未知的用户类型: {user_type}")
            credentials = self.test_users[user_type]
        else:
            # 默认使用admin用户
            credentials = self.test_users['admin']
        url = f"{self.api_base}{endpoint}"
        
        # 处理请求参数
        request_kwargs = {}
        if data is not None:
            request_kwargs['json'] = data
        if params is not None:
            request_kwargs['params'] = params
        
        # 合并额外的kwargs，但排除use_auth参数
        filtered_kwargs = {k: v for k, v in kwargs.items() if k != 'use_auth'}
        request_kwargs.update(filtered_kwargs)
        
        if use_auth:
            # 使用认证请求
            response = self.token_manager.make_authenticated_request(
                method, url, credentials['username'], credentials['password'], **request_kwargs
            )
        else:
            # 不使用认证的普通请求
            response = self.token_manager.session.request(method, url, **request_kwargs)
        
        # 记录请求日志
        print(f"🌐 {method} {endpoint} -> {response.status_code}")
        
        return response
    
    def assert_response_success(self, response: requests.Response, test_name: str) -> bool:
        """断言响应成功"""
        try:
            if response.status_code == 200:
                result = response.json()
                if result.get('code') == 0:
                    self.log_result(test_name, True, "请求成功")
                    return True
                else:
                    self.log_result(
                        test_name, 
                        False, 
                        f"业务逻辑失败: {result.get('message')}", 
                        result
                    )
                    return False
            else:
                self.log_result(
                    test_name, 
                    False, 
                    f"HTTP请求失败: {response.status_code}", 
                    response.text
                )
                return False
        except Exception as e:
            self.log_result(
                test_name, 
                False, 
                f"响应解析失败: {str(e)}", 
                response.text
            )
            return False
    
    def assert_response_error(self, response: requests.Response, test_name: str, 
                            expected_code: int = None) -> bool:
        """断言响应错误"""
        try:
            if expected_code and response.status_code != expected_code:
                self.log_result(
                    test_name, 
                    False, 
                    f"期望状态码 {expected_code}，实际 {response.status_code}", 
                    response.text
                )
                return False
            
            if response.status_code >= 400:
                self.log_result(test_name, True, f"正确返回错误: {response.status_code}")
                return True
            else:
                result = response.json()
                if result.get('code') != 0:
                    self.log_result(test_name, True, f"正确返回业务错误: {result.get('message')}")
                    return True
                else:
                    self.log_result(test_name, False, "期望错误但请求成功", result)
                    return False
        except Exception as e:
            self.log_result(
                test_name, 
                False, 
                f"错误响应解析失败: {str(e)}", 
                response.text
            )
            return False
    
    def wait_for_server(self, timeout: int = 30) -> bool:
        """等待服务器启动"""
        print(f"⏳ 等待服务器启动 (超时: {timeout}秒)...")
        
        start_time = time.time()
        while time.time() - start_time < timeout:
            try:
                # 尝试访问登录接口来检查服务器状态
                response = requests.post(
                    f"{self.api_base}/auth/login",
                    json={"username": "test", "password": "test"},
                    timeout=5
                )
                # 只要能收到响应就说明服务器启动了，不管是否登录成功
                if response.status_code in [200, 400, 401, 403]:
                    print("✅ 服务器已启动")
                    return True
            except requests.exceptions.ConnectionError:
                # 连接错误说明服务器还没启动
                pass
            except:
                # 其他错误可能说明服务器已启动但有其他问题
                print("✅ 服务器已启动")
                return True
            time.sleep(1)
        
        print("❌ 服务器启动超时")
        return False
    
    def print_test_summary(self):
        """打印测试摘要"""
        if not self.test_results:
            print("📊 没有测试结果")
            return
        
        total_tests = len(self.test_results)
        passed_tests = sum(1 for result in self.test_results if result['success'])
        failed_tests = total_tests - passed_tests
        
        print("\n" + "="*80)
        print("📊 测试摘要")
        print("="*80)
        print(f"总测试数: {total_tests}")
        print(f"通过: {passed_tests} ✅")
        print(f"失败: {failed_tests} ❌")
        print(f"成功率: {(passed_tests/total_tests*100):.1f}%")
        
        if failed_tests > 0:
            print("\n❌ 失败的测试:")
            for result in self.test_results:
                if not result['success']:
                    print(f"  - {result['test_name']}: {result['message']}")
        
        print("="*80)
    
    def save_test_report(self, filename: str = None):
        """保存测试报告"""
        if filename is None:
            timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
            filename = f"test_report_{timestamp}.json"
        
        report = {
            "timestamp": datetime.now().isoformat(),
            "total_tests": len(self.test_results),
            "passed_tests": sum(1 for r in self.test_results if r['success']),
            "failed_tests": sum(1 for r in self.test_results if not r['success']),
            "results": self.test_results
        }
        
        with open(filename, 'w', encoding='utf-8') as f:
            json.dump(report, f, ensure_ascii=False, indent=2)
        
        print(f"📄 测试报告已保存: {filename}")
    
    def cleanup(self):
        """清理测试环境"""
        print("🧹 清理测试环境...")
        # 可以在这里添加清理逻辑，比如删除测试数据等
        pass