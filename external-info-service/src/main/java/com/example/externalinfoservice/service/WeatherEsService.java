package com.example.externalinfoservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class WeatherEsService {

        
    private final RestTemplate restTemplate = new RestTemplate();

    // 특정 지역 날씨
    public Map<String, Object> getWeatherFromES(String area) {
        String url = "http://elasticsearch.seoultravel.life/seoul_citydata_weather_20250424/_search";

        Map<String, Object> term = Map.of("weather.area_nm.keyword", area);
        Map<String, Object> query = Map.of("term", term);
        Map<String, Object> body = Map.of("size", 100, "query", query);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        List<Map<String, Object>> hits = (List<Map<String, Object>>)
                ((Map<String, Object>) response.getBody().get("hits")).get("hits");

        if (hits.isEmpty()) return null;

        Map<String, Object> source = (Map<String, Object>) hits.get(0).get("_source");
        return (Map<String, Object>) source.get("weather");
    }

    // 전체 지역 날씨
    public List<Map<String, Object>> getAllWeatherFromES() {
        String url = "http://elasticsearch.seoultravel.life/seoul_citydata_weather_20250424/_search";

        Map<String, Object> body = Map.of(
                "size", 0,
                "aggs", Map.of(
                        "by_area", Map.of(
                                "terms", Map.of("field", "weather.area_nm.keyword", "size", 1000),
                                "aggs", Map.of(
                                        "latest_hit", Map.of(
                                                "top_hits", Map.of(
                                                        "size", 1,
                                                        "sort", List.of(Map.of("weather.weather_time", Map.of("order", "desc")))
                                                )
                                        )
                                )
                        )
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        Map<String, Object> aggregations = (Map<String, Object>) response.getBody().get("aggregations");
        Map<String, Object> byArea = (Map<String, Object>) aggregations.get("by_area");
        List<Map<String, Object>> buckets = (List<Map<String, Object>>) byArea.get("buckets");

        List<Map<String, Object>> results = new ArrayList<>();
        for (Map<String, Object> bucket : buckets) {
            Map<String, Object> latestHitWrapper = (Map<String, Object>) bucket.get("latest_hit");
            Map<String, Object> hitsWrapper = (Map<String, Object>) latestHitWrapper.get("hits");
            List<Map<String, Object>> hits = (List<Map<String, Object>>) hitsWrapper.get("hits");
            if (hits.isEmpty()) continue;

            Map<String, Object> latestHit = hits.get(0);
            Map<String, Object> source = (Map<String, Object>) latestHit.get("_source");
            results.add((Map<String, Object>) source.get("weather"));
        }

        return results;
    }

}
