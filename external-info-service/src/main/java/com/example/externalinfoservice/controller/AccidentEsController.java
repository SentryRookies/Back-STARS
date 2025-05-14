package com.example.externalinfoservice.controller;

import com.example.externalinfoservice.service.AccidentEsService;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("main/accident")
public class AccidentEsController {

    private final AccidentEsService accidentEsService;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping("")
    public SseEmitter streamAccident() {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        List<Map<String, Object>> accidentList = accidentEsService.getAllAccidentsFromES();
        sendToClients(accidentList);

        return emitter;
    }

    public void sendToClients(List<Map<String, Object>> accidentList) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("accident-alert")
                        .data(accidentList));
            } catch (IOException e) {
                emitter.completeWithError(e);
                emitters.remove(emitter);
            }
        }
    }
}