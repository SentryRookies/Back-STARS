package com.example.externalinfoservice.controller;

import com.example.externalinfoservice.service.AccidentEsService;
import com.example.externalinfoservice.service.ESRoadService;
import com.example.externalinfoservice.service.ParkEsService;
import com.example.externalinfoservice.service.WeatherEsService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequiredArgsConstructor
@RequestMapping("/main")
@Slf4j
public class StreamController {
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    private final WeatherEsService weatherEsService;
    private final ESRoadService roadService;
    private final ParkEsService parkEsService;
    private final AccidentEsService accidentEsService;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        SseEmitter emitter = new SseEmitter(0L); // 타임아웃 없음
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        // 구독시, 초기 데이터 주입
        try {
            log.info("초기 데이터 푸시 중...");
            var weatherList = weatherEsService.getAllWeatherFromES();
            var trafficList = roadService.getTrafficData();
            var parkList = parkEsService.getAllParkFromES();
            var accidentList = accidentEsService.getAllAccidentsFromES();

            emitter.send(SseEmitter.event()
                    .name("weather-update")
                    .data(weatherList));
            emitter.send(SseEmitter.event()
                    .name("traffic-update")
                    .data(trafficList));
            emitter.send(SseEmitter.event()
                    .name("park-update")
                    .data(parkList));
            emitter.send(SseEmitter.event()
                    .name("accident-alert")
                    .data(accidentList));

            log.info(".. 초기 데이터 푸시 완료");

        } catch (IOException | IllegalStateException e) {
            log.error("SSE 오류 발생");
            emitter.completeWithError(e);
            emitters.remove(emitter);
        }

        return emitter;
    }

    // 주기적으로 클라이언트에게 push
    public void sendToClients(List<Map<String, Object>> weatherList, JsonNode trafficList, List<Map<String, Object>> parkList, List<Map<String, Object>> accidentList) {
        for (SseEmitter emitter : emitters) {
            try {
                log.info("external 데이터 푸시 중...");

                emitter.send(SseEmitter.event()
                        .name("weather-update")
                        .data(weatherList));
                log.info(".. 날씨 데이터 푸시 완료");

                emitter.send(SseEmitter.event()
                        .name("traffic-update")
                        .data(trafficList));
                log.info(".. 도로현황 데이터 푸시 완료");

                emitter.send(SseEmitter.event()
                        .name("park-update")
                        .data(parkList));
                log.info(".. 주차 데이터 푸시 완료");

                emitter.send(SseEmitter.event()
                        .name("accident-alert")
                        .data(accidentList));
                log.info(".. 사고 데이터 푸시 완료");

            } catch (IOException | IllegalStateException e) {
                log.error("SSE 오류 발생");
                emitter.completeWithError(e);
                emitters.remove(emitter);
            }
        }
    }
}