package com.example.externalinfoservice.service;

import com.example.externalinfoservice.dto.WeatherRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class WeatherEsService {

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> getWeatherFromES(String area) {
        String url = "http://elasticsearch.seoultravel.life/seoul_citydata_weather/_search";

        Map<String, Object> term = Map.of("weather.area_nm.keyword", area);
        Map<String, Object> query = Map.of("term", term);
        Map<String, Object> body = Map.of("size", 1, "query", query);

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

}
