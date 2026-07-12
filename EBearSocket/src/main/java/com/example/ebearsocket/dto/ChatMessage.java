package com.example.ebearsocket.dto;

import lombok.Data;

@Data
public class ChatMessage {
    private String roomId;
    private String content;
    private Long senderId;
}
