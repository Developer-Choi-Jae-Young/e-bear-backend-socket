package com.example.ebearsocket.controller;

import com.example.ebearsocket.dto.ChatMessage;
import com.example.ebearsocket.dto.ChatMessageReqDto;
import com.example.ebearsocket.dto.ChatMessageResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final RestTemplate restTemplate;

    @Value("${user.chat.api.server}")
    private String url;

    @MessageMapping("/chat/send")
    public void send(ChatMessage message) {
        ChatMessageReqDto request = ChatMessageReqDto.builder().roomId(Long.valueOf(message.getRoomId())).content(message.getContent()).senderId(message.getSenderId()).build();
        ResponseEntity<ChatMessageResDto> response = restTemplate.postForEntity(url, request, ChatMessageResDto.class);

        if(response.getStatusCode().is2xxSuccessful()) {
            messagingTemplate.convertAndSend("/topic/chat/" + message.getRoomId(), message);
        }
    }
}
