package com.example.placeservice.controller;

import com.example.placeservice.service.CafeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/cafes")
@RequiredArgsConstructor
public class CafeController {

    private final CafeService cafeService;

    /**
     * 모든 장소의 주변 카페 데이터 처리
     */
    @PostMapping("/process-all")
    public ResponseEntity<Map<String, Object>> processAllAreas() {
        Map<String, Object> response = new HashMap<>();

        try {
            cafeService.processAllAreas();

            response.put("success", true);
            response.put("message", "모든 장소의 주변 카페 정보 처리가 시작되었습니다.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("처리 중 오류 발생: {}", e.getMessage(), e);

            response.put("success", false);
            response.put("message", "오류 발생: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }
}