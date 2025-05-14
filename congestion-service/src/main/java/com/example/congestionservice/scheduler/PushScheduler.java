package com.example.congestionservice.scheduler;

import com.example.congestionservice.controller.CongestionController;
import com.example.congestionservice.service.CongestionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PushScheduler {
    private final CongestionController congestionController;


    // 이전 혼잡도 상태를 저장하는 필드 추가(혼잡도 알림용)
    private Map<String, String> previousLevels = new HashMap<>();


    @Scheduled(fixedRate = 300_000)
    public void push() {
        System.out.println("혼잡도 푸시 중...");
        var congestionList = CongestionService.getCongestion();

        congestionController.sendToClients(congestionList); // 모든 지역 혼잡도 전송

        // 혼잡도 알림 전송 위한 로직
        Map<String, String> currentLevels = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        // ArrayNode 생성
        ArrayNode changedList = mapper.createArrayNode();

        for (JsonNode area : congestionList) {
            String areaName = area.get("area_nm").asText();
            String currentLevel = area.get("area_congest_lvl").asText();
            currentLevels.put(areaName, currentLevel);

            String previousLevel = previousLevels.get(areaName);
            // 혼잡도 변화 알림 로그 확인
//            System.out.println(areaName+"의"+previousLevel+":ㅣㅣ"+currentLevel);

            // 혼잡도 알림 전송(조건 : 1,2단계 -> 3단계 // 4단계)
            if (previousLevel == null && (currentLevel.equals("약간 붐빔") || currentLevel.equals("붐빔"))) {
                ObjectNode copy = mapper.createObjectNode();
                area.fieldNames().forEachRemaining(field -> {
                    if (!"fcst_ppltn".equals(field)) {
                        copy.set(field, area.get(field));
                    }
                });
                changedList.add(copy);
            }
            else if (previousLevel != null){
                if(currentLevel.equals("붐빔")){
                    ObjectNode copy = mapper.createObjectNode();
                    area.fieldNames().forEachRemaining(field -> {
                        if (!"fcst_ppltn".equals(field)) {
                            copy.set(field, area.get(field));
                        }
                    });
                    changedList.add(copy);
                }
                else if ((previousLevel.equals("여유") || previousLevel.equals("보통")) && (currentLevel.equals("약간 붐빔"))) {
                    ObjectNode copy = mapper.createObjectNode();
                    area.fieldNames().forEachRemaining(field -> {
                        if (!"fcst_ppltn".equals(field)) {
                            copy.set(field, area.get(field));
                        }
                    });
                    changedList.add(copy);
                }
            }
        }

        // 변화 없으면 return
        if (changedList.isEmpty()) {
            System.out.println("혼잡도 변화 없음");
        }else{
            System.out.println("혼잡도 변화 있음 → alert-update로 SSE 전송");
            congestionController.sendAlertToClients(changedList); // ✨ 바뀐 것만 보냄
        }
        previousLevels = currentLevels;

    }
}
