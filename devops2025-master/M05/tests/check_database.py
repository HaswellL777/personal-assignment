#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import sys
import os
# 设置标准输出编码为UTF-8
if sys.platform.startswith('win'):
    import codecs
    sys.stdout = codecs.getwriter('utf-8')(sys.stdout.detach())
"""
数据库检查脚本
"""

import pymysql
import json
from datetime import datetime

def connect_database():
    """连接数据库"""
    try:
        connection = pymysql.connect(
            host='101.201.127.215',
            port=3306,
            user='devops2025',
            password='sspku2025',
            database='devops2025',
            charset='utf8mb4',
            cursorclass=pymysql.cursors.DictCursor
        )
        print("✅ 数据库连接成功")
        return connection
    except Exception as e:
        print(f"❌ 数据库连接失败: {str(e)}")
        return None

def check_tables(connection):
    """检查表是否存在"""
    print("\n🔍 检查数据库表...")
    
    try:
        with connection.cursor() as cursor:
            cursor.execute("SHOW TABLES")
            tables = cursor.fetchall()
            
            if tables:
                print("📋 现有表:")
                for table in tables:
                    table_name = list(table.values())[0]
                    print(f"  - {table_name}")
            else:
                print("⚠️  没有找到任何表")
                
            return [list(table.values())[0] for table in tables]
            
    except Exception as e:
        print(f"❌ 查询表失败: {str(e)}")
        return []

def check_users(connection):
    """检查用户数据"""
    print("\n👥 检查用户数据...")
    
    try:
        with connection.cursor() as cursor:
            cursor.execute("SELECT * FROM users")
            users = cursor.fetchall()
            
            if users:
                print(f"📊 找到 {len(users)} 个用户:")
                for user in users:
                    print(f"  - ID: {user.get('id')}, 用户名: {user.get('username')}, 邮箱: {user.get('email')}")
                    print(f"    密码哈希: {user.get('password_hash', '')[:50]}...")
                    print(f"    创建时间: {user.get('created_at')}")
                    print()
            else:
                print("⚠️  没有找到任何用户")
                
            return users
            
    except Exception as e:
        print(f"❌ 查询用户失败: {str(e)}")
        return []

def check_roles(connection):
    """检查角色数据"""
    print("\n🎭 检查角色数据...")
    
    try:
        with connection.cursor() as cursor:
            cursor.execute("SELECT * FROM roles")
            roles = cursor.fetchall()
            
            if roles:
                print(f"📊 找到 {len(roles)} 个角色:")
                for role in roles:
                    print(f"  - ID: {role.get('id')}, 名称: {role.get('name')}, 描述: {role.get('description')}")
            else:
                print("⚠️  没有找到任何角色")
                
            return roles
            
    except Exception as e:
        print(f"❌ 查询角色失败: {str(e)}")
        return []

def check_user_roles(connection):
    """检查用户角色关联"""
    print("\n🔗 检查用户角色关联...")
    
    try:
        with connection.cursor() as cursor:
            cursor.execute("""
                SELECT ur.*, u.username, r.name as role_name 
                FROM user_roles ur 
                LEFT JOIN users u ON ur.user_id = u.id 
                LEFT JOIN roles r ON ur.role_id = r.id
            """)
            user_roles = cursor.fetchall()
            
            if user_roles:
                print(f"📊 找到 {len(user_roles)} 个用户角色关联:")
                for ur in user_roles:
                    print(f"  - 用户: {ur.get('username')}, 角色: {ur.get('role_name')}")
            else:
                print("⚠️  没有找到任何用户角色关联")
                
            return user_roles
            
    except Exception as e:
        print(f"❌ 查询用户角色关联失败: {str(e)}")
        return []

def main():
    print("=" * 60)
    print("🔍 数据库数据检查")
    print("=" * 60)
    
    connection = connect_database()
    if not connection:
        return
    
    try:
        # 检查表
        tables = check_tables(connection)
        
        if 'users' in tables:
            # 检查用户
            users = check_users(connection)
            
        if 'roles' in tables:
            # 检查角色
            roles = check_roles(connection)
            
        if 'user_roles' in tables:
            # 检查用户角色关联
            user_roles = check_user_roles(connection)
            
    finally:
        connection.close()
        print("\n🔒 数据库连接已关闭")
    
    print("\n" + "=" * 60)
    print("🏁 检查完成")
    print("=" * 60)

if __name__ == "__main__":
    main()