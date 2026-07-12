package com.example.ebearsocket.controller;

import com.example.ebearsocket.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/send")
    public void send(ChatMessage message) {
        messagingTemplate.convertAndSend("/topic/chat/" + message.getRoomId(), message);
    }
}
