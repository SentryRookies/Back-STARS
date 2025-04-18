package com.example.placeservice.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoApiService {

    private final RestTemplate restTemplate;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    private static final String KAKAO_API_BASE_URL = "https://dapi.kakao.com/v2/local";

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CafeResponse {
        private Meta meta;
        private List<Document> documents;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Meta {
            @JsonProperty("total_count")
            private int totalCount;
            @JsonProperty("pageable_count")
            private int pageableCount;
            @JsonProperty("is_end")
            private boolean isEnd;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Document {
            @JsonProperty("id")
            private String id;
            @JsonProperty("place_name")
            private String placeName;
            @JsonProperty("category_name")
            private String categoryName;
            @JsonProperty("category_group_code")
            private String categoryGroupCode;
            @JsonProperty("category_group_name")
            private String categoryGroupName;
            @JsonProperty("phone")
            private String phone;
            @JsonProperty("address_name")
            private String addressName;
            @JsonProperty("road_address_name")
            private String roadAddressName;
            private String x;  // 경도
            private String y;  // 위도
            @JsonProperty("place_url")
            private String placeUrl;
            private String distance;
        }
    }

    // 위도/경도 기준으로 주변 카페 검색
    public List<CafeResponse.Document> findCafesNearby(double latitude, double longitude, int radius) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoApiKey);

            String url = UriComponentsBuilder.fromHttpUrl(KAKAO_API_BASE_URL + "/search/category.json")
                    .queryParam("category_group_code", "CE7")  // 카페 카테고리 코드
                    .queryParam("x", longitude)
                    .queryParam("y", latitude)
                    .queryParam("radius", radius)  // 반경(미터)
                    .queryParam("size", 15)        // 최대 15개 결과
                    .build()
                    .toUriString();

            ResponseEntity<CafeResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    CafeResponse.class
            );

            CafeResponse result = response.getBody();
            if (result != null && result.getDocuments() != null) {
                return result.getDocuments();
            }

            return Collections.emptyList();
        } catch (Exception e) {
            log.error("카페 검색 중 오류: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}