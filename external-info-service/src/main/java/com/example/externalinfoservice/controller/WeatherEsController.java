package com.example.externalinfoservice.controller;

import com.example.externalinfoservice.dto.WeatherRequest;
import com.example.externalinfoservice.service.WeatherEsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/main/info")
public class WeatherEsController {

    private final WeatherEsService weatherEsService;

    @GetMapping("/weather")
    public ResponseEntity<Map<String, Object>> getWeatherByArea(@RequestParam String area) {
        Map<String, Object> weather = weatherEsService.getWeatherFromES(area);
        return weather != null
                ? ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(weather)
                : ResponseEntity.notFound().build();
    }
}
