package com.example.placeservice.service;

import com.example.placeservice.dto.CafeDto;
import com.example.placeservice.entity.Area;
import com.example.placeservice.entity.Cafe;
import com.example.placeservice.repository.AreaRepository;
import com.example.placeservice.repository.CafeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CafeService {

    private final AreaRepository areaRepository;
    private final CafeRepository cafeRepository;
    private final KakaoApiService kakaoApiService;

    /**
     * 모든 장소에 대해 주변 카페 데이터 처리
     */
    @Async
    public void processAllAreas() {
        List<Area> areas = areaRepository.findAll();
        log.info("총 {}개 장소에 대한 카페 정보를 처리합니다.", areas.size());

        for (int i = 0; i < areas.size(); i++) {
            Area area = areas.get(i);
            log.info("[{}/{}] '{}' (ID: {}) 처리 중...",
                    i + 1, areas.size(), area.getName(), area.getAreaId());

            processSingleArea(area);

            // API 호출 간격 조절
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        log.info("모든 장소 처리 완료.");
    }

    /**
     * 단일 장소의 주변 카페 정보 처리
     */
    @Transactional
    public void processSingleArea(Area area) {
        try {
            // 해당 지역의 기존 카페 데이터 삭제
            cafeRepository.deleteByAreaId(area.getAreaId());

            // 주변 카페 검색
            double latitude = area.getLat().doubleValue();
            double longitude = area.getLon().doubleValue();

            if (latitude == 0 || longitude == 0) {
                log.warn("장소 ID {}의 위도/경도 정보가 없습니다.", area.getAreaId());
                return;
            }

            List<KakaoApiService.CafeResponse.Document> cafes =
                    kakaoApiService.findCafesNearby(latitude, longitude, 1000);

            // "음식점 > 카페"가 포함된 카페만 필터링하고 "(휴업중)" 카페는 제외
            List<KakaoApiService.CafeResponse.Document> filteredCafes = cafes.stream()
                    .filter(cafe -> cafe.getCategoryName() != null && cafe.getCategoryName().contains("음식점 > 카페"))
                    .filter(cafe -> cafe.getPlaceName() == null || !cafe.getPlaceName().contains("(휴업중)"))
                    .collect(Collectors.toList());

            log.info("'{}' 주변 총 {}개의 카페 중 {}개의 '음식점 > 카페' 카테고리(휴업중 제외)를 찾았습니다.",
                    area.getName(), cafes.size(), filteredCafes.size());

            // 필터링된 카페 정보 저장
            for (KakaoApiService.CafeResponse.Document cafeDoc : filteredCafes) {
                Cafe cafe = Cafe.builder()
                        .name(cafeDoc.getPlaceName())
                        .address(cafeDoc.getAddressName())
                        .lat(new BigDecimal(cafeDoc.getY()))
                        .lon(new BigDecimal(cafeDoc.getX()))
                        .phone(cafeDoc.getPhone())
                        .kakaomapUrl(cafeDoc.getPlaceUrl())
                        .categoryCode(cafeDoc.getCategoryGroupCode())
                        .area(area)
                        .build();

                cafeRepository.save(cafe);
            }
        } catch (Exception e) {
            log.error("장소 ID {} 처리 중 오류: {}", area.getAreaId(), e.getMessage(), e);
        }
    }

    /**
     * 특정 지역의 카페 목록 조회
     */
    @Transactional(readOnly = true)
    public List<CafeDto> getCafesByAreaId(Long areaId) {
        List<Cafe> cafes = cafeRepository.findByAreaId(areaId);
        return cafes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 모든 카페 목록 조회
     */
    @Transactional(readOnly = true)
    public List<CafeDto> getAllCafes() {
        List<Cafe> cafes = cafeRepository.findAll();
        return cafes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * ID, 이름, 위도, 경도만 포함한 모든 카페 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllCafesWithLimitedInfo() {
        List<Cafe> cafes = cafeRepository.findAll();
        return cafes.stream()
                .map(cafe -> {
                    Map<String, Object> cafeMap = new HashMap<>();
                    cafeMap.put("id", cafe.getId());
                    cafeMap.put("name", cafe.getName());
                    cafeMap.put("lat", cafe.getLat());
                    cafeMap.put("lon", cafe.getLon());
                    return cafeMap;
                })
                .collect(Collectors.toList());
    }

    /**
     * Cafe 엔티티를 DTO로 변환
     */
    private CafeDto convertToDto(Cafe cafe) {
        return CafeDto.builder()
                .id(cafe.getId())
                .name(cafe.getName())
                .address(cafe.getAddress())
                .lat(cafe.getLat())
                .lon(cafe.getLon())
                .phone(cafe.getPhone())
                .kakaomapUrl(cafe.getKakaomapUrl())
                .categoryCode(cafe.getCategoryCode())
                .build();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getCafeDetailByPlaceCode(String placeCode) {
        Optional<Cafe> cafeOptional = cafeRepository.findByKakaomapUrl(placeCode);

        if (cafeOptional.isPresent()) {
            Cafe cafe = cafeOptional.get();
            Map<String, Object> cafeInfo = new HashMap<>();
            cafeInfo.put("name", cafe.getName());
            cafeInfo.put("address", cafe.getAddress());
            cafeInfo.put("phone", cafe.getPhone());
            cafeInfo.put("kakaomap_url", cafe.getKakaomapUrl());
            cafeInfo.put("lat", cafe.getLat());
            cafeInfo.put("lon", cafe.getLon());
            cafeInfo.put("category_code", cafe.getCategoryCode());

            return cafeInfo;
        }

        return null;
    }
}