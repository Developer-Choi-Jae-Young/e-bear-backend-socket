package com.example.ebearsocket.domain.alarm.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Service
public class AlarmService {

    // 사용자 ID를 키로 하여 SseEmitter를 저장
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    // 클라이언트가 연결을 시도할 때 호출
    public SseEmitter subscribe(String userId) {
        SseEmitter emitter = new SseEmitter(60 * 1000L * 60); // 1시간 유지
        emitters.put(userId, emitter);

        // 연결 종료/타임아웃 시 맵에서 삭제
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));

        // 연결 즉시 더미 데이터 보냄 : 503 에러 방지
        try {
            emitter.send(SseEmitter.event().name("connect").data("Connected!!"));
        } catch (IOException e) {
            emitters.remove(userId);
            log.debug("연결 데이터 전송 실패 : "+e);
        }

        return emitter;
    }

    // 1:1 알람 보내기
    public void sendToUser(String userId, String message) {
        if (emitters.containsKey(userId)) {
            try {
                emitters.get(userId).send(SseEmitter.event().name("alarm").data(message));
            } catch (IOException e) {
                emitters.remove(userId);
                log.debug("연결 데이터 전송 실패 : "+e);
            }
        }
    }

    // 1:N (전체) 알람 보내기
    public void sendToAll(String message) {
        emitters.forEach((userId, emitter) -> {
            try{
                emitter.send(SseEmitter.event().name("alarm").data(message));
            } catch (IOException e) {
                emitters.remove(userId);
            }
        });
    }
}
