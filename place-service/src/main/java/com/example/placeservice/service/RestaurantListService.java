package com.example.placeservice.service;

import com.example.placeservice.dto.restaurant.RestaurantDetailResponse;
import com.example.placeservice.dto.restaurant.RestaurantListResponse;
import com.example.placeservice.entity.Restaurant;
import com.example.placeservice.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantListService {

    private final RestaurantRepository restaurantRepository;

    @Transactional(readOnly = true)
    public List<RestaurantListResponse> getRestaurantList() {
        return restaurantRepository.findAll()
                .stream()
                .map(RestaurantListResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RestaurantDetailResponse getRestaurantDetail(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 음식점이 존재하지 않습니다. id=" + id));
        return RestaurantDetailResponse.fromEntity(restaurant);
    }
}