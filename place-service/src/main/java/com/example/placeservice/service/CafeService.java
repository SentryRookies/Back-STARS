package com.example.placeservice.service;

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
import java.util.List;
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
     * 특정 ID 목록의 장소만 처리
     */
    @Async
    public void processAreasByIds(List<Long> areaIds) {
        log.info("{}개 지정된 장소에 대한 처리를 시작합니다.", areaIds.size());

        for (int i = 0; i < areaIds.size(); i++) {
            Long areaId = areaIds.get(i);
            Area area = areaRepository.findById(areaId).orElse(null);

            if (area == null) {
                log.warn("ID {}인 장소를 찾을 수 없습니다.", areaId);
                continue;
            }

            log.info("[{}/{}] '{}' (ID: {}) 처리 중...",
                    i + 1, areaIds.size(), area.getName(), area.getAreaId());

            processSingleArea(area);

            // API 호출 간격 조절
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        log.info("지정된 모든 장소 처리 완료.");
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
            double latitude = area.getY().doubleValue();
            double longitude = area.getX().doubleValue();

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

            log.info("'{}' 주변 {}개의 카페 중 {}개의 '음식점 > 카페' 카테고리를 찾았습니다.",
                    area.getName(), cafes.size(), filteredCafes.size());

            // 필터링된 카페 정보 저장
            for (KakaoApiService.CafeResponse.Document cafeDoc : filteredCafes) {
                Cafe cafe = Cafe.builder()
                        .name(cafeDoc.getPlaceName())
                        .address(cafeDoc.getAddressName())
                        .lat(new BigDecimal(cafeDoc.getX()))
                        .lon(new BigDecimal(cafeDoc.getY()))
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
}