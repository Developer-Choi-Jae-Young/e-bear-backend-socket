package com.example.ebearsocket.controller;

import com.example.ebearsocket.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

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
            messagingTemplate.convertAndSend("/topic/list", ChatListReqDto.builder().id(message.getRoomId()).lastMessage(message.getContent())
                    .lastMessageTime(Objects.requireNonNull(response.getBody()).getLastMessageTime()).messageCount(response.getBody().getMessageCount()).build());
        }
    }

    @MessageMapping("/chat/list")
    public void send(ChatListReqDto chatListReqDto) {
        ChatListResDto chatListResDto = ChatListResDto.builder().id(chatListReqDto.getId()).lastMessage(chatListReqDto.getLastMessage())
                .lastMessageTime(chatListReqDto.getLastMessageTime()).messageCount(chatListReqDto.getMessageCount()).build();
        messagingTemplate.convertAndSend("/topic/list", chatListResDto);
    }

    @PostMapping("/socket/read-notify")
    public ResponseEntity<Boolean> notifyReadStatus(@RequestBody ChatListReqDto dto) {
        ChatListResDto chatListResDto = ChatListResDto.builder()
                .id(dto.getId())
                .lastMessage(dto.getLastMessage())
                .lastMessageTime(dto.getLastMessageTime())
                .messageCount(dto.getMessageCount())
                .build();

        messagingTemplate.convertAndSend("/topic/list", chatListResDto);

        return ResponseEntity.status(HttpStatus.OK).body(true);
    }
}
