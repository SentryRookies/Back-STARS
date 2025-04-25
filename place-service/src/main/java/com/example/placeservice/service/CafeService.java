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

    // 모든 지역에 대한 카페 처리가 완료되었는지 확인하는 플래그
    private boolean processingCompleted = false;

    /**
     * 모든 장소에 대해 주변 카페 데이터 처리
     */
    @Async
    @Transactional
    public void processAllAreas() {
        log.info("processAllAreas() 메서드 시작");

        // 이미 처리가 완료되었거나 이미 데이터가 있으면 다시 실행하지 않음
        if (processingCompleted) {
            log.info("이미 처리가 완료되었습니다. 중복 실행 방지를 위해 종료합니다.");
            return;
        }

        long cafeCount = cafeRepository.count();
        log.info("현재 DB에 저장된 카페 수: {}", cafeCount);

        if (cafeCount > 0) {
            log.info("카페 데이터가 이미 존재합니다. 처리를 건너뜁니다.");
            processingCompleted = true;
            return;
        }

        try {
            List<Area> areas = areaRepository.findAll();
            log.info("총 {}개 장소에 대한 카페 정보를 처리합니다.", areas.size());

            // 다른 처리 중 중복 실행 방지
            processingCompleted = true;

            log.info("지역별 카페 정보 처리 시작");
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
                    log.error("스레드 인터럽트 발생", e);
                }
            }

            log.info("모든 장소 처리 완료. 총 처리된 지역: {}", areas.size());

            // 최종 카페 수 확인
            log.info("최종 저장된 카페 수: {}", cafeRepository.count());

        } catch (Exception e) {
            processingCompleted = false; // 오류 발생 시 플래그 재설정
            log.error("카페 데이터 처리 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 단일 장소의 주변 카페 정보 처리
     */
    @Transactional
    public void processSingleArea(Area area) {
        log.info("processSingleArea() 시작 - 지역: '{}' (ID: {})", area.getName(), area.getAreaId());

        try {
            // 해당 지역의 기존 카페 데이터 삭제
            log.info("지역 ID {}의 기존 카페 데이터 삭제 시도", area.getAreaId());
            cafeRepository.deleteByAreaId(area.getAreaId());
            log.info("지역 ID {}의 기존 카페 데이터 삭제 완료", area.getAreaId());

            // 주변 카페 검색
            double latitude = area.getLat().doubleValue();
            double longitude = area.getLon().doubleValue();

            if (latitude == 0 || longitude == 0) {
                log.warn("장소 ID {}의 위도/경도 정보가 없습니다. 위도: {}, 경도: {}",
                        area.getAreaId(), latitude, longitude);
                return;
            }

            log.info("카카오 API 호출 - 위도: {}, 경도: {}, 반경: 1000m", latitude, longitude);
            List<KakaoApiService.CafeResponse.Document> cafes =
                    kakaoApiService.findCafesNearby(latitude, longitude, 1000);
            log.info("카카오 API 응답 - 총 {}개의 장소 정보 수신", cafes.size());

            // "음식점 > 카페"가 포함된 카페만 필터링하고 "(휴업중)" 카페는 제외
            List<KakaoApiService.CafeResponse.Document> filteredCafes = cafes.stream()
                    .filter(cafe -> {
                        boolean categoryMatch = cafe.getCategoryName() != null &&
                                cafe.getCategoryName().contains("음식점 > 카페");
                        if (!categoryMatch) {
                            log.debug("카테고리 불일치로 제외: {}, 카테고리: {}",
                                    cafe.getPlaceName(), cafe.getCategoryName());
                        }
                        return categoryMatch;
                    })
                    .filter(cafe -> {
                        boolean notClosed = cafe.getPlaceName() == null ||
                                !cafe.getPlaceName().contains("(휴업중)");
                        if (!notClosed) {
                            log.debug("휴업중으로 제외: {}", cafe.getPlaceName());
                        }
                        return notClosed;
                    })
                    .collect(Collectors.toList());

            log.info("'{}' 주변 총 {}개의 카페 중 {}개의 '음식점 > 카페' 카테고리(휴업중 제외)를 찾았습니다.",
                    area.getName(), cafes.size(), filteredCafes.size());

            // 최대 15개의 카페만 저장 (음식점과 동일한 로직 적용)
            int savedCount = 0;
            int maxCafesToSave = 15;

            // 필터링된 카페 정보 저장
            for (KakaoApiService.CafeResponse.Document cafeDoc : filteredCafes) {
                // 최대 카페 저장 개수 제한
                if (savedCount >= maxCafesToSave) {
                    log.info("최대 저장 개수({})에 도달하여 저장 중단", maxCafesToSave);
                    break;
                }

                log.info("카페 저장 시도: {}, URL: {}", cafeDoc.getPlaceName(), cafeDoc.getPlaceUrl());

                // 이미 동일한 카페가 있는지 확인 (kakaomapUrl로 중복 체크)
                Optional<Cafe> existingCafe = cafeRepository.findByKakaomapUrl(cafeDoc.getPlaceUrl());
                if (existingCafe.isPresent()) {
                    log.info("이미 존재하는 카페이므로 건너뜀: {}, ID: {}",
                            existingCafe.get().getName(), existingCafe.get().getId());
                    continue; // 이미 존재하는 카페는 건너뜀
                }

                try {
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

                    log.info("카페 객체 생성 완료: {}", cafe.getName());
                    Cafe savedCafe = cafeRepository.save(cafe);
                    log.info("카페 저장 완료: {}, 저장된 ID: {}", savedCafe.getName(), savedCafe.getId());

                    savedCount++;
                } catch (Exception e) {
                    log.error("카페 저장 중 오류 발생: {}, 오류: {}", cafeDoc.getPlaceName(), e.getMessage(), e);
                }
            }

            log.info("지역 '{}' (ID: {})에 총 {}개의 카페 저장 완료",
                    area.getName(), area.getAreaId(), savedCount);

        } catch (Exception e) {
            log.error("장소 ID {} 처리 중 오류: {}", area.getAreaId(), e.getMessage(), e);
            throw e; // 상위 메서드에서 처리할 수 있도록 예외 전파
        }
    }

    /**
     * 특정 지역의 카페 목록 조회
     */
    @Transactional(readOnly = true)
    public List<CafeDto> getCafesByAreaId(Long areaId) {
        log.info("지역 ID {}의 카페 목록 조회", areaId);
        List<Cafe> cafes = cafeRepository.findByAreaId(areaId);
        log.info("지역 ID {}에서 총 {}개의 카페 찾음", areaId, cafes.size());

        return cafes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 모든 카페 목록 조회
     */
    @Transactional(readOnly = true)
    public List<CafeDto> getAllCafes() {
        log.info("모든 카페 목록 조회");
        List<Cafe> cafes = cafeRepository.findAll();
        log.info("총 {}개의 카페 조회됨", cafes.size());

        return cafes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * ID, 이름, 위도, 경도만 포함한 모든 카페 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllCafesWithLimitedInfo() {
        log.info("제한된 정보로 모든 카페 목록 조회");
        List<Cafe> cafes = cafeRepository.findAll();
        log.info("총 {}개의 카페 조회됨", cafes.size());

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
        log.info("장소 코드로 카페 상세 정보 조회: {}", placeCode);
        Optional<Cafe> cafeOptional = cafeRepository.findByKakaomapUrl(placeCode);

        if (cafeOptional.isPresent()) {
            Cafe cafe = cafeOptional.get();
            log.info("카페 찾음: {}, ID: {}", cafe.getName(), cafe.getId());

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

        log.info("장소 코드 {}에 해당하는 카페를 찾을 수 없음", placeCode);
        return null;
    }
}