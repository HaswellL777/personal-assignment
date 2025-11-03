#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import sys
import os
# 设置标准输出编码为UTF-8
if sys.platform.startswith('win'):
    import codecs
    sys.stdout = codecs.getwriter('utf-8')(sys.stdout.detach())

"""
角色管理功能测试脚本
测试角色CRUD操作、权限分配等功能
"""

import requests
import json
import sys
from datetime import datetime
from base_test import BaseTest
from token_manager import TokenManager

class RoleManagementTester(BaseTest):
    def __init__(self):
        super().__init__()
        self.created_role_id = None
        
    def login_admin(self):
        """管理员登录"""
        print("🔐 管理员登录...")
        
        success = self.login_user("admin", "123456")
        if success:
            print("✅ 管理员登录成功，获取到Token")
        else:
            print("❌ 管理员登录失败")
        return success
    
    def get_roles_list(self):
        """获取角色列表"""
        print("\n📋 获取角色列表...")
        
        response = self.make_request("GET", "/api/roles", username="admin")
        print(f"获取角色列表状态码: {response.status_code}")
        
        if response.status_code == 200:
            result = response.json()
            if result.get("code") == 0:
                roles_data = result.get("data", {})
                if isinstance(roles_data, dict) and "content" in roles_data:
                    roles = roles_data["content"]
                    print(f"✅ 成功获取角色列表，共 {len(roles)} 个角色")
                    for i, role in enumerate(roles[:3], 1):  # 显示前3个角色
                        print(f"角色{i}: ID={role.get('id')}, 名称={role.get('name')}, 显示名={role.get('displayName')}")
                    return True
                elif isinstance(roles_data, list):
                    print(f"✅ 成功获取角色列表，共 {len(roles_data)} 个角色")
                    for i, role in enumerate(roles_data[:3], 1):  # 显示前3个角色
                        print(f"角色{i}: ID={role.get('id')}, 名称={role.get('name')}, 显示名={role.get('displayName')}")
                    return True
                else:
                    print(f"❌ 角色列表数据格式异常: {roles_data}")
                    return False
            else:
                print(f"❌ 获取角色列表失败: {result.get('message')}")
                return False
        else:
            print(f"❌ 获取角色列表请求失败，状态码: {response.status_code}")
            return False
    
    def create_role(self):
        """创建角色"""
        print("\n➕ 创建新角色...")
        
        timestamp = datetime.now().strftime("%H%M%S")
        role_data = {
            "name": f"TEST_ROLE_{timestamp}",
            "description": f"这是一个测试角色，创建时间: {datetime.now()}",
            "status": 1
        }
        
        response = self.make_request(
            "POST",
            "/api/roles",
            json=role_data,
            username="admin"
        )
        
        print(f"创建角色状态码: {response.status_code}")
        
        if response.status_code == 200:
            result = response.json()
            if result.get("code") == 0:
                created_role = result.get("data")
                self.created_role_id = created_role.get("id")
                print(f"✅ 成功创建角色: {created_role.get('name')} (ID: {self.created_role_id})")
                return True
            else:
                print(f"❌ 创建角色失败: {result.get('message')}")
                return False
        else:
            print(f"❌ 创建角色请求失败，状态码: {response.status_code}")
            return False
    
    def get_role_detail(self):
        """获取角色详情"""
        if not self.created_role_id:
            print("❌ 没有可查询的角色ID")
            return False
            
        print(f"\n🔍 获取角色详情 (ID: {self.created_role_id})...")
        
        response = self.make_request("GET", f"/api/roles/{self.created_role_id}", username="admin")
        print(f"获取角色详情状态码: {response.status_code}")
        
        if response.status_code == 200:
            result = response.json()
            if result.get("code") == 0:
                role = result.get("data")
                print(f"✅ 成功获取角色详情: {role.get('name')} - {role.get('displayName')}")
                return True
            else:
                print(f"❌ 获取角色详情失败: {result.get('message')}")
                return False
        else:
            print(f"❌ 获取角色详情请求失败，状态码: {response.status_code}")
            return False
    
    def update_role(self):
        """更新角色"""
        if not self.created_role_id:
            print("❌ 没有可更新的角色ID")
            return False
            
        print(f"\n✏️ 更新角色 (ID: {self.created_role_id})...")
        
        update_data = {
            "name": f"UPDATED_ROLE_{datetime.now().strftime('%H%M%S')}",
            "description": f"角色已更新，更新时间: {datetime.now()}",
            "status": 1
        }
        
        response = self.make_request(
            "PUT",
            f"/api/roles/{self.created_role_id}",
            json=update_data,
            username="admin"
        )
        
        print(f"更新角色状态码: {response.status_code}")
        
        if response.status_code == 200:
            result = response.json()
            if result.get("code") == 0:
                updated_role = result.get("data")
                print(f"✅ 成功更新角色: {updated_role.get('name')}")
                return True
            else:
                print(f"❌ 更新角色失败: {result.get('message')}")
                return False
        else:
            print(f"❌ 更新角色请求失败，状态码: {response.status_code}")
            return False
    
    def get_permissions_list(self):
        """获取权限列表（用于权限分配测试）"""
        print("\n📋 获取权限列表...")
        
        response = self.make_request("GET", "/api/permissions", username="admin")
        print(f"获取权限列表状态码: {response.status_code}")
        
        if response.status_code == 200:
            result = response.json()
            if result.get("code") == 0:
                permissions_data = result.get("data", [])
                if isinstance(permissions_data, dict) and "content" in permissions_data:
                    permissions = permissions_data["content"]
                elif isinstance(permissions_data, list):
                    permissions = permissions_data
                else:
                    permissions = []
                
                print(f"✅ 成功获取权限列表，共 {len(permissions)} 个权限")
                # 返回前几个权限ID用于测试
                permission_ids = [p.get("id") for p in permissions[:3] if p.get("id")]
                return permission_ids
            else:
                print(f"❌ 获取权限列表失败: {result.get('message')}")
                return []
        else:
            print(f"❌ 获取权限列表请求失败，状态码: {response.status_code}")
            return []
    
    def assign_permissions_to_role(self, permission_ids):
        """为角色分配权限"""
        if not self.created_role_id or not permission_ids:
            print("❌ 缺少角色ID或权限ID")
            return False
            
        print(f"\n🔗 为角色分配权限 (角色ID: {self.created_role_id})...")
        
        response = self.make_request(
            "POST",
            f"/api/roles/{self.created_role_id}/permissions",
            json=permission_ids,
            username="admin"
        )
        
        print(f"分配权限状态码: {response.status_code}")
        
        if response.status_code == 200:
            result = response.json()
            if result.get("code") == 0:
                print(f"✅ 成功为角色分配 {len(permission_ids)} 个权限")
                return True
            else:
                print(f"❌ 分配权限失败: {result.get('message')}")
                return False
        else:
            print(f"❌ 分配权限请求失败，状态码: {response.status_code}")
            return False
    
    def get_role_permissions(self):
        """获取角色的权限列表"""
        if not self.created_role_id:
            print("❌ 没有可查询的角色ID")
            return False
            
        print(f"\n🔍 获取角色权限列表 (角色ID: {self.created_role_id})...")
        
        response = self.make_request("GET", f"/api/roles/{self.created_role_id}/permissions", username="admin")
        print(f"获取角色权限状态码: {response.status_code}")
        
        if response.status_code == 200:
            result = response.json()
            if result.get("code") == 0:
                permission_ids = result.get("data", [])
                print(f"✅ 成功获取角色权限，共 {len(permission_ids)} 个权限")
                return True
            else:
                print(f"❌ 获取角色权限失败: {result.get('message')}")
                return False
        else:
            print(f"❌ 获取角色权限请求失败，状态码: {response.status_code}")
            return False
    
    def delete_role(self):
        """删除角色"""
        if not self.created_role_id:
            print("❌ 没有可删除的角色ID")
            return False
            
        print(f"\n🗑️ 删除角色 (ID: {self.created_role_id})...")
        
        response = self.make_request("DELETE", f"/api/roles/{self.created_role_id}", username="admin")
        print(f"删除角色状态码: {response.status_code}")
        
        if response.status_code == 200:
            result = response.json()
            if result.get("code") == 0:
                print("✅ 成功删除角色")
                return True
            else:
                print(f"❌ 删除角色失败: {result.get('message')}")
                return False
        else:
            print(f"❌ 删除角色请求失败，状态码: {response.status_code}")
            return False
    
    def run_all_tests(self):
        """运行所有角色管理测试"""
        print("🚀 开始角色管理功能测试")
        print("=" * 50)
        
        # 1. 管理员登录
        if not self.login_admin():
            print("❌ 管理员登录失败，测试终止")
            return False
        
        # 2. 获取角色列表
        if not self.get_roles_list():
            print("❌ 获取角色列表失败")
        
        # 3. 创建角色
        if not self.create_role():
            print("❌ 创建角色失败，后续测试可能受影响")
            return False
        
        # 4. 获取角色详情
        if not self.get_role_detail():
            print("❌ 获取角色详情失败")
        
        # 5. 更新角色
        if not self.update_role():
            print("❌ 更新角色失败")
        
        # 6. 获取权限列表
        permission_ids = self.get_permissions_list()
        
        # 7. 为角色分配权限
        if permission_ids:
            if not self.assign_permissions_to_role(permission_ids):
                print("❌ 分配权限失败")
        else:
            print("⚠️ 没有可用权限，跳过权限分配测试")
        
        # 8. 获取角色权限
        if not self.get_role_permissions():
            print("❌ 获取角色权限失败")
        
        # 9. 删除角色
        if not self.delete_role():
            print("❌ 删除角色失败")
        
        print("\n" + "=" * 50)
        print("🎉 角色管理功能测试完成！")
        return True

def main():
    """主函数"""
    tester = RoleManagementTester()
    
    try:
        success = tester.run_all_tests()
        if success:
            print("✅ 所有测试执行完成")
            sys.exit(0)
        else:
            print("❌ 测试执行过程中出现错误")
            sys.exit(1)
    except Exception as e:
        print(f"❌ 测试执行异常: {str(e)}")
        sys.exit(1)

if __name__ == "__main__":
    main()