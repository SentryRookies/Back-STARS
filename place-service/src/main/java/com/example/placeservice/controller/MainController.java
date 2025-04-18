package com.example.placeservice.controller;

import com.example.placeservice.dto.CafeDto;
import com.example.placeservice.entity.Cafe;
import com.example.placeservice.service.CafeService;
import lombok.RequiredArgsConstructor;
import com.example.placeservice.repository.CafeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/main")
@RequiredArgsConstructor
public class MainController {

    private final CafeRepository cafeRepository;
    private final CafeService cafeService;

    /**
     * 전체 카페 목록 조회
     */
    @GetMapping("/cafe/list")
    public ResponseEntity<Map<String, Object>> getCafeList() {
        log.info("카페 목록 조회");
        List<Cafe> cafes = cafeRepository.findAll();

        List<Map<String, Object>> cafeList = cafes.stream()
                .map(cafe -> {
                    Map<String, Object> cafeMap = new HashMap<>();
                    cafeMap.put("id", cafe.getId().toString());
                    cafeMap.put("name", cafe.getName());
                    cafeMap.put("x", cafe.getLon().toString());
                    cafeMap.put("y", cafe.getLat().toString());
                    return cafeMap;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("data", cafeList);

        return ResponseEntity.ok(response);
    }

    /**
     * 지역 ID를 기준으로 카페 목록 조회
     */
    @GetMapping("/cafe/list/{areaId}")
    public ResponseEntity<List<CafeDto>> getCafesByAreaId(@PathVariable Long areaId) {
        log.info("지역 ID {}의 카페 목록 조회", areaId);
        List<CafeDto> cafes = cafeService.getCafesByAreaId(areaId);
        return ResponseEntity.ok(cafes);
    }
}