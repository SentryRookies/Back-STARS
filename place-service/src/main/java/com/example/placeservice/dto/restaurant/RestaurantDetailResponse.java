//추가
package com.example.placeservice.dto.restaurant;

import com.example.placeservice.entity.Restaurant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RestaurantDetailResponse {

    private String address_name;
    private String category_group_code;
    private String category_group_name; // 고정 X, DB 값
    private String category_name;        // 고정 X, DB 값
    private String distance; // 빈 값 유지
    private String id;
    private String phone;
    private String place_name;
    private String place_url;
    private String road_address_name;
    private String lat;
    private String lon;
    private String categoryGroupName; // 추가
    private String categoryName; // 추가

    private String kakao_id;


    public static RestaurantDetailResponse fromEntity(Restaurant restaurant) {
        return RestaurantDetailResponse.builder()
                .address_name(restaurant.getAddress())
                .category_group_code(restaurant.getCategory_code())
                .category_group_name(restaurant.getCategoryGroupName()) // 고정X (DB)
                .category_name(restaurant.getCategoryName()) // 고정X (DB)
                .distance("") // 그대로 비워두기
                .id(String.valueOf(restaurant.getRestaurantId()))
                .kakao_id(restaurant.getKakao_id())
                .phone(restaurant.getPhone())
                .place_name(restaurant.getName())
                .place_url(restaurant.getKakaomap_url())
                .categoryGroupName(restaurant.getCategoryGroupName()) //  추가
                .categoryName(restaurant.getCategoryName()) //  추가
                .road_address_name(restaurant.getAddress()) // 현재 Address랑 Road Address가 같게 저장된 경우
                .lat(restaurant.getLat().toString())
                .lon(restaurant.getLon().toString())
                .build();
    }
}