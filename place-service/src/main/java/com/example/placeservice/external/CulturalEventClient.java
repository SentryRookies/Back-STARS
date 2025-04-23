package com.example.placeservice.external;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CulturalEventClient {

    private final RestTemplate restTemplate;

    public CulturalEventClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // API에서 1000개씩 데이터를 받아오는 메서드
    public String fetchEventData(int startIndex, int endIndex) {
        String url = String.format("http://openapi.seoul.go.kr:8088/7669764c417069613736734567476c/json/culturalEventInfo/%d/%d/", startIndex, endIndex);
        return restTemplate.getForObject(url, String.class);
    }
}
