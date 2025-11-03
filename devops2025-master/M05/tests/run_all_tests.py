#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
综合测试运行器
运行所有API测试脚本并生成测试报告
"""

import subprocess
import sys
import os
import time
from datetime import datetime
import json

class TestRunner:
    def __init__(self):
        self.test_results = []
        self.start_time = None
        self.end_time = None
        
    def run_test(self, test_name, script_path):
        """运行单个测试脚本"""
        print(f"\n{'='*60}")
        print(f"🧪 运行测试: {test_name}")
        print(f"📄 脚本路径: {script_path}")
        print(f"{'='*60}")
        
        if not os.path.exists(script_path):
            print(f"❌ 测试脚本不存在: {script_path}")
            self.test_results.append({
                "name": test_name,
                "script": script_path,
                "status": "SKIPPED",
                "reason": "脚本文件不存在",
                "duration": 0,
                "timestamp": datetime.now().isoformat()
            })
            return False
            
        start_time = time.time()
        
        try:
            # 运行测试脚本
            result = subprocess.run(
                [sys.executable, script_path],
                capture_output=True,
                text=True,
                encoding='utf-8',
                errors='replace',
                timeout=300  # 5分钟超时
            )
            
            duration = time.time() - start_time
            
            if result.returncode == 0:
                print(f"✅ {test_name} 测试成功完成")
                status = "PASSED"
                reason = "测试成功"
            else:
                print(f"❌ {test_name} 测试失败")
                print(f"错误输出: {result.stderr}")
                status = "FAILED"
                reason = f"退出码: {result.returncode}"
                
            self.test_results.append({
                "name": test_name,
                "script": script_path,
                "status": status,
                "reason": reason,
                "duration": round(duration, 2),
                "timestamp": datetime.now().isoformat(),
                "stdout": result.stdout,
                "stderr": result.stderr
            })
            
            return result.returncode == 0
            
        except subprocess.TimeoutExpired:
            duration = time.time() - start_time
            print(f"⏰ {test_name} 测试超时")
            self.test_results.append({
                "name": test_name,
                "script": script_path,
                "status": "TIMEOUT",
                "reason": "测试执行超时(5分钟)",
                "duration": round(duration, 2),
                "timestamp": datetime.now().isoformat()
            })
            return False
            
        except Exception as e:
            duration = time.time() - start_time
            print(f"💥 {test_name} 测试异常: {str(e)}")
            self.test_results.append({
                "name": test_name,
                "script": script_path,
                "status": "ERROR",
                "reason": str(e),
                "duration": round(duration, 2),
                "timestamp": datetime.now().isoformat()
            })
            return False
    
    def generate_report(self):
        """生成测试报告"""
        print(f"\n{'='*80}")
        print("📊 测试报告生成中...")
        print(f"{'='*80}")
        
        total_tests = len(self.test_results)
        passed_tests = len([r for r in self.test_results if r["status"] == "PASSED"])
        failed_tests = len([r for r in self.test_results if r["status"] == "FAILED"])
        skipped_tests = len([r for r in self.test_results if r["status"] == "SKIPPED"])
        timeout_tests = len([r for r in self.test_results if r["status"] == "TIMEOUT"])
        error_tests = len([r for r in self.test_results if r["status"] == "ERROR"])
        
        total_duration = sum([r["duration"] for r in self.test_results])
        
        # 控制台报告
        print(f"\n🎯 测试执行总结:")
        print(f"   总测试数: {total_tests}")
        print(f"   ✅ 成功: {passed_tests}")
        print(f"   ❌ 失败: {failed_tests}")
        print(f"   ⏭️  跳过: {skipped_tests}")
        print(f"   ⏰ 超时: {timeout_tests}")
        print(f"   💥 异常: {error_tests}")
        print(f"   ⏱️  总耗时: {total_duration:.2f}秒")
        
        if self.start_time and self.end_time:
            print(f"   🕐 开始时间: {self.start_time}")
            print(f"   🕐 结束时间: {self.end_time}")
        
        print(f"\n📋 详细测试结果:")
        for result in self.test_results:
            status_icon = {
                "PASSED": "✅",
                "FAILED": "❌", 
                "SKIPPED": "⏭️",
                "TIMEOUT": "⏰",
                "ERROR": "💥"
            }.get(result["status"], "❓")
            
            print(f"   {status_icon} {result['name']}: {result['status']} ({result['duration']}s)")
            if result["status"] != "PASSED":
                print(f"      原因: {result['reason']}")
        
        # 生成JSON报告文件
        report_data = {
            "summary": {
                "total_tests": total_tests,
                "passed": passed_tests,
                "failed": failed_tests,
                "skipped": skipped_tests,
                "timeout": timeout_tests,
                "error": error_tests,
                "total_duration": total_duration,
                "start_time": self.start_time,
                "end_time": self.end_time,
                "success_rate": round((passed_tests / total_tests * 100) if total_tests > 0 else 0, 2)
            },
            "test_results": self.test_results,
            "generated_at": datetime.now().isoformat()
        }
        
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        report_file = f"reports/test_report_{timestamp}.json"
        try:
            with open(report_file, 'w', encoding='utf-8') as f:
                json.dump(report_data, f, ensure_ascii=False, indent=2)
            print(f"\n📄 详细测试报告已保存到: {report_file}")
        except Exception as e:
            print(f"\n❌ 保存测试报告失败: {str(e)}")
        
        # 返回是否所有测试都成功
        return failed_tests == 0 and timeout_tests == 0 and error_tests == 0
    
    def run_all_tests(self):
        """运行所有测试"""
        self.start_time = datetime.now().isoformat()
        
        print("🚀 开始运行所有API测试")
        print(f"🕐 开始时间: {self.start_time}")
        print("="*80)
        
        # 检查必要的依赖文件
        required_files = ["token_manager.py", "base_test.py"]
        for file_name in required_files:
            file_path = os.path.join(os.path.dirname(__file__), file_name)
            if not os.path.exists(file_path):
                print(f"❌ 缺少必要的依赖文件: {file_name}")
                print("请确保所有必要的文件都存在后再运行测试")
                return 1
        
        # 定义测试列表
        tests = [
            ("数据库连接测试", "check_database.py"),
            ("用户管理测试", "test_user_management.py"),
            ("角色管理测试", "test_role_management.py"),
            ("权限管理测试", "test_permission_management.py"),
            ("认证授权测试", "test_auth.py")
        ]
        
        # 运行每个测试
        for test_name, script_name in tests:
            script_path = os.path.join(os.path.dirname(__file__), script_name)
            self.run_test(test_name, script_path)
            
            # 测试间隔
            time.sleep(1)
        
        self.end_time = datetime.now().isoformat()
        
        # 生成报告
        all_passed = self.generate_report()
        
        print(f"\n{'='*80}")
        if all_passed:
            print("🎉 所有测试都成功完成！")
            return 0
        else:
            print("⚠️  部分测试失败，请查看详细报告")
            return 1

def main():
    """主函数"""
    print("🧪 DevOps2025 API 综合测试运行器")
    print("="*80)
    
    runner = TestRunner()
    exit_code = runner.run_all_tests()
    
    print(f"\n🏁 测试运行完成，退出码: {exit_code}")
    sys.exit(exit_code)

if __name__ == "__main__":
    main()