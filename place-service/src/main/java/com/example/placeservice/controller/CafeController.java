package com.example.placeservice.controller;

import com.example.placeservice.dto.cafe.CafeListDto;
import com.example.placeservice.service.CafeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/main")
@RequiredArgsConstructor
public class CafeController {

    private final CafeService cafeService;

    /**
     * 전체 카페 목록 조회 - 레스토랑 스타일로 단순화 (캐싱 없음)
     */
    @GetMapping("/cafe/list")
    public ResponseEntity<List<CafeListDto>> getCafeList() {
        log.info("카페 목록 조회");
        return ResponseEntity.ok(cafeService.getSimpleCafeList());
    }

    @GetMapping("/info/cafe/{cafeId}")
    public ResponseEntity<Map<String, Object>> getCafeInfo(@PathVariable Long cafeId) {
        log.info("카페 ID {}의 상세 정보 조회", cafeId);
        Map<String, Object> cafeInfo = cafeService.getCafeInfo(cafeId);

        if (cafeInfo != null) {
            return ResponseEntity.ok(cafeInfo);
        }

        return ResponseEntity.notFound().build();
    }
}