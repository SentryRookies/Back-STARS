package com.example.externalinfoservice.controller;

import com.example.externalinfoservice.service.WeatherEsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;


@RestController
@RequiredArgsConstructor
@RequestMapping("/main/info/weather")
public class SseWeatherController {

    private final WeatherEsService weatherEsService;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping("/stream")
    public SseEmitter streamWeather() {
        SseEmitter emitter = new SseEmitter(0L); // 타임아웃 없음
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        return emitter;
    }

    // 주기적으로 클라이언트에게 push
    public void sendToClients(List<Map<String, Object>> weatherList) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("weather-update")
                        .data(weatherList));
            } catch (IOException e) {
                emitter.completeWithError(e);
                emitters.remove(emitter);
            }
        }
    }
}