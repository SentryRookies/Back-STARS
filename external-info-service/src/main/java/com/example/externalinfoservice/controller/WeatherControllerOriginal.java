//package com.example.externalinfoservice.controller;
//
//import com.example.externalinfoservice.service.WeatherService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/weather")
//@RequiredArgsConstructor
//public class WeatherControllerOriginal {
//    private final WeatherService weatherService;
//
//    @GetMapping("/{place-code}")
//    public String getWeatherData(@PathVariable("place-code") String placeCode) {
//        return weatherService.getWeatherData(placeCode);
//    }
//
//    @GetMapping(value = "/api/{place-code}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<String> getWeatherDataApi(@PathVariable("place-code") String placeCode) {
//        String jsonData = weatherService.getWeatherData(placeCode);
//        return ResponseEntity.ok(jsonData);
//    }
//}