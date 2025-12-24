package com.example.demo.llm;

import com.example.demo.llm.dto.ChatRequest;
import com.example.demo.llm.dto.ChatResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class LlmService {

    private static final Logger logger = LoggerFactory.getLogger(LlmService.class);

    @Value("${llm.api-url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${llm.api-key:}")
    private String apiKey;

    @Value("${llm.model:gpt-3.5-turbo}")
    private String defaultModel;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @PostConstruct
    public void init() {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            logger.warn("LLM API Key 未配置，LLM功能将不可用。请设置环境变量 LLM_API_KEY");
        } else {
            logger.info("LLM服务初始化完成，API URL: {}, 默认模型: {}", apiUrl, defaultModel);
        }
    }

    @PreDestroy
    public void destroy() {
        logger.info("正在关闭LLM服务线程池...");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    logger.error("LLM服务线程池未能正常关闭");
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        logger.info("LLM服务线程池已关闭");
    }

    /**
     * 检查LLM服务是否可用
     */
    private void checkServiceAvailable() {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException("LLM API Key 未配置，请联系管理员设置");
        }
    }

    /**
     * 同步聊天请求
     */
    public ChatResponse chat(ChatRequest request) throws Exception {
        checkServiceAvailable();

        if (request.getModel() == null || request.getModel().trim().isEmpty()) {
            request.setModel(defaultModel);
        }
        request.setStream(false);

        logger.debug("发送聊天请求到LLM API，模型: {}", request.getModel());

        HttpURLConnection conn = null;
        try {
            conn = createConnection();
            byte[] requestBody = objectMapper.writeValueAsBytes(request);
            conn.getOutputStream().write(requestBody);
            conn.getOutputStream().flush();

            int responseCode = conn.getResponseCode();
            logger.debug("LLM API响应码: {}", responseCode);

            if (responseCode != 200) {
                String errorMessage = readErrorResponse(conn);
                logger.error("LLM API调用失败，响应码: {}, 错误信息: {}", responseCode, errorMessage);
                throw new RuntimeException("LLM API调用失败: " + errorMessage);
            }

            try (var is = conn.getInputStream()) {
                ChatResponse response = objectMapper.readValue(is, ChatResponse.class);
                logger.debug("LLM API调用成功，响应ID: {}", response.getId());
                return response;
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * 流式聊天请求
     */
    public SseEmitter chatStream(ChatRequest request) {
        SseEmitter emitter = new SseEmitter(120000L); // 2分钟超时

        executor.execute(() -> {
            HttpURLConnection conn = null;
            try {
                checkServiceAvailable();

                if (request.getModel() == null || request.getModel().trim().isEmpty()) {
                    request.setModel(defaultModel);
                }
                request.setStream(true);

                logger.debug("发送流式聊天请求到LLM API，模型: {}", request.getModel());

                conn = createConnection();
                byte[] requestBody = objectMapper.writeValueAsBytes(request);
                conn.getOutputStream().write(requestBody);
                conn.getOutputStream().flush();

                int responseCode = conn.getResponseCode();
                if (responseCode != 200) {
                    String errorMessage = readErrorResponse(conn);
                    logger.error("LLM API流式调用失败，响应码: {}, 错误信息: {}", responseCode, errorMessage);
                    emitter.send(SseEmitter.event().data("{\"error\":\"" + errorMessage + "\"}"));
                    emitter.complete();
                    return;
                }

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("data: ")) {
                            String data = line.substring(6);
                            if ("[DONE]".equals(data)) {
                                emitter.send(SseEmitter.event().data("[DONE]"));
                                logger.debug("流式响应完成");
                                break;
                            }
                            emitter.send(SseEmitter.event().data(data));
                        }
                    }
                }
                emitter.complete();
            } catch (IllegalStateException e) {
                logger.warn("LLM服务不可用: {}", e.getMessage());
                try {
                    emitter.send(SseEmitter.event().data("{\"error\":\"" + e.getMessage() + "\"}"));
                } catch (IOException ignored) {}
                emitter.complete();
            } catch (Exception e) {
                logger.error("流式聊天处理失败", e);
                emitter.completeWithError(e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        });

        emitter.onCompletion(() -> logger.debug("SSE连接完成"));
        emitter.onTimeout(() -> logger.warn("SSE连接超时"));
        emitter.onError(e -> logger.error("SSE连接错误", e));

        return emitter;
    }

    /**
     * 创建HTTP连接
     */
    private HttpURLConnection createConnection() throws Exception {
        URL url = URI.create(apiUrl).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setDoOutput(true);
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(60000);
        return conn;
    }

    /**
     * 读取错误响应
     */
    private String readErrorResponse(HttpURLConnection conn) {
        try (var errorStream = conn.getErrorStream()) {
            if (errorStream != null) {
                return new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            logger.warn("读取错误响应失败", e);
        }
        return "Unknown error (HTTP " + getResponseCodeSafely(conn) + ")";
    }

    private int getResponseCodeSafely(HttpURLConnection conn) {
        try {
            return conn.getResponseCode();
        } catch (Exception e) {
            return -1;
        }
    }
}
