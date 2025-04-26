package com.example.placeservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


@Service
@RequiredArgsConstructor
public class AttractionKakaoMapClient {

    @Value("${kakao.api.key}")
    private String kakaoApiKey;



    public JsonNode searchAttractionByKeyword(String keyword) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);

        // Kakao API 호출 URL 세팅
        String builder = UriComponentsBuilder
                .fromHttpUrl("https://dapi.kakao.com/v2/local/search/keyword.json")
                .queryParam("query",keyword)
                .queryParam("sort", "accuracy") // 정확도순 정렬
                .queryParam("page",1) // 요청 페이지
                .queryParam("size", 15)
                .build(false).toString(); // 한 페이지에 최대 15개 결과

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // REST API 호출
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                builder.toString(),
                HttpMethod.GET,
                entity,
                JsonNode.class
        );

        return response.getBody(); // JSON 응답 반환
    }

}
