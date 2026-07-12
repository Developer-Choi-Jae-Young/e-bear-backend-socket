package com.example.ebearsocket.domain.alarm.controller;

import com.example.ebearsocket.domain.alarm.service.AlarmService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "*") // 브라우저 차단 방지
public class AlarmController {
    /**
     * TODO
     * 1. 알람 유실 방지(ex 네트워크 불안정 등)를 위한 redis 설정 로직 추가
     * 2. 클라이언트 연결 시 보안강화(ex JWT 토큰, 세션 등) 로직 추가
     * 3. 알림 내역 DB 저장 로직 추가
     */
    
    private final AlarmService alarmService;

    // 클라이언트가 연결
    @GetMapping(value = "/subscribe/{userId}", produces = "text/event-stream")
    public SseEmitter subscribe(@PathVariable String userId) {
        return alarmService.subscribe(userId);
    }

    // 테스트용: 특정 유저에게 알람 보내기
    @GetMapping("/send-alarm/{userId}")
    public void sendAlarm(@PathVariable String userId, @RequestParam String msg) {
        alarmService.sendToUser(userId, msg);
    }

    // 테스트용: 전체 알림 보내기
    @GetMapping("/send-all")
    public void sendAll(@RequestParam String msg) {
        alarmService.sendToAll(msg);
    }
}
