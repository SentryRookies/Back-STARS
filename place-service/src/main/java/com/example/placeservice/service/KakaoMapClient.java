package com.example.placeservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class KakaoMapClient {

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    public JsonNode searchRestaurantsByCategory(BigDecimal lon, BigDecimal lat, int page) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);

        // Kakao API 호출 URL 세팅
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl("https://dapi.kakao.com/v2/local/search/category.json")
                .queryParam("category_group_code", "FD6") // FD6: 음식점 카테고리
                .queryParam("x", lon) // x: 경도(lon)
                .queryParam("y", lat) // y: 위도(lat)
                .queryParam("sort", "accuracy") // 정확도순 정렬
                .queryParam("page", page) // 요청 페이지
                .queryParam("size", 15); // 한 페이지에 최대 15개 결과

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // REST API 호출
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                JsonNode.class
        );

        return response.getBody(); // JSON 응답 반환
    }

}
