package com.example.placeservice.controller;

import com.example.placeservice.service.CongestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/congestion")
@RequiredArgsConstructor
public class CongestionController {
    private final CongestionService congestionService;

    @GetMapping("/{area-id}")
    public String getCongestionData(@PathVariable("area-id") String areaName) {
        // 서울 실시간 도시데이터 OpenAPI 데이터 중 인구 혼잡도 데이터 이용
        return congestionService.getCongestion(areaName);
    }
}
