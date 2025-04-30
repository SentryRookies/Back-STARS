//추가
package com.example.placeservice.dto.restaurant;

import com.example.placeservice.entity.Restaurant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RestaurantDetailResponse {

    private Long   area_id;
    private String area_name;
    private String address;
    private String category_group_code;
    private String category_group_name;
    private String category_name;
    private String restaurant_id;
    private String phone;
    private String restaurant_name;
    private String place_url;
    private String road_address_name;
    private String lat;
    private String lon;
    private String categoryGroupName;
    private String categoryName;
    private String kakao_id;

    public static RestaurantDetailResponse fromEntity(Restaurant restaurant) {
        return RestaurantDetailResponse.builder()
                .area_id(restaurant.getArea().getAreaId())
                .area_name(restaurant.getArea().getName())
                .address(restaurant.getAddress())
                .category_group_code(restaurant.getCategory_code())
                .category_group_name(restaurant.getCategoryGroupName()) // 고정X (DB)
                .category_name(restaurant.getCategoryName()) // 고정X (DB)
                .restaurant_id(String.valueOf(restaurant.getRestaurantId()))
                .kakao_id(restaurant.getKakao_id())
                .phone(restaurant.getPhone())
                .restaurant_name(restaurant.getName())
                .place_url(restaurant.getKakaomap_url())
                .categoryGroupName(restaurant.getCategoryGroupName()) //  추가
                .categoryName(restaurant.getCategoryName()) //  추가
                .road_address_name(restaurant.getAddress()) // 현재 Address랑 Road Address가 같게 저장된 경우
                .lat(restaurant.getLat().toString())
                .lon(restaurant.getLon().toString())
                .build();
    }
}