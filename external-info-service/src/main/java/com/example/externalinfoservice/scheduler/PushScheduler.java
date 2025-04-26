package com.example.externalinfoservice.scheduler;

import com.example.externalinfoservice.controller.StreamController;
import com.example.externalinfoservice.service.ESRoadService;
import com.example.externalinfoservice.service.ParkEsService;
import com.example.externalinfoservice.service.WeatherEsService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PushScheduler {

    private final WeatherEsService weatherEsService;
    private final ESRoadService roadService;
    private final ParkEsService parkEsService;
    private final StreamController streamController;

    // 5분마다 날씨 데이터를 SSE 클라이언트에 push
    @Scheduled(fixedRate = 300_000) // 5분 = 300초
    public void pushWeatherToClients() {
        var weatherList = weatherEsService.getAllWeatherFromES();
        var trafficList = roadService.getTrafficData();
        var parkList = parkEsService.getAllParkFromES();
        streamController.sendToClients(weatherList, trafficList, parkList);
    }

}
