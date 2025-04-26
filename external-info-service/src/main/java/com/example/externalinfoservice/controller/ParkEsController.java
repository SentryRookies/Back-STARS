package com.example.externalinfoservice.controller;

import com.example.externalinfoservice.service.ParkEsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequiredArgsConstructor
@RequestMapping("/main/info/park")
public class ParkEsController {

    private final ParkEsService parkEsService;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    // REST API 엔드포인트 - 일반 조회
    @GetMapping(value = {"/{areaId}", ""}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPark(@PathVariable(name = "areaId", required = false) String areaId) {
        if (areaId == null || areaId.isBlank()) {
            List<Map<String, Object>> allPark = parkEsService.getAllParkFromES();
            return ResponseEntity.ok(allPark);
        }

        Map<String, Object> park = parkEsService.getParkFromES(areaId);
        return park != null ? ResponseEntity.ok(park) : ResponseEntity.notFound().build();
    }

    // SSE 스트림 엔드포인트
    @GetMapping("/stream")
    public SseEmitter streamPark() {
        SseEmitter emitter = new SseEmitter(0L); // 타임아웃 없음
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        return emitter;
    }

//    // 주기적으로 클라이언트에게 push
//    public void sendToClients(List<Map<String, Object>> parkList) {
//        for (SseEmitter emitter : emitters) {
//            try {
//                emitter.send(SseEmitter.event()
//                        .name("park-update")
//                        .data(parkList));
//            } catch (IOException e) {
//                emitter.completeWithError(e);
//                emitters.remove(emitter);
//            }
//        }
//    }
}