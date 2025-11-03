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
用户管理模块测试脚本
测试用户资料管理相关功能
"""

import requests
import json
import time
from datetime import datetime
import os
from base_test import BaseTest

class UserManagementTester(BaseTest):
    def __init__(self):
        super().__init__()
        self.test_results = []
        
    def log_result(self, test_name, success, message, details=None):
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
    
    def setup_authentication(self):
        """设置认证，登录测试用户"""
        print("🔐 设置认证...")
        
        # 登录admin用户
        try:
            success = self.login_user("admin", "123456")
            if success:
                self.log_result(
                    "管理员认证设置",
                    True,
                    "管理员登录成功，获取到Token"
                )
            else:
                self.log_result(
                    "管理员认证设置",
                    False,
                    "管理员登录失败"
                )
                return False
                
        except Exception as e:
            self.log_result(
                "管理员认证设置",
                False,
                f"管理员认证设置异常: {str(e)}"
            )
            return False
        
        # 登录普通用户zhangsan
        try:
            success = self.login_user("zhangsan", "123456")
            if success:
                self.log_result(
                    "普通用户认证设置",
                    True,
                    "普通用户登录成功，获取到Token"
                )
                return True
            else:
                self.log_result(
                    "普通用户认证设置",
                    False,
                    "普通用户登录失败"
                )
                return False
                
        except Exception as e:
            self.log_result(
                "普通用户认证设置",
                False,
                f"普通用户认证设置异常: {str(e)}"
            )
            return False
    
    def test_get_user_profile(self):
        """测试获取用户资料"""
        print("\n📋 测试获取用户资料...")
        
        for username in ['admin', 'zhangsan']:
            if not self.token_manager.has_valid_token(username):
                continue
                
            try:
                response = self.make_request(
            "GET",
            "/api/v1/users/profile",
                    username=username
                )
                
                if response.status_code == 200:
                    result = response.json()
                    if result.get('code') == 0:
                        profile_data = result.get('data')
                        self.log_result(
                            f"获取用户资料 - {username}",
                            True,
                            f"成功获取用户资料，用户ID: {profile_data.get('id')}",
                            profile_data
                        )
                    else:
                        self.log_result(
                            f"获取用户资料 - {username}",
                            False,
                            f"获取用户资料失败: {result.get('message')}",
                            result
                        )
                else:
                    self.log_result(
                        f"获取用户资料 - {username}",
                        False,
                        f"请求失败: {response.status_code}",
                        response.text
                    )
                    
            except Exception as e:
                self.log_result(
                    f"获取用户资料 - {username}",
                    False,
                    f"请求异常: {str(e)}"
                )
    
    def test_update_user_profile(self):
        """测试更新用户资料"""
        print("\n✏️ 测试更新用户资料...")
        
        # 测试数据
        update_data_sets = [
            {
                "user": "admin",
                "data": {
                    "nickname": "超级管理员",
                    "bio": "系统超级管理员账户",
                    "avatarUrl": "https://example.com/avatar/admin.jpg"
                },
                "desc": "管理员资料更新"
            },
            {
                "user": "zhangsan",
                "data": {
                    "nickname": "张三同学",
                    "bio": "我是张三，很高兴认识大家！",
                    "avatarUrl": "https://example.com/avatar/zhangsan.jpg"
                },
                "desc": "普通用户资料更新"
            }
        ]
        
        for update_set in update_data_sets:
            username = update_set['user']
            if not self.token_manager.has_valid_token(username):
                continue
                
            try:
                response = self.make_request(
                    "PUT",
                    "/api/v1/users/profile",
                    json=update_set['data'],
                    username=username
                )
                
                if response.status_code == 200:
                    result = response.json()
                    if result.get('code') == 0:
                        updated_user = result.get('data')
                        self.log_result(
                            f"更新用户资料 - {update_set['desc']}",
                            True,
                            f"资料更新成功，昵称: {updated_user.get('nickname')}",
                            updated_user
                        )
                    else:
                        self.log_result(
                            f"更新用户资料 - {update_set['desc']}",
                            False,
                            f"资料更新失败: {result.get('message')}",
                            result
                        )
                else:
                    self.log_result(
                        f"更新用户资料 - {update_set['desc']}",
                        False,
                        f"请求失败: {response.status_code}",
                        response.text
                    )
                    
            except Exception as e:
                self.log_result(
                    f"更新用户资料 - {update_set['desc']}",
                    False,
                    f"请求异常: {str(e)}"
                )
    
    def test_change_password(self):
        """测试修改密码"""
        print("\n🔑 测试修改密码...")
        
        # 测试数据
        password_change_tests = [
            {
                "user": "zhangsan",
                "data": {
                    "currentPassword": "123456",
                    "newPassword": "newpass123",
                    "confirmPassword": "newpass123"
                },
                "desc": "正常密码修改",
                "should_succeed": True
            },
            {
                "user": "zhangsan",
                "data": {
                    "currentPassword": "wrongpass",
                    "newPassword": "newpass456",
                    "confirmPassword": "newpass456"
                },
                "desc": "错误的当前密码",
                "should_succeed": False
            },
            {
                "user": "zhangsan",
                "data": {
                    "currentPassword": "newpass123",
                    "newPassword": "finalpass",
                    "confirmPassword": "differentpass"
                },
                "desc": "新密码和确认密码不一致",
                "should_succeed": False
            }
        ]
        
        for test_case in password_change_tests:
            username = test_case['user']
            if not self.token_manager.has_valid_token(username):
                continue
                
            try:
                response = self.make_request(
            "PUT",
            "/api/v1/users/password",
                    json=test_case['data'],
                    username=username
                )
                
                if response.status_code == 200:
                    result = response.json()
                    success = result.get('code') == 0
                    
                    if success == test_case['should_succeed']:
                        self.log_result(
                            f"修改密码测试 - {test_case['desc']}",
                            True,
                            f"测试结果符合预期: {result.get('message')}",
                            result
                        )
                    else:
                        self.log_result(
                            f"修改密码测试 - {test_case['desc']}",
                            False,
                            f"测试结果不符合预期: {result.get('message')}",
                            result
                        )
                else:
                    # 对于某些错误情况，可能返回非200状态码
                    if not test_case['should_succeed']:
                        self.log_result(
                            f"修改密码测试 - {test_case['desc']}",
                            True,
                            f"正确拒绝了无效请求: {response.status_code}",
                            response.text
                        )
                    else:
                        self.log_result(
                            f"修改密码测试 - {test_case['desc']}",
                            False,
                            f"请求失败: {response.status_code}",
                            response.text
                        )
                        
            except Exception as e:
                self.log_result(
                    f"修改密码测试 - {test_case['desc']}",
                    False,
                    f"请求异常: {str(e)}"
                )
    
    def test_unauthorized_access(self):
        """测试未授权访问"""
        print("\n🚫 测试未授权访问...")
        
        # 测试无Token访问
        try:
            response = self.make_request("GET", "/api/v1/users/profile", use_auth=False)
            
            if response.status_code == 401 or response.status_code == 403:
                self.log_result(
                    "无Token访问测试",
                    True,
                    f"正确拒绝了无Token访问: {response.status_code}"
                )
            else:
                self.log_result(
                    "无Token访问测试",
                    False,
                    f"未正确拒绝无Token访问: {response.status_code}",
                    response.text
                )
                
        except Exception as e:
            self.log_result(
                "无Token访问测试",
                False,
                f"请求异常: {str(e)}"
            )
        
        # 测试无效Token访问
        try:
            # 临时设置无效token
            self.token_manager.set_token("test_invalid", "invalid_token_here")
            
            response = self.make_request(
            "GET",
            "/api/v1/users/profile",
                username="test_invalid"
            )
            
            if response.status_code == 401 or response.status_code == 403:
                self.log_result(
                    "无效Token访问测试",
                    True,
                    f"正确拒绝了无效Token访问: {response.status_code}"
                )
            else:
                self.log_result(
                    "无效Token访问测试",
                    False,
                    f"未正确拒绝无效Token访问: {response.status_code}",
                    response.text
                )
                
        except Exception as e:
            self.log_result(
                "无效Token访问测试",
                False,
                f"请求异常: {str(e)}"
            )
    
    def generate_report(self):
        """生成测试报告"""
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        report_file = f"reports/user_management_test_report_{timestamp}.json"
        
        # 统计结果
        total_tests = len(self.test_results)
        passed_tests = sum(1 for result in self.test_results if result['success'])
        failed_tests = total_tests - passed_tests
        success_rate = (passed_tests / total_tests * 100) if total_tests > 0 else 0
        
        # 生成报告
        report = {
            "test_summary": {
                "total_tests": total_tests,
                "passed_tests": passed_tests,
                "failed_tests": failed_tests,
                "success_rate": f"{success_rate:.1f}%",
                "test_time": datetime.now().isoformat()
            },
            "test_results": self.test_results
        }
        
        # 保存报告
        with open(report_file, 'w', encoding='utf-8') as f:
            json.dump(report, f, ensure_ascii=False, indent=2)
        
        # 打印统计信息
        print("\n" + "="*60)
        print("📊 用户管理模块测试结果统计:")
        print(f"总测试数: {total_tests}")
        print(f"通过: {passed_tests} ✅")
        print(f"失败: {failed_tests} ❌")
        print(f"成功率: {success_rate:.1f}%")
        
        if failed_tests > 0:
            print(f"\n❌ 失败的测试:")
            for result in self.test_results:
                if not result['success']:
                    print(f"  - {result['test_name']}: {result['message']}")
        
        print(f"\n📄 详细测试报告已保存到: {report_file}")
        
        return report_file
    
    def run_all_tests(self):
        """运行所有测试"""
        print("🚀 开始用户管理模块测试...")
        print("="*60)
        
        # 设置认证
        if not self.setup_authentication():
            print("❌ 认证设置失败，无法继续测试")
            return
        
        # 运行各项测试
        self.test_get_user_profile()
        self.test_update_user_profile()
        self.test_change_password()
        self.test_unauthorized_access()
        
        # 生成报告
        self.generate_report()

if __name__ == "__main__":
    tester = UserManagementTester()
    tester.run_all_tests()