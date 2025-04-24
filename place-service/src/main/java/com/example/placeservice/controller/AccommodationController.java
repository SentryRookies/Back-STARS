package com.example.placeservice.controller;

import com.example.placeservice.dto.AccommodationResponse;
import com.example.placeservice.service.AccommodationSaveService;
import com.example.placeservice.service.AccommodationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@RestController
@RequestMapping("/main/accommodation")
public class AccommodationController {
    private final AccommodationService accommodationService;
    private final AccommodationSaveService accommodationSaveService;

    // 생성자 주입
    public AccommodationController(AccommodationService accommodationService, AccommodationSaveService accommodationSaveService) {
        this.accommodationService = accommodationService;
        this.accommodationSaveService = accommodationSaveService;
    }

    @GetMapping("/list")
    public List<AccommodationResponse.Body.Items.Item> getAccommodations() throws IOException {
        return accommodationService.getAllAccommodationData();
    }
}
