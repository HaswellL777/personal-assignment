package com.example.demo.app;

import com.example.demo.common.ApiResponse;
import com.example.demo.app.vo.AppVO;
import com.example.demo.common.BusinessException;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/apps")
public class AppController {

    @GetMapping
    public ApiResponse<List<AppVO>> list() {
        List<AppVO> apps = new ArrayList<>();

        apps.add(AppVO.builder()
                .id(1L)
                .name("智能客服助手")
                .description("基于大语言模型的智能客服系统，支持多轮对话和知识库检索")
                .fullDescription("这是一个功能强大的智能客服系统，基于最新的大语言模型技术，能够理解用户意图，提供准确的回答。支持多轮对话、知识库检索、情感分析等功能。")
                .avatar("https://via.placeholder.com/80x80")
                .category("customer-service")
                .price(BigDecimal.ZERO)
                .rating(4.8)
                .downloads(1200)
                .reviews(156)
                .author("AI团队")
                .publishedAt(LocalDate.of(2024, 1, 15))
                .tags(List.of("客服", "对话", "AI"))
                .features(List.of("多轮对话支持", "知识库集成", "情感分析", "多语言支持", "实时学习"))
                .scenarios(List.of("客户服务", "在线咨询", "技术支持"))
                .build());

        apps.add(AppVO.builder()
                .id(2L)
                .name("内容创作大师")
                .description("AI驱动的内容生成和优化工具，支持多种文体创作")
                .fullDescription("专业的内容创作工具，利用AI技术帮助用户快速生成高质量内容。支持文章、广告文案、社交媒体内容等多种文体的创作和优化。")
                .avatar("https://via.placeholder.com/80x80")
                .category("content-creation")
                .price(BigDecimal.ZERO)
                .rating(4.9)
                .downloads(980)
                .reviews(123)
                .author("创作工坊")
                .publishedAt(LocalDate.of(2024, 1, 10))
                .tags(List.of("内容创作", "文案", "SEO"))
                .features(List.of("多文体支持", "SEO优化", "内容检测", "风格调整", "批量生成"))
                .scenarios(List.of("内容营销", "社交媒体", "博客写作"))
                .build());

        return ApiResponse.ok(apps);
    }

    @GetMapping("/test-biz-error")
    public ApiResponse<Void> testBizError() {
        // 模拟业务场景报错：用户余额不足
        throw new BusinessException(10001, "余额不足，请充值后再试");
    }
}
