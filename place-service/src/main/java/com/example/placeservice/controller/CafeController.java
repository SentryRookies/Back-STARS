package com.example.placeservice.controller;

import com.example.placeservice.entity.Cafe;
import com.example.placeservice.service.CafeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import com.example.placeservice.repository.CafeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/main")
@RequiredArgsConstructor
public class CafeController {

    private final CafeRepository cafeRepository;
    private final CafeService cafeService;

    /**
     * 전체 카페 목록 조회
     */
    @GetMapping("/cafe/list")
    public ResponseEntity<List<Map<String, Object>>> getCafeList() {
        log.info("카페 목록 조회");
        // 캐싱된 서비스 메소드 호출로 변경
        return ResponseEntity.ok(cafeService.getCachedCafeList());
    }

    @GetMapping("/info/cafe/{cafeId}")
    public ResponseEntity<Map<String, Object>> getCafeInfo(@PathVariable Long cafeId) {
        log.info("카페 ID {}의 상세 정보 조회", cafeId);
        // 캐싱된 서비스 메소드 호출로 변경
        Map<String, Object> cafeInfo = cafeService.getCachedCafeInfo(cafeId);

        if (cafeInfo != null) {
            return ResponseEntity.ok(cafeInfo);
        }

        return ResponseEntity.notFound().build();
    }
}