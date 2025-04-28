package com.example.placeservice.dto.restaurant;
//추가
import com.example.placeservice.entity.Restaurant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RestaurantListResponse {
    private Long id;
    private String name;
    private String lat;
    private String lon;

    public static RestaurantListResponse fromEntity(Restaurant restaurant) {
        return RestaurantListResponse.builder()
                .id(restaurant.getRestaurantId())
                .name(restaurant.getName())
                .lat(restaurant.getLat().toString())
                .lon(restaurant.getLon().toString())
                .build();
    }
}
