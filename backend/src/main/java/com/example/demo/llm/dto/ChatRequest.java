package com.example.demo.llm.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class ChatRequest {
    private String model;

    @NotEmpty(message = "消息列表不能为空")
    @Size(max = 50, message = "消息数量不能超过50条")
    @Valid
    private List<Message> messages;

    private Boolean stream;

    @Data
    public static class Message {
        private String role;

        @Size(max = 10000, message = "单条消息内容不能超过10000字符")
        private String content;
    }
}
