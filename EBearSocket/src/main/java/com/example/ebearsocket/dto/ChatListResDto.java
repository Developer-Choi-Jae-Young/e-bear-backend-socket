package com.example.ebearsocket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatListResDto {
    private String id;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Integer messageCount;
}
