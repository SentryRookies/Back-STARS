package com.example.externalinfoservice.controller;

import com.example.externalinfoservice.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/weather")
@RequiredArgsConstructor
public class WeatherController {
    private final WeatherService weatherService;

    /**
     * 전체 지역의 간략한 날씨 정보를 조회합니다.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAllAreasWeatherSummary() {
        String jsonData = weatherService.getAllAreasWeatherSummary();
        return ResponseEntity.ok(jsonData);
        }

    /**
     * 지역별 상세 날씨 정보를 조회합니다.
     */
    @GetMapping(value = "/{place-code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getWeatherDataApi(@PathVariable("place-code") String placeCode) {
        String jsonData = weatherService.getWeatherData(placeCode);
        return ResponseEntity.ok(jsonData);
    }
}