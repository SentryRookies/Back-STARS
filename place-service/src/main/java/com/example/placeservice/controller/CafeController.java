package com.example.placeservice.controller;

import com.example.placeservice.dto.cafe.CafeDto;
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
        List<Cafe> cafes = cafeRepository.findAll();

        // ID를 기준으로 정렬
        List<Map<String, Object>> cafeList = cafes.stream()
                .sorted(Comparator.comparing(Cafe::getId))
                .map(cafe -> {
                    Map<String, Object> cafeMap = new LinkedHashMap<>();
                    cafeMap.put("id", cafe.getId().toString());
                    cafeMap.put("name", cafe.getName());
                    cafeMap.put("x", cafe.getLon().toString());
                    cafeMap.put("y", cafe.getLat().toString());
                    return cafeMap;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(cafeList);
    }

    /**
     * 지역 ID를 기준으로 카페 목록 조회
     */
    @GetMapping("/cafe/list/{areaId}")
    public ResponseEntity<List<CafeDto>> getCafesByAreaId(@PathVariable Long areaId) {
        log.info("지역 ID {}의 카페 목록 조회", areaId);
        List<CafeDto> cafes = cafeService.getCafesByAreaId(areaId);

        // ID 기준으로 정렬
        List<CafeDto> sortedCafes = cafes.stream()
                .sorted(Comparator.comparing(CafeDto::getId))
                .collect(Collectors.toList());

        return ResponseEntity.ok(sortedCafes);
    }

    @GetMapping("/info/cafe/{cafeId}")
    public ResponseEntity<Map<String, Object>> getCafeInfo(@PathVariable Long cafeId) {
        log.info("카페 ID {}의 상세 정보 조회", cafeId);

        // 카페 ID로 상세 정보 조회
        Optional<Cafe> cafeOptional = cafeRepository.findById(cafeId);

        if (cafeOptional.isPresent()) {
            Cafe cafe = cafeOptional.get();
            Map<String, Object> cafeInfo = new LinkedHashMap<>();
            cafeInfo.put("id", cafe.getId());
            cafeInfo.put("name", cafe.getName());
            cafeInfo.put("address", cafe.getAddress());
            cafeInfo.put("phone", cafe.getPhone());
            cafeInfo.put("category_code", cafe.getCategoryCode());
            cafeInfo.put("kakaomap_url", cafe.getKakaomapUrl());
            cafeInfo.put("lat", cafe.getLat());
            cafeInfo.put("lon", cafe.getLon());

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("data", cafeInfo);

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.notFound().build();
    }
}