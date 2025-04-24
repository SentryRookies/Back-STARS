package com.example.externalinfoservice.controller;

import com.example.externalinfoservice.service.WeatherEsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/main/info/weather")
public class WeatherEsController {

    private final WeatherEsService weatherEsService;

    @GetMapping(value = {"/{placeCode}", ""}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getWeather(@PathVariable(name = "placeCode", required = false) String placeCode) {
        if (placeCode == null || placeCode.isBlank()) {
            List<Map<String, Object>> allWeather = weatherEsService.getAllWeatherFromES();
            return ResponseEntity.ok(allWeather);
        }

        Map<String, Object> weather = weatherEsService.getWeatherFromES(placeCode);
        return weather != null ? ResponseEntity.ok(weather) : ResponseEntity.notFound().build();
    }
}
