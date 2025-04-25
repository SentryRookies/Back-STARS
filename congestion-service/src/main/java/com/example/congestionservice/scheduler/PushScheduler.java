package com.example.congestionservice.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PushScheduler {


    // 5분마다 날씨 데이터를 SSE 클라이언트에 push
    @Scheduled(fixedRate = 300_000) // 5분 = 300초
    public void pushWeatherToClients() {
        var trafficList = roadService.getTrafficData();
        streamController.sendToClients(trafficList);
    }

}
