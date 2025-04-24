package com.example.externalinfoservice.scheduler;

import com.example.externalinfoservice.controller.SseWeatherController;
import com.example.externalinfoservice.service.WeatherEsService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WeatherPushScheduler {

    private final WeatherEsService weatherEsService;
    private final SseWeatherController sseWeatherController;

    // 5분마다 날씨 데이터를 SSE 클라이언트에 push
    @Scheduled(fixedRate = 10_000) // 5분 = 300초
    public void pushWeatherToClients() {
        var weatherList = weatherEsService.getAllWeatherFromES();
        sseWeatherController.sendToClients(weatherList);
    }
}
