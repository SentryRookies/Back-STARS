package com.example.congestionservice.controller;

import com.example.congestionservice.service.CongestionService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequiredArgsConstructor
@RequestMapping("/main")
public class CongestionController {
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    private final CongestionService congestionService;


    @GetMapping("/congestion")
    public SseEmitter streamCongestion() {
        SseEmitter emitter = new SseEmitter(0L); // 타임아웃 없음
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        try{
            System.out.println("혼잡도 푸시 중...");
            var congestionList = congestionService.getCongestion();

            sendToClients(congestionList); // 모든 지역 혼잡도 전송
            System.out.println(".. 혼잡도 푸시 완료");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return emitter;

    }

    // 주기적으로 클라이언트에게 push
    public void sendToClients(JsonNode congestionList) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("congestion-update")
                        .data(congestionList));

            } catch (IOException e) {
                emitter.completeWithError(e);
                emitters.remove(emitter);
            }
        }
    }

    // 주기적으로 클라이언트에게 push
    public void sendAlertToClients(JsonNode changedList) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("congestion-alert")
                        .data(changedList));

            } catch (IOException e) {
                emitter.completeWithError(e);
                emitters.remove(emitter);
            }
        }
    }
}
