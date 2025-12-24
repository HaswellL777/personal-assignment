package com.example.demo.llm;

import com.example.demo.common.ApiResponse;
import com.example.demo.llm.dto.ChatRequest;
import com.example.demo.llm.dto.ChatResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/llm")
public class LlmController {

    private static final Logger logger = LoggerFactory.getLogger(LlmController.class);

    @Autowired
    private LlmService llmService;

    @PostMapping("/chat")
    public ApiResponse<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        logger.info("收到聊天请求，模型: {}", request.getModel());
        try {
            ChatResponse response = llmService.chat(request);
            logger.info("聊天请求处理成功");
            return ApiResponse.ok(response);
        } catch (IllegalStateException e) {
            logger.warn("LLM服务配置错误: {}", e.getMessage());
            return ApiResponse.fail(50001, e.getMessage());
        } catch (Exception e) {
            logger.error("LLM调用失败", e);
            return ApiResponse.fail(50000, "LLM调用失败: " + e.getMessage());
        }
    }

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(@Valid @RequestBody ChatRequest request) {
        logger.info("收到流式聊天请求，模型: {}", request.getModel());
        return llmService.chatStream(request);
    }
}
