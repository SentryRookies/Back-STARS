package com.example.congestionservice.controller;

import com.example.congestionservice.config.CongestionPreviousCache;
import com.example.congestionservice.service.CongestionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequiredArgsConstructor
@RequestMapping("/main")
public class CongestionController {
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final CongestionPreviousCache congestionPreviousCache;

    @GetMapping("/congestion")
    public SseEmitter streamCongestion() {
        SseEmitter emitter = new SseEmitter(0L); // 타임아웃 없음
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        Map<String, String> previousLevels = congestionPreviousCache.getPreviousLevels();

        // 초기데이터 투입
        try {
            System.out.println("혼잡도 초기 데이터 푸시 중...");
            var congestionList = CongestionService.getCongestion();

            emitter.send(SseEmitter.event()
                    .name("congestion-update")
                    .data(congestionList));

            // 혼잡도 알림 전송 위한 로직
            ObjectMapper mapper = new ObjectMapper();
            ArrayNode changedList = mapper.createArrayNode();  // ArrayNode 생성
            // previousdata 저장을 위한 데이터
            Map<String, String> currentLevels = new HashMap<>();

            for (JsonNode area : congestionList) {
                String areaName = area.get("area_nm").asText();
                String currentLevel = area.get("area_congest_lvl").asText();
                currentLevels.put(areaName, currentLevel);

                if(currentLevel.equals("약간 붐빔") || currentLevel.equals("붐빔")){
                    ObjectNode copy = mapper.createObjectNode();
                    area.fieldNames().forEachRemaining(field -> {
                        if (!"fcst_ppltn".equals(field)) {
                            copy.set(field, area.get(field));
                        }
                    });
                    changedList.add(copy);

                }
            }

            previousLevels.putAll(currentLevels);

            emitter.send(SseEmitter.event()
                    .name("congestion-alert")
                    .data(changedList));

            System.out.println(".. 혼잡도 초기 데이터 푸시 완료");
            
        } catch (IOException | IllegalStateException e) {
            emitter.completeWithError(e);
            emitters.remove(emitter);
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
