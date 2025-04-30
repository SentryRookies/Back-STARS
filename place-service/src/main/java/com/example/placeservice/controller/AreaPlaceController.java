package com.example.placeservice.controller;

import com.example.placeservice.entity.*;
import com.example.placeservice.repository.AccommodationRepository;
import com.example.placeservice.repository.AreaRepository;
import com.example.placeservice.repository.AttractionRepository;
import com.example.placeservice.repository.CafeRepository;
import com.example.placeservice.repository.CulturalEventRepository;
import com.example.placeservice.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/main")
@RequiredArgsConstructor
public class AreaPlaceController {

    private final AreaRepository areaRepository;
    private final CafeRepository cafeRepository;
    private final RestaurantRepository restaurantRepository;
    private final AttractionRepository attractionRepository;
    private final AccommodationRepository accommodationRepository;
    private final CulturalEventRepository culturalEventRepository;

    /**
     * 지역 ID를 기준으로 모든 장소 정보(카페, 음식점, 관광지, 숙박, 행사)를 한번에 조회
     */
    @GetMapping("/place/list/{areaId}")
    public ResponseEntity<List<Map<String, Object>>> getAllPlacesByAreaId(@PathVariable Long areaId) {
        log.info("지역 ID {}의 모든 장소 정보 통합 조회", areaId);

        // 지역 정보 조회
        Optional<Area> areaOptional = areaRepository.findById(areaId);
        if (!areaOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        // 배열 형태로 응답 변경
        List<Map<String, Object>> response = new ArrayList<>();

        // 카페 정보
        Map<String, Object> cafeResponse = new LinkedHashMap<>();
        cafeResponse.put("type", "cafe");

        List<Map<String, Object>> cafeList = new ArrayList<>();
        List<Cafe> cafes = cafeRepository.findByAreaId(areaId);

        for (Cafe cafe : cafes) {
            Map<String, Object> cafeMap = new LinkedHashMap<>();
            cafeMap.put("id", cafe.getId());
            cafeMap.put("name", cafe.getName());
            cafeMap.put("address", cafe.getAddress());
            cafeMap.put("phone", cafe.getPhone());
            cafeMap.put("category_code", cafe.getCategoryCode());
            cafeMap.put("kakaomap_url", cafe.getKakaomapUrl());
            cafeMap.put("lat", cafe.getLat());
            cafeMap.put("lon", cafe.getLon());
            cafeList.add(cafeMap);
        }

        cafeResponse.put("content", cafeList);
        response.add(cafeResponse); // 배열에 추가

        // 음식점 정보
        Map<String, Object> restaurantResponse = new LinkedHashMap<>();
        restaurantResponse.put("type", "restaurant");

        List<Map<String, Object>> restaurantList = new ArrayList<>();
        List<Restaurant> restaurants = restaurantRepository.findByAreaAreaId(areaId);

        for (Restaurant restaurant : restaurants) {
            Map<String, Object> restaurantMap = new LinkedHashMap<>();
            restaurantMap.put("id", restaurant.getRestaurantId());
            restaurantMap.put("name", restaurant.getName());
            restaurantMap.put("address", restaurant.getAddress());
            restaurantMap.put("phone", restaurant.getPhone());
            restaurantMap.put("category_code", restaurant.getCategory_code());
            restaurantMap.put("category_group_name", restaurant.getCategoryGroupName());
            restaurantMap.put("category_name", restaurant.getCategoryName());
            restaurantMap.put("kakaomap_url", restaurant.getKakaomap_url());
            restaurantMap.put("lat", restaurant.getLat());
            restaurantMap.put("lon", restaurant.getLon());
            restaurantList.add(restaurantMap);
        }

        restaurantResponse.put("content", restaurantList);
        response.add(restaurantResponse); // 배열에 추가

        // 관광지 정보
        Map<String, Object> attractionResponse = new LinkedHashMap<>();
        attractionResponse.put("type", "attraction");

        List<Map<String, Object>> attractionList = new ArrayList<>();
        List<Attraction> attractions = attractionRepository.findByAreaAreaId(areaId);

        for (Attraction attraction : attractions) {
            Map<String, Object> attractionMap = new LinkedHashMap<>();
            attractionMap.put("id", attraction.getAttractionId());
            attractionMap.put("seoul_attraction_id", attraction.getSeoulAttractionId());
            attractionMap.put("name", attraction.getName());
            attractionMap.put("address", attraction.getAddress());
            attractionMap.put("phone", attraction.getPhone());
            attractionMap.put("homepage_url", attraction.getHomepageUrl());
            attractionMap.put("close_day", attraction.getCloseDay());
            attractionMap.put("use_time", attraction.getUseTime());
            attractionMap.put("kakaomap_url", attraction.getKakaomapUrl());
            attractionMap.put("lat", attraction.getLat());
            attractionMap.put("lon", attraction.getLon());
            attractionList.add(attractionMap);
        }

        attractionResponse.put("content", attractionList);
        response.add(attractionResponse); // 배열에 추가

        // 숙박시설 정보
        Map<String, Object> accommodationResponse = new LinkedHashMap<>();
        accommodationResponse.put("type", "accommodation");

        List<Map<String, Object>> accommodationList = new ArrayList<>();
        List<Accommodation> accommodations = accommodationRepository.findByAreaId(areaId);

        for (Accommodation accommodation : accommodations) {
            Map<String, Object> accommodationMap = new LinkedHashMap<>();
            accommodationMap.put("id", accommodation.getAccommodationId());
            accommodationMap.put("name", accommodation.getName());
            accommodationMap.put("address", accommodation.getAddress());
            accommodationMap.put("phone", accommodation.getPhone());
            accommodationMap.put("gu", accommodation.getGu());
            accommodationMap.put("type", accommodation.getType());
            accommodationMap.put("kakaomap_url", accommodation.getKakaomapUrl());
            accommodationMap.put("lat", accommodation.getLat());
            accommodationMap.put("lon", accommodation.getLon());
            accommodationList.add(accommodationMap);
        }

        accommodationResponse.put("content", accommodationList);
        response.add(accommodationResponse); // 배열에 추가

        // 문화행사 정보
        Map<String, Object> eventResponse = new LinkedHashMap<>();
        eventResponse.put("type", "cultural_event");

        List<Map<String, Object>> eventList = new ArrayList<>();
        List<CulturalEvent> events = culturalEventRepository.findByAreaAreaId(areaId);

        for (CulturalEvent event : events) {
            Map<String, Object> eventMap = new LinkedHashMap<>();
            eventMap.put("id", event.getEventId());
            eventMap.put("category", event.getCategory());
            eventMap.put("title", event.getTitle());
            eventMap.put("address", event.getAddress());
            eventMap.put("target", event.getTarget());
            eventMap.put("event_fee", event.getEventFee());
            eventMap.put("event_img", event.getEventImg());
            eventMap.put("start_date", event.getStartDate());
            eventMap.put("end_date", event.getEndDate());
            eventMap.put("lat", event.getLat());
            eventMap.put("lon", event.getLon());
            eventList.add(eventMap);
        }

        eventResponse.put("content", eventList);
        response.add(eventResponse); // 배열에 추가

        return ResponseEntity.ok(response);
    }
}