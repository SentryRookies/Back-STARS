package com.example.placeservice.init;

import com.example.placeservice.repository.CafeRepository;
import com.example.placeservice.repository.RestaurantRepository;
import com.example.placeservice.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

// SB 실행 시, attraction data 저장(area 데이터가 먼저 저장되어 있어야 함)
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInit implements CommandLineRunner {

    private final AccommodationService accommodationService;
    private final AttractionService attractionService;
    private final CulturalEventService culturalEventService;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantService restaurantService;
    private final CafeRepository cafeRepository;
    private final CafeService cafeService;

    private final JdbcTemplate jdbcTemplate;


    @Override
    public void run(String... args) throws Exception {
        // 1. data.sql 파일 실행(지역 데이터)
        try {
            ClassPathResource resource = new ClassPathResource("data.sql");

            // 파일 내용을 문자열로 읽기
            String sql = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

            // SQL 실행
            jdbcTemplate.execute(sql);
            log.info("data.sql 실행 완료");

        } catch (IOException e) {
            log.error("area 데이터 초기화 시 오류 발생 : {}",e.getMessage());
        }

        // 2. 장소 데이터 삽입
        accommodationService.saveAccommodations();
        attractionService.fetchDataFromVisitSeoul();
        culturalEventService.fetchAndSaveAllEvents();

        if (restaurantRepository.count() == 0) {
            log.info("음식점 데이터 저장 시작");
            restaurantService.fetchAndSaveRestaurants();
            log.info("음식점 데이터 저장 완료");
        } else {
            log.info("음식점 데이터가 이미 존재합니다. 건너뜁니다.");
        }

        // 카페 데이터 초기화
        if (cafeRepository.count() == 0) {
            log.info("카페 데이터 저장 시작");
            cafeService.processAllAreas();
            log.info("카페 데이터 저장 완료");
        } else {
            log.info("카페 데이터가 이미 존재합니다. 건너뜁니다.");
        }

    }

}
