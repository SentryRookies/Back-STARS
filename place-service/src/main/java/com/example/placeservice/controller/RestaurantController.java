package com.example.placeservice.controller;


import com.example.placeservice.dto.restaurant.RestaurantDetailResponse;
import com.example.placeservice.dto.restaurant.RestaurantListResponse;
import com.example.placeservice.service.RestaurantListService;
import com.example.placeservice.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/main")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final RestaurantListService restaurantListService;

    // 전체 지역 기준으로 음식점 정보 저장
    @PostMapping("/fetch")
    public ResponseEntity<String> fetchRestaurants() {
        restaurantService.fetchAndSaveRestaurants();
        return ResponseEntity.ok("음식점 데이터 저장 완료!");
    }

    //음식점 목록 제공
    @GetMapping("/restaurant/list")
    public ResponseEntity<List<RestaurantListResponse>> getRestaurantList() {
        List<RestaurantListResponse> restaurantList = restaurantListService.getRestaurantList();
        return ResponseEntity.ok(restaurantList);
    }

    //음식점 상세 정보 제공
    @GetMapping("/info/restaurant/{restaurantId}")
    public ResponseEntity<RestaurantDetailResponse> getRestaurantDetail(@PathVariable Long restaurantId) {
        RestaurantDetailResponse restaurantDetail = restaurantListService.getRestaurantDetail(restaurantId);
        return ResponseEntity.ok(restaurantDetail);
    }
}
